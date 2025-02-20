package me.skyquiz.recall.items;
import me.skyquiz.recall.Recall;
import me.skyquiz.recall.components.ModComponents;
import me.skyquiz.recall.components.RecallPointComponent;
import me.skyquiz.recall.effects.RecallEffect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Set;

public class ItemRecallScroll extends Item {

    public ItemRecallScroll(Settings settings) {
		super(settings);
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
            ItemStack itemStack = context.getStack();

            RecallPointComponent recallPointComponent = new RecallPointComponent(GlobalPos.create(world.getRegistryKey(), blockPos));
            boolean bl = !playerEntity.isInCreativeMode() && itemStack.getCount() == 1;
            if (bl) {
                itemStack.set(ModComponents.RECALL_POINT_DATA, recallPointComponent);
            } else {
                ItemStack itemStack2 = itemStack.copyComponentsToNewStack(ModItems.RECALL_SCROLL, 1);
                itemStack.decrementUnlessCreative(1, playerEntity);
                itemStack2.set(ModComponents.RECALL_POINT_DATA, recallPointComponent);
                if (!playerEntity.getInventory().insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false);
                }
            }

            return ActionResult.SUCCESS;
        }
    }


    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld serverWorld) {
            ItemStack stack = user.getStackInHand(hand);
            RecallPointComponent recallPointComponent = stack.get(ModComponents.RECALL_POINT_DATA);
            if (recallPointComponent != null) {
                BlockPos.Mutable pos = recallPointComponent.recallPos().pos().mutableCopy();

                while (!world.getBlockState(pos).blocksMovement()) {
                    pos.move(Direction.UP);
                }

                world.emitGameEvent(GameEvent.TELEPORT, pos.toBottomCenterPos(), GameEvent.Emitter.of(user));
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                user.teleport(serverWorld, pos.getX(), pos.getY(), pos.getZ(), PositionFlag.DELTA, user.getYaw(), user.getPitch(), true);

                world.emitGameEvent(GameEvent.TELEPORT, pos.toBottomCenterPos(), GameEvent.Emitter.of(user));
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                stack.decrementUnlessCreative(1, user);
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.FAIL;
    }

    public boolean hasGlint(ItemStack stack) {
        return stack.contains(ModComponents.RECALL_POINT_DATA) || super.hasGlint(stack);
    }
}
