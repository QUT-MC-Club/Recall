package me.skyquiz.recall.effect;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import me.skyquiz.recall.Recall;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import oshi.util.platform.windows.WmiUtil;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Random;

public class RecallStatusEffect extends StatusEffect implements PolymerStatusEffect {
    public RecallStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xe9b8b3);
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        Recall.LOGGER.info(String.valueOf(duration));
        if (duration <= 1) return true;

        return super.canApplyUpdateEffect(duration, amplifier);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity player) {
            BlockPos home = player.getSpawnPointPosition();
            ServerWorld homeWorld = world.getServer().getWorld(player.getSpawnPointDimension());

            if (home == null) home = player.getServerWorld().getSpawnPos();

            if (homeWorld == null) homeWorld = world.getServer().getWorld(player.getServerWorld().getRegistryKey());
            if (!(homeWorld instanceof ServerWorld)) return false;

            home.add(0,1, 0);

            world.sendEntityStatus(player, (byte)46);
            player.teleport(homeWorld, home.getX(), home.getY(), home.getZ(), PositionFlag.DELTA, player.getYaw(), player.getPitch(), false);
            homeWorld.sendEntityStatus(player, (byte)46);
        } else {
            if (!entity.isAlive()) return false;
            new TeleportRandomlyConsumeEffect(20).onConsume(world, null, entity);
        }
        return super.applyUpdateEffect(world, entity, amplifier);
    }

    @Override
    public StatusEffect getPolymerReplacement(PacketContext context)
    {
        return null;
    }
}
