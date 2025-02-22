package me.skyquiz.recall.effect;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import me.skyquiz.recall.Recall;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import xyz.nucleoid.packettweaker.PacketContext;


public class RecallStatusEffect extends StatusEffect implements PolymerStatusEffect {
    private int timeLeft;

    public RecallStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xe9b8b3);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        timeLeft = duration;
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
        if (getTimeLeft() <= 1) {
            if (entity instanceof ServerPlayerEntity player) {
                Random random = Random.create();
                if (amplifier < 1 && random.nextDouble() >= Recall.TELEPORT_CHANCE) {
                    new TeleportRandomlyConsumeEffect(20).onConsume(world, null, entity);
                } else {
                    BlockPos home = player.getSpawnPointPosition();
                    ServerWorld homeWorld = world.getServer().getWorld(player.getSpawnPointDimension());

                    if (home == null) home = player.getServerWorld().getSpawnPos();

                    if (homeWorld == null)
                        homeWorld = world.getServer().getWorld(player.getServerWorld().getRegistryKey());
                    if (!(homeWorld instanceof ServerWorld)) return false;

                    home = home.mutableCopy().add(0, 1, 0);

                    world.sendEntityStatus(player, (byte) 46);
                    TeleportTarget target = new TeleportTarget(
                            homeWorld,
                            home.toCenterPos(),
                            Vec3d.ZERO,
                            player.getYaw(),
                            player.getPitch(),
                            TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET
                    );
                    player.fallDistance = 0;
                    player.teleportTo(target);
                    homeWorld.sendEntityStatus(player, (byte) 46);
                }
            }
        }

        return super.applyUpdateEffect(world, entity, amplifier);
    }

    @Override
    public void onEntityDamage(ServerWorld world, LivingEntity entity, int amplifier, DamageSource source, float amount) {
        entity.removeStatusEffect(Recall.RECALL);
    }

    @Override
    public StatusEffect getPolymerReplacement(PacketContext context)
    {
        return StatusEffects.NAUSEA.value();
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}
