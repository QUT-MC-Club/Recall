package me.skyquiz.recall.item;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.skyquiz.recall.Recall;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class ReturnApple extends SimplePolymerItem {
    private static final FoodComponent RETURN_APPLE_COMPONENT = new FoodComponent.Builder()
            .alwaysEdible()
            .nutrition(4)
            .saturationModifier(0.3f)
            .build();

    private static final ConsumableComponent RETURN_APPLE_CONSUMABLE_COMPONENT = ConsumableComponent.builder()
            .consumeParticles(true)
            .sound(SoundEvents.ENTITY_GENERIC_EAT)
            .build();

    public ReturnApple(Settings settings) {
        super(settings
                .food(RETURN_APPLE_COMPONENT, RETURN_APPLE_CONSUMABLE_COMPONENT)
                .rarity(Rarity.RARE)
        );
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.APPLE;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return PolymerResourcePackUtils.hasMainPack(context) ? super.getPolymerItemModel(stack, context) : null;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(Recall.RECALL, Recall.TIME_TO_TP_TICKS, 0));
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.recall.return_apple.tooltip").formatted(Formatting.DARK_GRAY));
    }
}
