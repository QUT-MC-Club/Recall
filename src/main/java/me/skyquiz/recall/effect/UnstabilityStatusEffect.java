package me.skyquiz.recall.effect;

import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import com.jamieswhiteshirt.rtree3i.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class UnstabilityStatusEffect extends StatusEffect implements PolymerStatusEffect {
    public UnstabilityStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xB438FF);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, net.minecraft.entity.LivingEntity target, int amplifier, double proximity) {
        if (attacker == null) return;
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        if (ClaimUtils.canDamageEntity(world, target, world.getDamageSources().mobAttack(livingEntity))) {
            teleportEntitySafely(20 + 10 * (amplifier + 1), world, target);
        } else {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT);
        }
    }

    @Override
    public StatusEffect getPolymerReplacement(PacketContext context) {
        return null;
    }


    private void teleportEntitySafely(float diameter, World world, LivingEntity user) {
        boolean bl = false;

        for(int i = 0; i < 16; ++i) {
            double d = user.getX() + (user.getRandom().nextDouble() - (double)0.5F) * (double) diameter;
            double e = MathHelper.clamp(user.getY() + (user.getRandom().nextDouble() - (double)0.5F) * (double) diameter, world.getBottomY(), world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1);
            double f = user.getZ() + (user.getRandom().nextDouble() - (double)0.5F) * (double) diameter;
            if (user.hasVehicle()) {
                user.stopRiding();
            }

            Vec3d vec3d = user.getPos();
            Selection<Entry<ClaimBox, Claim>> claims = ClaimUtils.getClaimsAt(world, new BlockPos((int) d, (int) e, (int) f));
            if (!claims.isEmpty()) continue;
            if (user.teleport(d, e, f, true)) {
                world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(user));
                SoundCategory soundCategory;
                SoundEvent soundEvent;
                if (user instanceof FoxEntity) {
                    soundEvent = SoundEvents.ENTITY_FOX_TELEPORT;
                    soundCategory = SoundCategory.NEUTRAL;
                } else {
                    soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    soundCategory = SoundCategory.PLAYERS;
                }

                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
                user.onLanding();
                bl = true;
                break;
            }
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT);
        user.teleport(user.getX(), user.getY(), user.getZ(), true);

        if (bl && user instanceof PlayerEntity playerEntity) {
            playerEntity.clearCurrentExplosion();
        }
        // return bl
    }
}
