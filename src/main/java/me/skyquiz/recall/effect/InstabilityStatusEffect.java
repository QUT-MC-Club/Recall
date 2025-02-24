package me.skyquiz.recall.effect;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.*;
import draylar.goml.registry.GOMLBlocks;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import com.jamieswhiteshirt.rtree3i.Entry;
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

public class InstabilityStatusEffect extends InstantStatusEffect implements PolymerStatusEffect {
    public InstabilityStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xB438FF);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity target, int amplifier) {
        LivingEntity attacker = target.getAttacker();
        boolean permitted = canEntityTeleport(world, target, attacker);

        if (permitted) {
            teleportEntitySafely(20 + 10 * (amplifier + 1), world, target, attacker);
        } else {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT);
        }
        return true;
    }

    @Override
    public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (attacker == null) return;
        if (!(attacker instanceof LivingEntity)) return;

        boolean permitted = canEntityTeleport(world, target, (LivingEntity) attacker);

        if (permitted) {
            teleportEntitySafely(20 + 10 * (amplifier + 1), world, target, (LivingEntity) attacker);
        } else {
            world.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT);
        }
    }

    @Override
    public StatusEffect getPolymerReplacement(PacketContext context) {
        return null;
    }


    private boolean canEntityTeleport(ServerWorld world, LivingEntity target, LivingEntity attacker) {
        var claimOdds = ClaimUtils.getClaimsAt(world, target.getBlockPos());
        AtomicBoolean permitted = new AtomicBoolean(false);
        if (claimOdds.isEmpty()) { // Not within claim
            permitted.set(true);
        } else { // Within Claim
            var testedClaim = claimOdds.filter((Entry<ClaimBox, Claim> claim) -> { // Test for specific claim hit was in
                BlockPos pos = target.getBlockPos();
                Box checkBox = Box.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
                return claim.getKey().toBox().intersectionVolume(checkBox) == 1;
            });

            // assume tested claim is one
            testedClaim.forEach((claimBoxClaimEntry -> {
                Claim claim = claimBoxClaimEntry.getValue();
                Set<UUID> owners = claim.getOwners();
                Set<UUID> trustedPlayers = claim.getTrusted();

                if (attacker instanceof PlayerEntity) { // Attacker is player
                    if (owners.contains(attacker.getUuid()) || trustedPlayers.  contains(attacker.getUuid())) { // attacker is owner or trusted
                        permitted.set(true);
                        return;
                    }
                    if (target instanceof PlayerEntity) { // target is player
                        if (attacker.getUuid().equals(target.getUuid())) { // self-cast
                            permitted.set(true);
                            return;
                        }
                        if (claim.hasAugment(GOMLBlocks.PVP_ARENA.getFirst())) { // claim has pvp-arena
                            switch (claim.getData(GOMLBlocks.PVP_ARENA.getFirst().key)) {
                                case EVERYONE -> permitted.set(true);
                                case DISABLED -> permitted.set(false);
                                case TRUSTED -> permitted.set(claim.hasPermission((PlayerEntity) attacker) && claim.hasPermission((PlayerEntity) target));
                                case UNTRUSTED -> permitted.set(!claim.hasPermission((PlayerEntity) attacker) && !claim.hasPermission((PlayerEntity) target));
                                case null, default -> throw new MatchException(null, null);
                            }
                        } else { // claim does not have pvp arena
                            permitted.set(GetOffMyLawn.CONFIG.enablePvPinClaims);
                        }
                    } else { // target is non-player (animal, armor stand, etc...)
                        permitted.set(false);
                    }
                } else { // Attacker is non-player (probably dispensers or witches, if one will choose to be evil)
                    permitted.set(true);
                }
            }));
        }
        return permitted.get();
    }

    private void teleportEntitySafely(float diameter, World world, LivingEntity target, LivingEntity source) {
        boolean bl = false;

        for(int i = 0; i < 16; ++i) {
            double d = target.getX() + (target.getRandom().nextDouble() - (double)0.5F) * (double) diameter;
            double e = MathHelper.clamp(target.getY() + (target.getRandom().nextDouble() - (double)0.5F) * (double) diameter, world.getBottomY(), world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1);
            double f = target.getZ() + (target.getRandom().nextDouble() - (double)0.5F) * (double) diameter;
            if (target.hasVehicle()) {
                target.stopRiding();
            }

            Vec3d vec3d = target.getPos();

//            Selection<Entry<ClaimBox, Claim>> interactingClaimsOriginal = ClaimUtils.getClaimsAt(world, source.getBlockPos());
//            if (interactingClaimsOriginal.isNotEmpty()) {
//                if ((!ClaimUtils.canDamageEntity(world, target, world.getDamageSources().mobAttack(source)))) {
//                    continue;
//                }
//            }


            Selection<Entry<ClaimBox, Claim>> interactingClaims = ClaimUtils.getClaimsAt(world, new BlockPos((int) d, (int) e, (int) f));
            if (interactingClaims.isNotEmpty()) {
                if ((!ClaimUtils.canDamageEntity(world, target, world.getDamageSources().mobAttack(source)))) {
                    continue;
                }
            }
            if (target.teleport(d, e, f, true)) {
                world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(target));
                SoundCategory soundCategory;
                SoundEvent soundEvent;
                if (target instanceof FoxEntity) {
                    soundEvent = SoundEvents.ENTITY_FOX_TELEPORT;
                    soundCategory = SoundCategory.NEUTRAL;
                } else {
                    soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    soundCategory = SoundCategory.PLAYERS;
                }

                world.playSound(null, target.getX(), target.getY(), target.getZ(), soundEvent, soundCategory);
                target.onLanding();
                bl = true;
                break;
            }
        }

        if (bl && target instanceof PlayerEntity playerEntity) {
            playerEntity.clearCurrentExplosion();
        }
        // return bl
    }


}
