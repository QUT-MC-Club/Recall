package me.skyquiz.recall.effect;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.server.world.ServerWorld;
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
        new TeleportRandomlyConsumeEffect(20 + 10 * (amplifier + 1)).onConsume(world, null, target);
    }

    @Override
    public StatusEffect getPolymerReplacement(PacketContext context) {
        return null;
    }
}
