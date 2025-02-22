package me.skyquiz.recall.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.skyquiz.recall.Recall;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import static net.minecraft.item.Items.CARROT_ON_A_STICK;
import static net.minecraft.item.Items.GLASS_BOTTLE;

public class ReturnPotion extends SimplePolymerItem {
    private static final FoodComponent RETURN_POTION_COMPONENT = new FoodComponent.Builder()
            .alwaysEdible()
            .build();

    private static final ConsumableComponent RETURN_POTION_CONSUMABLE_COMPONENT = ConsumableComponent.builder()
            .consumeParticles(false)
            .sound(SoundEvents.ITEM_HONEY_BOTTLE_DRINK)
            .build();

    public ReturnPotion(Settings settings) {
        super(settings
                .maxCount(16)
                .food(RETURN_POTION_COMPONENT, RETURN_POTION_CONSUMABLE_COMPONENT)
                .useRemainder(GLASS_BOTTLE));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.HONEY_BOTTLE;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return PolymerResourcePackUtils.hasMainPack(context) ? super.getPolymerItemModel(stack, context) : null;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(Recall.RECALL, Recall.TIME_TO_TP_TICKS, 1));
        }
        return super.finishUsing(stack, world, user);
    }
}
