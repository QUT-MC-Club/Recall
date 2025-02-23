package me.skyquiz.recall.effect;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.*;
import draylar.goml.block.SelectiveClaimAugmentBlock;
import draylar.goml.other.StatusEnum;
import draylar.goml.registry.GOMLBlocks;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import com.jamieswhiteshirt.rtree3i.Entry;
import me.skyquiz.recall.Recall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
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

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class UnstabilityStatusEffect extends InstantStatusEffect implements PolymerStatusEffect {
    public UnstabilityStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xB438FF);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        Recall.LOGGER.info("Ran Update Effect");
        if (ClaimUtils.getClaimsAt(world, entity.getBlockPos()).isEmpty()) {
            teleportEntitySafely(20 + 10 * (amplifier + 1), world, entity);
        } else {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT);
        }
        return true;
    }

    @Override
    public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (attacker == null) return;
        if (!(attacker instanceof LivingEntity)) return;

        var claimOdds = ClaimUtils.getClaimsAt(world, target.getBlockPos());
        AtomicBoolean permitted = new AtomicBoolean(false);
        if (claimOdds.isEmpty()) { // Not within claim
            Recall.LOGGER.info("NOCLAIM - true");
            permitted.set(true);
        } else { // Within Claim
            // TODO: Add way to get single claim to work with
            var testedClaim = claimOdds.filter((Entry<ClaimBox, Claim> claim) -> { // Test for specific claim hit was in
                BlockPos pos = target.getBlockPos();
                Box checkBox = Box.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
                return claim.getKey().toBox().intersectionVolume(checkBox) == 1;
            });

            // assume tested claim is one
            testedClaim.forEach((claimBoxClaimEntry -> {
                Claim claim = claimBoxClaimEntry.getValue();
                Set<UUID> owners = claim.getOwners();
                Set<UUID> trustedPlayers = claim.getTrusted();

                if (attacker instanceof PlayerEntity) { // Attacker is player
                    if (owners.contains(attacker.getUuid()) || trustedPlayers.contains(attacker.getUuid())) { // attacker is owner or trusted
                        Recall.LOGGER.info("TRUST - true");
                        permitted.set(true);
                        return;
                    }
                    if (target instanceof PlayerEntity) { // target is player
                        if (claim.hasAugment(GOMLBlocks.PVP_ARENA.getFirst())) { // claim has pvp-arena
                            switch (claim.getData(GOMLBlocks.PVP_ARENA.getFirst().key)) {
                                case EVERYONE -> {
                                    Recall.LOGGER.info("PVP/ALL - true");
                                    permitted.set(true);
                                }
                                case DISABLED -> {
                                    Recall.LOGGER.info("PVP/DENY - false");
                                    permitted.set(false);
                                }
                                case TRUSTED -> {
                                    Recall.LOGGER.info("PVP/TRUST - " + claim.hasPermission((PlayerEntity) attacker) + " / " + claim.hasPermission((PlayerEntity) target));
                                    permitted.set(claim.hasPermission((PlayerEntity) attacker) && claim.hasPermission((PlayerEntity) target));
                                }
                                case UNTRUSTED -> {
                                    Recall.LOGGER.info("PVP/UNTRUST - " + !claim.hasPermission((PlayerEntity) attacker) + " / " + !claim.hasPermission((PlayerEntity) target));
                                    permitted.set(!claim.hasPermission((PlayerEntity) attacker) && !claim.hasPermission((PlayerEntity) target));
                                }
                                case null, default -> throw new MatchException(null, null);
                            }
                        } else { // claim does not have pvp arena
                            Recall.LOGGER.info("PVP/NOAUG - " + GetOffMyLawn.CONFIG.enablePvPinClaims);
                            permitted.set(GetOffMyLawn.CONFIG.enablePvPinClaims);
                        }
                    } else { // target is non-player (animal, armor stand, etc...)
                        Recall.LOGGER.info("NON-PLAYER+CLAIM - false");
                        permitted.set(false);
                    }
                } else { // Attacker is non-player (probably dispensers or witches, if one will choose to be evil)
                    Recall.LOGGER.info("NON-PLAYER - true");
                    permitted.set(true);
                }
            }));
        }

        Recall.LOGGER.info(String.valueOf(permitted.get()));
        if (permitted.get()) {
            teleportEntitySafely(20 + 10 * (amplifier + 1), world, target);
        } else {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT);
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

        if (bl && user instanceof PlayerEntity playerEntity) {
            playerEntity.clearCurrentExplosion();
        }
        // return bl
    }
}
