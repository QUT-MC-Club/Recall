package me.skyquiz.recall.effect;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.skyquiz.recall.Recall;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.dimension.NetherPortal;
import xyz.nucleoid.packettweaker.PacketContext;


public class RecallStatusEffect extends StatusEffect implements PolymerStatusEffect {
    private int timeLeft;
    private boolean active;
    private boolean playedCancelSfx;
    public RecallStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xe9b8b3);
    }

    @Override
    public void playApplySound(LivingEntity entity, int amplifier) {
        active = true;
        playedCancelSfx = false;

        if (entity instanceof ServerPlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, Recall.TIME_TO_TP_TICKS + 60, 1, false, false));
            player.playSoundToPlayer(SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.MASTER, 5, 1);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        timeLeft = duration;
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity player) {
            if (!active && !playedCancelSfx) {
                world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ALLAY_DEATH, SoundCategory.AMBIENT, 1, 3);
                if (player.hasStatusEffect(StatusEffects.NAUSEA)) player.removeStatusEffect(StatusEffects.NAUSEA);
                playedCancelSfx = true;
            }
        }


        if (getTimeLeft() <= 1) {
            if (entity instanceof ServerPlayerEntity player) {
                Random random = Random.create();
                Recall.LOGGER.info(String.valueOf(active));
                if (!active) {
                    player.playSoundToPlayer(SoundEvents.ENTITY_ALLAY_DEATH, SoundCategory.AMBIENT, 1, 3);
                    return true;
                }

                if (amplifier < 1 && random.nextDouble() >= Recall.TELEPORT_CHANCE) {
                    new TeleportRandomlyConsumeEffect(20).onConsume(world, null, entity);
                    return true;
                }

                TeleportTarget target = player.getRespawnTarget(true, TeleportTarget.NO_OP);

                player.fallDistance = 0;
                player.teleportTo(target);
                world.sendEntityStatus(player, (byte) 46);
            }
        }

        return true;
    }

    @Override
    public void onEntityDamage(ServerWorld world, LivingEntity entity, int amplifier, DamageSource source, float amount) {
        if (entity.hasStatusEffect(Recall.RECALL)) active = false;
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}
