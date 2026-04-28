package me.verdo.elements.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.GolemSealComponent;
import me.verdo.elements.npc.action.builder.BuilderApplySealAction;
import me.verdo.elements.npc.sensor.CheckActiveSealSensor;
import me.verdo.elements.util.WorldUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ApplySealAction extends ActionBase {
    public ApplySealAction(@NonNullDecl BuilderApplySealAction builderActionBase) {
        super(builderActionBase);
    }

    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        GolemSealComponent sealComponent = store.ensureAndGetComponent(ref, ElementsPlugin.get().golemStorage);

//        if (!sealComponent.getStoredSeal().isEmpty()) return false;

        Ref<EntityStore> playerReference = role.getStateSupport().getInteractionIterationTarget();
        if (playerReference == null) return false;

        PlayerRef playerRefComponent = store.getComponent(playerReference, PlayerRef.getComponentType());
        if  (playerRefComponent == null) return false;

        Player playerComponent = store.getComponent(playerReference, Player.getComponentType());
        if (playerComponent == null) return false;

        ItemStack heldItem = InventoryComponent.getItemInHand(store, playerReference);

        if (heldItem == null) return false;

        if (heldItem.getItemId().equals("Dominion_Wand")) {
            NPCEntity npcComponent = store.getComponent(ref, NPCEntity.getComponentType());
            TransformComponent npcTransformComponent = store.getComponent(ref, TransformComponent.getComponentType());

            MovementStatesComponent movementStates = store.getComponent(playerReference, MovementStatesComponent.getComponentType());
            if (movementStates == null) return false;
            if (movementStates.getMovementStates().crouching) {
                if (npcTransformComponent == null) return false;
                if (npcComponent == null) return false;

                npcComponent.setToDespawn();
                WorldUtil.dropItem(store, npcTransformComponent.getPosition(), new ItemStack("Straw_Golem"));

                if (sealComponent.getStoredSeal() != null) {
                    WorldUtil.dropItem(store, npcTransformComponent.getPosition(), sealComponent.getStoredSeal());
                }
                return true;
            }
        }

        if (CheckActiveSealSensor.SealType.findSealByItem(heldItem.getItemId()) == null) return false;

        CombinedItemContainer inventory = InventoryComponent.getCombined(store, playerReference, InventoryComponent.HOTBAR_FIRST);

        if (inventory.removeItemStack(heldItem).succeeded()) {
            sealComponent.setStoredSeal(heldItem);

            NPCEntity.setAppearance(ref, "Straw_Golem_Harvesting", store);
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Straw_Golem_Harvesting");
            Model model = Model.createScaledModel(modelAsset, 1.0f);
            PersistentModel persistentModel = new PersistentModel(model.toReference());
            store.putComponent(ref, PersistentModel.getComponentType(), persistentModel);
        }

        return true;
    }
}
