package me.skyquiz.recall;

import com.thedeathlycow.vaulted.end.VaultedEnd;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.skyquiz.recall.effect.RecallStatusEffect;
import me.skyquiz.recall.item.ReturnApple;
import me.skyquiz.recall.item.ReturnPotion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
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
//    public static final RegistryEntry<StatusEffect> UNSTABILITY;
//
//    public static final Potion UNSTABILITY_POTION;



    static {
        RECALL = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "recall"), new RecallStatusEffect());
//        UNSTABILITY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "unstability"), new UnstabilityStatusEffect());
//        UNSTABILITY_POTION = Registry.register(Registries.POTION, Identifier.of(MOD_ID, "unstability"),
//                new SimplePolymerPotion("unstability", new StatusEffectInstance(Recall.UNSTABILITY, Recall.TIME_TO_TP_TICKS, 0))
//        );
    }


    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
        boolean valid = PolymerResourcePackUtils.addModAssets(MOD_ID);
        if (valid) LOGGER.info("Added Resources");

//        RegistryEntry<Potion> unstability_entry = Registries.POTION.getEntry(UNSTABILITY_POTION);

//        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
//                builder.registerPotionRecipe(
//                        // Input potion.
//                        Potions.AWKWARD,
//                        // Ingredient
//                        RETURN_APPLE,
//                        // Output potion.
//                        unstability_entry
//                ));

        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(MOD_ID, "recall"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(RETURN_APPLE::getDefaultStack)
                .displayName(Text.translatable("itemgroup." + MOD_ID))
                .entries(((displayContext, entries) -> {
                    entries.add(RETURN_APPLE);
                    entries.add(RETURN_POTION);
//                    entries.add(PotionContentsComponent.createStack(Items.POTION, unstability_entry));
//                    entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, unstability_entry));
//                    entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, unstability_entry));
//                    entries.add(PotionContentsComponent.createStack(Items.TIPPED_ARROW, unstability_entry));
                })).build());


        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (key.getValue().equals(Identifier.of(VaultedEnd.MOD_ID, "vaults/normal/elytra"))) {
                // We make the pool and add an item
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(RETURN_APPLE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 4.0f)).build());
                tableBuilder.pool(poolBuilder);
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (key.getValue().equals(Identifier.of(VaultedEnd.MOD_ID, "vaults/ominous/elytra"))) {
                // We make the pool and add an item
                LootPool.Builder poolBuilder = LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .with(ItemEntry.builder(RETURN_POTION))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                tableBuilder.pool(poolBuilder);
            }
        });
    }


    public static Item register(Item item, RegistryKey<Item> registryKey) {
        return Registry.register(Registries.ITEM, registryKey.getValue(), item);
    }
}