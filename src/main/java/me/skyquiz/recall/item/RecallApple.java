package me.skyquiz.recall.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class RecallApple extends PotionItem implements PolymerItem {
    // shapeless - eye of ender + dragons breath + golden apple = 1 recall potion
    public RecallApple(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.APPLE;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            BlockPos home = player.getSpawnPointPosition();
            ServerWorld homeWorld = world.getServer().getWorld(player.getSpawnPointDimension());

            if (home == null) home = player.getServerWorld().getSpawnPos();

            if (homeWorld == null) homeWorld = world.getServer().getWorld(player.getServerWorld().getRegistryKey());
            if (!(homeWorld instanceof ServerWorld)) return super.finishUsing(stack, world, user);

            home.add(0, 1, 0);

            world.sendEntityStatus(player, (byte) 46);
            player.teleport(homeWorld, home.getX(), home.getY(), home.getZ(), PositionFlag.DELTA, player.getYaw(), player.getPitch(), false);
            homeWorld.sendEntityStatus(player, (byte) 46);

            return super.finishUsing(stack, world, user);
        }
        return super.finishUsing(stack, world, user);
    }
}
