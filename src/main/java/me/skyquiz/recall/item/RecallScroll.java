package me.skyquiz.recall.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import me.skyquiz.recall.component.ModComponents;
import me.skyquiz.recall.component.RecallPointComponent;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class RecallScroll extends Item implements PolymerItem {
    private static final float CHARGE_PROGRESS = 0.2F;
    private static final float LOAD_PROGRESS = 0.5F;
    private static final float DEFAULT_SPEED = 3.15F;
    private static final float DEFAULT_PULL_TIME = 1.25F;

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
        } else {
            world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            PlayerEntity playerEntity = context.getPlayer();
            if (playerEntity == null) return ActionResult.FAIL;
            ItemStack itemStack = context.getStack();

            boolean bl = !playerEntity.isInCreativeMode() && itemStack.getCount() == 1;
            RecallPointComponent recallPointComponent = new RecallPointComponent(GlobalPos.create(world.getRegistryKey(), blockPos));
            if (bl) {
                PolymerItemUtils.ITEM_MODIFICATION_EVENT.register(
                        (original, client, context1) -> {
                            original.set(ModComponents.RECALL_POINT_DATA, recallPointComponent);
                            return original;
                        }
                );
            } else {

            }

            return ActionResult.SUCCESS;
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
//        RecallPointComponent component = user.getStackInHand(hand).get(ModComponents.RECALL_POINT_DATA);
//        if (user instanceof ServerPlayerEntity player){
//            if (component != null) {
//                BlockPos home = component.recallPos().pos();
//                ServerWorld homeWorld = world.getServer().getWorld(component.recallPos().dimension());
//
//                if (home == null) home = player.getServerWorld().getSpawnPos();
//
//                if (homeWorld == null) homeWorld = world.getServer().getWorld(player.getServerWorld().getRegistryKey());
//                if (!(homeWorld instanceof ServerWorld)) return ActionResult.FAIL;
//
//                home.add(0, 1, 0);
//
//                world.sendEntityStatus(player, (byte) 46);
//                player.teleport(homeWorld, home.getX(), home.getY(), home.getZ(), PositionFlag.DELTA, player.getYaw(), player.getPitch(), false);
//                homeWorld.sendEntityStatus(player, (byte) 46);
//            }
//        }
        return super.use(world, user, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
    }
}
