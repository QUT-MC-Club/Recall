package me.skyquiz.recall;

import com.thedeathlycow.vaulted.end.VaultedEnd;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.other.SimplePolymerPotion;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.skyquiz.recall.effect.RecallStatusEffect;
import me.skyquiz.recall.effect.InstabilityStatusEffect;
import me.skyquiz.recall.item.ReturnApple;
import me.skyquiz.recall.item.ReturnPotion;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
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
    public static final double TELEPORT_CHANCE = 0.25;
    public static final int TIME_TO_TP_TICKS = 100;


    public static final RegistryKey<Item> RETURN_APPLE_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "return_apple"));
    public static final Item RETURN_APPLE = register(new ReturnApple(new Item.Settings().registryKey(RETURN_APPLE_KEY)), RETURN_APPLE_KEY);

    public static final RegistryKey<Item> RETURN_POTION_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "return_potion"));
    public static final Item RETURN_POTION = register(new ReturnPotion(new Item.Settings().registryKey(RETURN_POTION_KEY)), RETURN_POTION_KEY);


    public static final RegistryEntry<StatusEffect> RECALL;
    public static final RegistryEntry<StatusEffect> INSTABILITY;

    public static final Potion INSTABILITY_POTION;
    public static final Potion STRONG_INSTABILITY_POTION;

    static {
        RECALL = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "recall"), new RecallStatusEffect());
        INSTABILITY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "instability"), new InstabilityStatusEffect());
        INSTABILITY_POTION = Registry.register(Registries.POTION, Identifier.of(MOD_ID, "instability"),
                new SimplePolymerPotion("instability", new StatusEffectInstance(Recall.INSTABILITY, 1, 0))
        );
        STRONG_INSTABILITY_POTION = Registry.register(Registries.POTION, Identifier.of(MOD_ID, "strong_instability"),
                new SimplePolymerPotion("strong_instability", new StatusEffectInstance(Recall.INSTABILITY, 1, 1))
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

        RegistryEntry<Potion> instability_entry = Registries.POTION.getEntry(INSTABILITY_POTION);
        RegistryEntry<Potion> strong_instability_entry = Registries.POTION.getEntry(STRONG_INSTABILITY_POTION);

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                builder.registerPotionRecipe(
                        // Input potion.
                        Potions.AWKWARD,
                        // Ingredient
                        Items.CHORUS_FRUIT,
                        // Output potion.
                        instability_entry
                ));
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder ->
                builder.registerPotionRecipe(
                        // Input potion.
                        instability_entry,
                        // Ingredient
                        Items.GLOWSTONE_DUST,
                        // Output potion.
                        strong_instability_entry
                ));


        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(MOD_ID, "recall"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(RETURN_APPLE::getDefaultStack)
                .displayName(Text.translatable("itemgroup." + MOD_ID))
                .entries(((displayContext, entries) -> {
                    entries.add(RETURN_APPLE);
                    entries.add(RETURN_POTION);
                    entries.add(PotionContentsComponent.createStack(Items.POTION, instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.TIPPED_ARROW, instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.POTION, strong_instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.SPLASH_POTION, strong_instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.LINGERING_POTION, strong_instability_entry));
                    entries.add(PotionContentsComponent.createStack(Items.TIPPED_ARROW, strong_instability_entry));
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