package me.skyquiz.recall.item;

import eu.pb4.polymer.core.api.other.SimplePolymerPotion;
import me.skyquiz.recall.Recall;
import net.minecraft.entity.effect.StatusEffectInstance;

public class UnstabilityPotion extends SimplePolymerPotion {
    public UnstabilityPotion() {
        super("unstability", new StatusEffectInstance(Recall.UNSTABILITY, Recall.TIME_TO_TP_TICKS, 0));
    }
}
