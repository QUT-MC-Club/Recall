package me.skyquiz.recall.component;

import me.skyquiz.recall.Recall;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static void initialize() {
        Recall.LOGGER.info("Registering {} components", Recall.MOD_ID);
    }

    public static final ComponentType<RecallPointComponent> RECALL_POINT_DATA = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Recall.MOD_ID, "recall_point"),
            ComponentType.<RecallPointComponent>builder().codec(RecallPointComponent.RECALL_POINT_CODEC).build()
    );
}
