package me.verdo.elements.npc;

import com.hypixel.hytale.server.npc.NPCPlugin;
import me.verdo.elements.npc.action.builder.BuilderApplySealAction;
import me.verdo.elements.npc.action.builder.BuilderHarvestCropAction;
import me.verdo.elements.npc.sensor.builder.BuilderCheckActiveSealSensor;
import me.verdo.elements.npc.sensor.builder.BuilderFindCropSensor;

public class ElementsNPC {
    public static void registerComponents() {
        NPCPlugin.get().registerCoreComponentType("FindCrop", BuilderFindCropSensor::new);
        NPCPlugin.get().registerCoreComponentType("HarvestCrop", BuilderHarvestCropAction::new);

        NPCPlugin.get().registerCoreComponentType("CheckActiveSeal", BuilderCheckActiveSealSensor::new);
        NPCPlugin.get().registerCoreComponentType("ApplySeal", BuilderApplySealAction::new);
    }
}
