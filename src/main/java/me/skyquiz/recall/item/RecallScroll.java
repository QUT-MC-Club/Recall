package me.skyquiz.recall.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import me.skyquiz.recall.Recall;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;
import java.util.logging.Logger;

public class RecallScroll extends Item implements PolymerItem {
    private static final float CHARGE_PROGRESS = 0.2F;

    public RecallScroll(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();

        if (!world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            return super.useOnBlock(context);
        }

        world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        boolean bl = !playerEntity.isInCreativeMode() && itemStack.getCount() == 1;
        LodestoneTrackerComponent lodestoneTrackerComponent = new LodestoneTrackerComponent(Optional.of(GlobalPos.create(world.getRegistryKey(), blockPos)), true);
        if (bl) {
            itemStack.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent);
        } else {
            ItemStack itemStack2 = itemStack.copyComponentsToNewStack(Recall.RECALL_SCROLL, 1);
            itemStack.decrementUnlessCreative(1, playerEntity);
            itemStack2.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent);
            if (!playerEntity.getInventory().insertStack(itemStack2)) {
                playerEntity.dropItem(itemStack2, false);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean pointSet = itemStack.get(DataComponentTypes.LODESTONE_TRACKER) != null;
        if (pointSet) {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Recall.LOGGER.info(String.valueOf(remainingUseTicks));
        ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(user.getUuid());
        if (player == null) return false;

        // Getting destination teleport location
        LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        if (lodestoneTrackerComponent == null) return super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (lodestoneTrackerComponent.target().isEmpty ()) {
            stack.set(DataComponentTypes.LODESTONE_TRACKER, null);
            return false;
        }

        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i);

        if ((double) f < 0.9) {
            return false;
        }

        GlobalPos homeRef = lodestoneTrackerComponent.target().get();
        BlockPos home = homeRef.pos().mutableCopy();
        ServerWorld homeWorld = world.getServer().getWorld(homeRef.dimension());

        // Sanity Check
        if (homeWorld == null) homeWorld = world.getServer().getWorld(player.getServerWorld().getRegistryKey());
        assert homeWorld != null;
        if (home == null) home = player.getServerWorld().getSpawnPos();
        if (!homeWorld.getBlockState(home).isOf(Blocks.LODESTONE)) home = player.getServerWorld().getSpawnPos();
        if (!homeWorld.getBlockState(home).isOf(Blocks.AIR)) home = player.getServerWorld().getSpawnPos();


        // Teleport Protocol
        world.sendEntityStatus(player, (byte) 46);
        player.teleport(homeWorld, home.toBottomCenterPos().getX(), home.toBottomCenterPos().getY(), home.toBottomCenterPos().getZ(), PositionFlag.DELTA, player.getYaw(), player.getPitch(), false);
        homeWorld.sendEntityStatus(player, (byte) 46);
        stack.decrementUnlessCreative(1, user);

        return true;
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
