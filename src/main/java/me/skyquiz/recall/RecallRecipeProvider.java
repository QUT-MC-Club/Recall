package me.skyquiz.recall;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecallRecipeProvider extends FabricRecipeProvider {
    public RecallRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter exporter) {
        return new RecipeGenerator(wrapperLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

                ShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BREWING, Recall.RETURN_APPLE, 1)
                        .pattern("xxx")
                        .pattern("xox")
                        .pattern("xxx")

                        .input('x', Items.CHORUS_FRUIT)
                        .input('o', Items.GOLDEN_APPLE)
                        .group("multi_bench")
                        .criterion(hasItem(Items.CHORUS_FRUIT), conditionsFromItem(Items.CHORUS_FRUIT))
                        .offerTo(exporter);

                ShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.BREWING, Recall.RETURN_POTION, 1)
                        .pattern("xxx")
                        .pattern("xox")
                        .pattern("xxx")

                        .input('x', Recall.RETURN_APPLE)
                        .input('o', Items.DRAGON_BREATH)
                        .group("multi_bench")
                        .criterion(hasItem(Recall.RETURN_APPLE), conditionsFromItem(Recall.RETURN_APPLE))
                        .offerTo(exporter);


            }
        };
    }

    @Override
    public String getName() {
        return "RecallRecipeProvider";
    }
}
