package me.skyquiz.recall.effect;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.packettweaker.PacketContext;

public class UnstabilityStatusEffect extends StatusEffect implements PolymerStatusEffect {
    private int timeLeft;

    public UnstabilityStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x5900FF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        timeLeft = duration;
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (timeLeft <= 1) {
            new TeleportRandomlyConsumeEffect(20 + 10 * (amplifier + 1)).onConsume(world, null, entity);
        }

        return super.applyUpdateEffect(world, entity, amplifier);
    }

    @Override
    public StatusEffect getPolymerReplacement(PacketContext context)
    {
        return StatusEffects.NAUSEA.value();
    }
}
