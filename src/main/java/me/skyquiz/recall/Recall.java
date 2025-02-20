package me.skyquiz.recall;

import me.skyquiz.recall.item.RecallApple;
import me.skyquiz.recall.item.RecallScroll;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Recall implements ModInitializer {
    public static final String MOD_ID = "recall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static final RegistryKey<Item> RECALL_APPLE_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "recall_apple"));
    public static final Item RECALL_APPLE = register(
            new RecallApple(new Item.Settings().registryKey(RECALL_APPLE_KEY)),
            RECALL_APPLE_KEY
    );

    public static final RegistryKey<Item> RECALL_SCROLL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "recall_scroll"));
    public static final Item RECALL_SCROLL = register(
            new RecallScroll(new Item.Settings().registryKey(RECALL_SCROLL_KEY)),
            RECALL_SCROLL_KEY
    );

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
    }

    public static Item register(Item item, RegistryKey<Item> registryKey) {
        return Registry.register(Registries.ITEM, registryKey.getValue(), item);
    }

}