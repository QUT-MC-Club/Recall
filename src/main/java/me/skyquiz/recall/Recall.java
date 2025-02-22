package me.skyquiz.recall;

import eu.pb4.polymer.core.api.other.SimplePolymerPotion;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.skyquiz.recall.effect.RecallStatusEffect;
import me.skyquiz.recall.effect.UnstabilityStatusEffect;
import me.skyquiz.recall.item.ReturnApple;
import me.skyquiz.recall.item.ReturnPotion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recall implements ModInitializer {
    public static final String MOD_ID = "recall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // CONFIG VALUES
    public static final double TELEPORT_CHANCE = 0.4;
    public static final int TIME_TO_TP_TICKS = 100;


    public static final RegistryKey<Item> RETURN_APPLE_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "return_apple"));
    public static final Item RETURN_APPLE = register(new ReturnApple(new Item.Settings().registryKey(RETURN_APPLE_KEY)), RETURN_APPLE_KEY);

    public static final RegistryKey<Item> RETURN_POTION_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "return_potion"));
    public static final Item RETURN_POTION = register(new ReturnPotion(new Item.Settings().registryKey(RETURN_POTION_KEY)), RETURN_POTION_KEY);


    public static final RegistryEntry<StatusEffect> RECALL;
    public static final RegistryEntry<StatusEffect> UNSTABILITY;

    public static final Potion UNSTABILITY_POTION;


    static {
        RECALL = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "recall"), new RecallStatusEffect());
        UNSTABILITY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "unstability"), new UnstabilityStatusEffect());
        UNSTABILITY_POTION = Registry.register(Registries.POTION, Identifier.of(MOD_ID, "unstability"),
                new SimplePolymerPotion("unstability", new StatusEffectInstance(Recall.UNSTABILITY, Recall.TIME_TO_TP_TICKS, 0))
        );
    }


    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
        boolean valid = PolymerResourcePackUtils.addModAssets(MOD_ID);
        if (valid) LOGGER.info("Added Resources");

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                builder.registerPotionRecipe(
                        // Input potion.
                        Potions.AWKWARD,
                        // Ingredient
                        Items.CHORUS_FRUIT,
                        // Output potion.
                        Registries.POTION.getEntry(UNSTABILITY_POTION)
                ));

        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> {
                            itemGroup.add(RETURN_APPLE);
                            itemGroup.add(RETURN_POTION);
//                            itemGroup.add(PotionContentsComponent.createStack(Items.POTION, )));
//                            itemGroup.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, ));
//                            itemGroup.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, ));
//                            itemGroup.add(PotionContentsComponent.createStack(Items.TIPPED_ARROW,  ));
                        }
                );
    }



    public static Item register(Item item, RegistryKey<Item> registryKey) {
        return Registry.register(Registries.ITEM, registryKey.getValue(), item);
    }
}