package me.skyquiz.recall.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.skyquiz.recall.Recall;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

public class ModComponents {
    public static void initialize() {
        Recall.LOGGER.info("Registering {} components", Recall.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }

    public static final ComponentType<RecallPointComponent> RECALL_POINT_DATA = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Recall.MOD_ID, "recall_point"),
            ComponentType.<RecallPointComponent>builder().codec(RecallPointComponent.RECALL_POINT_CODEC).build()
    );
}
