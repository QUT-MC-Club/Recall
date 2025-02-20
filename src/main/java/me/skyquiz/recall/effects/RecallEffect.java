package me.skyquiz.recall.effects;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import me.skyquiz.recall.Recall;
import net.minecraft.block.SmokerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Random;
import java.util.Set;

public class RecallEffect extends StatusEffect implements PolymerStatusEffect {

    public RecallEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xe9b8b3);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity player) {
            BlockPos home = player.getSpawnPointPosition();
            ServerWorld homeWorld = world.getServer().getWorld(player.getSpawnPointDimension());

            if (home == null) home = player.getServerWorld().getSpawnPos();

            if (homeWorld == null) homeWorld = world.getServer().getWorld(player.getServerWorld().getRegistryKey());
            if (!(homeWorld instanceof ServerWorld)) return super.applyUpdateEffect(world, entity, amplifier);

            home.add(0,1, 0);

            world.sendEntityStatus(player, (byte)46);
            player.teleport(homeWorld, home.getX(), home.getY(), home.getZ(), PositionFlag.DELTA, player.getYaw(), player.getPitch(), false);
            homeWorld.sendEntityStatus(player, (byte)46);

            player.removeStatusEffect(Recall.RECALL_EFFECT);
        }

        return super.applyUpdateEffect(world, entity, amplifier);
    }


    @Override
    public StatusEffect getPolymerReplacement(PacketContext context)
    {
        return null;
    }

}