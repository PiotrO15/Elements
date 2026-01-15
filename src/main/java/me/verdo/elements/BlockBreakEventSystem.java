package me.verdo.elements;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockBreakEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    protected BlockBreakEventSystem(@Nonnull Class<BreakBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull BreakBlockEvent event) {
        if (event.getBlockType().getId().replaceFirst("\\*", "").startsWith("Essence_Jar")) {
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

            EssenceStorageComponent c = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage);

            if (c.getStoredEssenceType() != null && c.getStoredEssenceAmount() > 0) {
                ItemStack toDrop = new ItemStack(c.getStoredEssenceType().id);
                toDrop = toDrop.withQuantity(c.getStoredEssenceAmount());
                List<ItemStack> allItemStacks = List.of(toDrop);
                Vector3d dropPosition = event.getTargetBlock().toVector3d().add(0.5F, 0.0F, 0.5F);
                Holder<EntityStore>[] itemEntityHolders = ItemComponent.generateItemDrops(store, allItemStacks, dropPosition, Vector3f.ZERO);
                if (itemEntityHolders.length > 0) {
                    world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
                }
            }
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
