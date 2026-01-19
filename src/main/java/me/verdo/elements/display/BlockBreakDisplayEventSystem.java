package me.verdo.elements.display;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.RenderedItemComponent;

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

        if (world == null)
            return;

        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(event.getTargetBlock().x, event.getTargetBlock().z));

        if (chunk == null) {
            return;
        }

        int localX = ChunkUtil.localCoordinate(event.getTargetBlock().x);
        int localZ = ChunkUtil.localCoordinate(event.getTargetBlock().z);
        Ref<ChunkStore> chunkStoreRef = chunk.getBlockComponentEntity(localX, event.getTargetBlock().y, localZ);

        if (chunkStoreRef == null)
            return;

        RenderedItemComponent c = chunkStoreRef.getStore().getComponent(chunkStoreRef, ElementsPlugin.get().renderedItem);

        if (c == null) {
            return;
        }

        commandBuffer.run(s -> ItemDisplayManager.removeDisplayEntity(world, c.getStoredUUID()));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
