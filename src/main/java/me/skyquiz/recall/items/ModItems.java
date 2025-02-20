package me.skyquiz.recall.items;

import me.skyquiz.recall.Recall;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final RegistryKey<Item> RECALL_SCROLL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Recall.MOD_ID, "recall_scroll"));
    public static final Item RECALL_SCROLL = register(
            new ItemRecallScroll(new Item.Settings().registryKey(RECALL_SCROLL_KEY)),
            RECALL_SCROLL_KEY
    );

    public static Item register(Item item, RegistryKey<Item> registryKey) {
        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, registryKey.getValue(), item);

        // Return the registered item!
        return registeredItem;
    }

    public static void initialize() {}
}
