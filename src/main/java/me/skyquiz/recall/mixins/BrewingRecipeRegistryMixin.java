package me.skyquiz.recall.mixins;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//@Mixin(BrewingRecipeRegistry.Builder.class)
public interface BrewingRecipeRegistryMixin {

//    @Invoker("registerPotionRecipe")
//    static void invokeRegisterPotionRecipe(RegistryEntry<Potion> input, Item ingredient, RegistryEntry<Potion> output){
//        throw new AssertionError();
//    }

}