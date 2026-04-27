package me.verdo.elements.npc;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.npc.action.builder.BuilderApplySealAction;
import me.verdo.elements.npc.action.builder.BuilderDepositInContainerAction;
import me.verdo.elements.npc.action.builder.BuilderHarvestCropAction;
import me.verdo.elements.npc.interaction.SpawnGolemInteraction;
import me.verdo.elements.npc.sensor.builder.BuilderCheckActiveSealSensor;
import me.verdo.elements.npc.sensor.builder.BuilderFindCropSensor;
import me.verdo.elements.npc.sensor.builder.BuilderFindItemContainerSensor;
import me.verdo.elements.npc.sensor.builder.BuilderFindItemSensor;

public class ElementsNPC {
    public static void registerComponents() {
        NPCPlugin.get().registerCoreComponentType("FindCrop", BuilderFindCropSensor::new);
        NPCPlugin.get().registerCoreComponentType("HarvestCrop", BuilderHarvestCropAction::new);

        NPCPlugin.get().registerCoreComponentType("CheckActiveSeal", BuilderCheckActiveSealSensor::new);
        NPCPlugin.get().registerCoreComponentType("ApplySeal", BuilderApplySealAction::new);
        NPCPlugin.get().registerCoreComponentType("FindItemContainer", BuilderFindItemContainerSensor::new);
        NPCPlugin.get().registerCoreComponentType("DepositInContainer", BuilderDepositInContainerAction::new);
        NPCPlugin.get().registerCoreComponentType("FindItem", BuilderFindItemSensor::new);

        ElementsPlugin.get().getCodecRegistry(Interaction.CODEC).register("SpawnGolem", SpawnGolemInteraction.class, SpawnGolemInteraction.CODEC);
    }
}
