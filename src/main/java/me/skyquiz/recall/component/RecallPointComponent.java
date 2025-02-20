package me.skyquiz.recall.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.GlobalPos;

public record RecallPointComponent(GlobalPos recallPos) {
    public static final Codec<RecallPointComponent> RECALL_POINT_CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    GlobalPos.CODEC.fieldOf("target").forGetter(RecallPointComponent::recallPos)
            ).apply(builder, RecallPointComponent::new));
}
