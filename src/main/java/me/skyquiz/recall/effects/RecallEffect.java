package me.skyquiz.recall.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class RecallEffect extends StatusEffect {
    public RecallEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.NEUTRAL, 0xe9b8b3);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            player.addExperience(1 << amplifier);

            BlockPos home = player.getSpawnPointPosition();
            RegistryKey<World> dimension = player.getSpawnPointDimension();

            if (home == null) {
                home = player.getServerWorld().getSpawnPos();
            }

            if (dimension == null) {
                dimension = player.getServerWorld().getRegistryKey();
            }

            home.add(0,1, 0);

            //player.teleport(home.getX(), home.getY(), home.getZ(), true);

            entity.teleport(home.getX(), home.getY(), home.getZ(), true);
        }

        return super.applyUpdateEffect(world, entity, amplifier);
    }
}