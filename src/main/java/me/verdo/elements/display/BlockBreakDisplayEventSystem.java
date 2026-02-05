package me.verdo.elements.display;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.util.ModChunkUtil;
import me.verdo.elements.util.WorldUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBreakDisplayEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public BlockBreakDisplayEventSystem(@Nonnull Class<BreakBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull BreakBlockEvent event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(ref, Player.getComponentType());

        if (player == null)
            return;

        World world = player.getWorld();

        Ref<ChunkStore> chunkStoreRef = ModChunkUtil.getBlockComponentEntity(world, event.getTargetBlock());

        if (chunkStoreRef == null)
            return;

        StoredItemComponent storedItem = chunkStoreRef.getStore().getComponent(chunkStoreRef, ElementsPlugin.get().storedItem);
        if (storedItem != null) {
            commandBuffer.run(_ -> ItemDisplayManager.removeDisplayEntity(world, storedItem.getDisplayedItemUUID()));
            WorldUtil.dropItem(world, event.getTargetBlock(), storedItem.getStoredItem());
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
