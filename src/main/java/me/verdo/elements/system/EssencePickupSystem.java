package me.verdo.elements.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventPickup;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.component.ComplexEssenceStorageComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EssencePickupSystem extends EntityTickingSystem<EntityStore> {
    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void tick(float v, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);
        TransformComponent playerTransform = commandBuffer.getComponent(playerRef, TransformComponent.getComponentType());
        ModelComponent modelcomponent = commandBuffer.getComponent(playerRef, ModelComponent.getComponentType());
        if (playerTransform == null || modelcomponent == null) {
            return;
        }
        Vector3d playerPos = playerTransform.getPosition().clone().add(0, modelcomponent.getModel().getEyeHeight(), 0);

        Player player = EntityUtils.toHolder(index, archetypeChunk).getComponent(Player.getComponentType());
        if (player != null) {
            if (player.getInventory().getUtilityItem() != null && player.getInventory().getUtilityItem().getItemId().equals("Copper_Wand")) {
                ItemStack itemStack = player.getInventory().getUtilityItem();

                ComplexEssenceStorageComponent component = itemStack.getFromMetadataOrNull(ComplexEssenceStorageComponent.METADATA_KEY, ComplexEssenceStorageComponent.CODEC);
                if (component == null) {
                    ItemStack newItemStack = itemStack.withMetadata(ComplexEssenceStorageComponent.METADATA_KEY, ComplexEssenceStorageComponent.CODEC, new ComplexEssenceStorageComponent());
                    player.getInventory().getUtility().replaceItemStackInSlot(player.getInventory().getActiveUtilitySlot(), itemStack, newItemStack);
                }

                List<Ref<EntityStore>> nearby = new ArrayList<>();
                SpatialResource<Ref<EntityStore>, EntityStore> itemSpatialResource = commandBuffer.getResource(EntityModule.get().getItemSpatialResourceType());
                itemSpatialResource.getSpatialStructure().collect(playerPos, 3, nearby);
                for (var itemEntityRef : nearby) {
                    ItemComponent itemComponent = commandBuffer.getComponent(itemEntityRef, ItemComponent.getComponentType());

                    if (itemComponent == null) {
                        continue; // Not an item entity
                    }

                    PreventPickup preventPickup = commandBuffer.getComponent(itemEntityRef, PreventPickup.getComponentType());

                    if (preventPickup != null) {
                        continue;
                    }

                    ItemStack itemStackOnGround = itemComponent.getItemStack();
                    String itemId = itemStackOnGround.getItemId();

                    if (itemId.contains("Essence")) {
                        System.out.println("FOUND ESSENCE!");

                        ItemStack leftover = component.store(itemStackOnGround);

                        ItemStack newMetaItemStack = itemStack.withMetadata(ComplexEssenceStorageComponent.METADATA_KEY, ComplexEssenceStorageComponent.CODEC, component);
                        player.getInventory().getUtility().replaceItemStackInSlot(player.getInventory().getActiveUtilitySlot(), itemStack, newMetaItemStack);

                        if (leftover == null) {
                            commandBuffer.removeEntity(itemEntityRef, RemoveReason.REMOVE);
                        } else {
//                            itemComponent.setItemStack(leftover);
//                            store.replaceComponent(itemEntityRef, ItemComponent.getComponentType(), itemComponent);
                        }
                    }
                }
            }
        }
    }
}
