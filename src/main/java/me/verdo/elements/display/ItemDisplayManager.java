package me.verdo.elements.display;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.DespawnComponent;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventPickup;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.RenderedItemComponent;

import java.util.UUID;

public class ItemDisplayManager {
    public static void createOrUpdateDisplay(
            ItemContainerState state,
            World world,
            double blockX, double blockY, double blockZ,
            Ref<ChunkStore> chunkStoreRef
    ) {
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(blockX, blockZ));

        if (chunk == null)
            return;
        removeDisplayEntity(world, chunkStoreRef, chunk);

        String displayItemId = null;

        ItemStack storedItem = state.getItemContainer().getItemStack((short) 0);

        if (storedItem != null) {
            displayItemId = storedItem.getItemId();
        }

        if (displayItemId == null || displayItemId.isEmpty()) {
            return;
        }

        // Create display item stack from the item ID
        ItemStack displayItem = new ItemStack(displayItemId, 1);

        // Create a central position above the block pos
        Vector3d displayPosition = new Vector3d(
                blockX + 0.5,
                blockY + 1,
                blockZ + 0.5
        );

        // Create the display item entity
        Store<EntityStore> entityStore = world.getEntityStore().getStore();

        Holder<EntityStore> displayHolder = createDisplayItemHolder(
                entityStore,
                displayItem,
                displayPosition
        );

        if (displayHolder != null) {
            world.getEntityStore().getStore().addEntity(displayHolder, AddReason.SPAWN);

            RenderedItemComponent c = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().renderedItem);

            UUIDComponent uuidComponent = displayHolder.getComponent(UUIDComponent.getComponentType());
            if (uuidComponent != null) {
                c.setStoredUUID(uuidComponent.getUuid());
                chunkStoreRef.getStore().replaceComponent(chunkStoreRef, ElementsPlugin.get().renderedItem, c);
                chunk.markNeedsSaving();
            }
        }
    }
    public static void removeDisplayEntity(World world, Ref<ChunkStore> chunkStoreRef, WorldChunk chunk) {
        RenderedItemComponent c = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().renderedItem);
        UUID displayUUID = c.getStoredUUID();
        if (displayUUID == null) {
            return;
        }

        removeDisplayEntity(world, displayUUID);

        c.setStoredUUID(null);
        chunkStoreRef.getStore().replaceComponent(chunkStoreRef, ElementsPlugin.get().renderedItem, c);
        chunk.markNeedsSaving();
    }

    public static void removeDisplayEntity(World world, UUID uuid) {
        EntityStore entityStore = world.getEntityStore();
        Store<EntityStore> store = entityStore.getStore();

        if (uuid == null) {
            return;
        }

        Ref<EntityStore> entityRef = entityStore.getRefFromUUID(uuid);

        if (entityRef != null && entityRef.isValid()) {
            store.removeEntity(entityRef, RemoveReason.REMOVE);
        }
    }
    private static Holder<EntityStore> createDisplayItemHolder(
            ComponentAccessor<EntityStore> accessor,
            ItemStack itemStack,
            Vector3d position
    ) {
        if (itemStack == null || itemStack.isEmpty() || !itemStack.isValid()) {
            System.out.println("No valid item");
            return null;
        }

        ItemStack displayStack = new ItemStack(itemStack.getItemId(), 1);

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

        holder.addComponent(ItemComponent.getComponentType(), new ItemComponent(displayStack));
        holder.addComponent(TransformComponent.getComponentType(),
                new TransformComponent(position, Vector3f.ZERO));
        holder.ensureComponent(Intangible.getComponentType());
        holder.ensureComponent(PreventPickup.getComponentType());
        holder.ensureComponent(UUIDComponent.getComponentType());
        holder.ensureComponent(PhysicsValues.getComponentType());

        TimeResource timeResource = accessor.getResource(TimeResource.getResourceType());
        holder.addComponent(DespawnComponent.getComponentType(),
                DespawnComponent.despawnInSeconds(timeResource, 86400f));

        return holder;
    }
}
