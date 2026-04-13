package me.verdo.elements.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
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
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ApplySealAction extends ActionBase {
    public ApplySealAction(@NonNullDecl BuilderApplySealAction builderActionBase) {
        super(builderActionBase);
    }

    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        GolemSealComponent golemSeal = store.ensureAndGetComponent(ref, ElementsPlugin.get().golemStorage);

        if (!golemSeal.getStoredSeal().isEmpty()) return false;

        Ref<EntityStore> playerReference = role.getStateSupport().getInteractionIterationTarget();
        if (playerReference == null) return false;

        PlayerRef playerRefComponent = store.getComponent(playerReference, PlayerRef.getComponentType());
        if  (playerRefComponent == null) return false;

        Player playerComponent = store.getComponent(playerReference, Player.getComponentType());
        if (playerComponent == null) return false;

        ItemStack heldItem = InventoryComponent.getItemInHand(store, playerReference);

        if (heldItem == null) return false;

        if (CheckActiveSealSensor.SealType.findSealByItem(heldItem.getItemId()) == null) return false;

        CombinedItemContainer inventory = InventoryComponent.getCombined(store, playerReference, InventoryComponent.HOTBAR_FIRST);
        if (inventory.removeItemStack(heldItem).succeeded()) {
            golemSeal.setStoredSeal(heldItem);

            NPCEntity.setAppearance(ref, "Pig", store);
        }

        return true;
    }
}
