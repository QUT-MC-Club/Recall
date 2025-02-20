package me.skyquiz.recall;

import me.skyquiz.recall.components.ModComponents;
import me.skyquiz.recall.effects.RecallEffect;
import me.skyquiz.recall.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
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

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryEntry<StatusEffect> RECALL;

    static {
        RECALL = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "recall"), new RecallEffect());
    }


    public static final Potion RECALL_POTION = Registry.register(
                    Registries.POTION,
                    Identifier.of(MOD_ID, "recall"),
                    new Potion("recall", new StatusEffectInstance(
                                            RECALL,
                                    3600,
                                    0)));



    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");

        ModComponents.initialize();
        ModItems.initialize();

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.WATER,
                    // Ingredient
                    Items.PURPLE_BED,
                    // Output potion.
                    Registries.POTION.getEntry(RECALL_POTION)
            );
        });
    }

    public void registerPotionRecipes() {
        //BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, Items.WITHER_ROSE, TATER_POTION);
    }
}