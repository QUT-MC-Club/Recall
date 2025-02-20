package me.skyquiz.recall;

import me.skyquiz.recall.effects.RecallEffect;
import me.skyquiz.recall.mixins.BrewingRecipeRegistryMixin;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recall implements ModInitializer {
    public static final String MOD_ID = "recall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryEntry<StatusEffect> RECALL_EFFECT = registerStatusEffect("recall", new RecallEffect());

    public static final Potion RECALL_POTION = registerPotion("recall", RECALL_EFFECT);
    public static final RegistryEntry<Potion> RECALL_POTION_ENTRY = registerPotionEntry("recall", RECALL_POTION);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");


    }

    private static RegistryEntry<StatusEffect> registerStatusEffect(String id, StatusEffect effect){
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID,id), effect);
    }

    private static Potion registerPotion(String id, RegistryEntry<StatusEffect> effect)
    {
        return Registry.register(
                Registries.POTION,
                Identifier.of(MOD_ID, id),
                new Potion("id", new StatusEffectInstance(
                        effect,
                        1200,
                        0)));
    }

    private static RegistryEntry<Potion> registerPotionEntry(String name, Potion potion)
    {
        return Registry.registerReference(Registries.POTION, Identifier.ofVanilla(name), potion);
    }

    public static void registerPotionsRecipes(){
        //BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.WATER, Items.PURPLE_BED, RECALL_POTION_ENTRY);
    }
}