package me.verdo.elements.util;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.spatial.SpatialSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpatialRefUtil {
    public static class ComponentStateRefSystem extends RefSystem<ChunkStore> {
        private final Query<ChunkStore> QUERY;
        private final ResourceType<ChunkStore, SpatialNeedRebuild> spatialNeedRebuild;

        public ComponentStateRefSystem(Query<ChunkStore> query, ResourceType<ChunkStore, SpatialNeedRebuild> spatialNeedRebuild) {
            this.QUERY = query;
            this.spatialNeedRebuild = spatialNeedRebuild;
        }

        public Query<ChunkStore> getQuery() {
            return QUERY;
        }

        public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            commandBuffer.getExternalData().getWorld().getChunkStore().getStore().getResource(spatialNeedRebuild).markAsNeedRebuild();
        }

        public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            commandBuffer.getExternalData().getWorld().getChunkStore().getStore().getResource(spatialNeedRebuild).markAsNeedRebuild();
        }

        @Nonnull
        public String toString() {
            return "ItemContainerStateRefSystem{}";
        }
    }

    public static class ComponentSpatialSystem extends SpatialSystem<ChunkStore> {
        @Nonnull
        private final Query<ChunkStore> QUERY;
        private final ResourceType<ChunkStore, SpatialNeedRebuild> spatialNeedRebuild;

        public ComponentSpatialSystem(ResourceType<ChunkStore, SpatialResource<Ref<ChunkStore>, ChunkStore>> resourceType, Query<ChunkStore> query, ResourceType<ChunkStore, SpatialNeedRebuild> spatialNeedRebuild) {
            this.QUERY = query;
            super(resourceType);
            this.spatialNeedRebuild = spatialNeedRebuild;
        }

        public void tick(float dt, int systemIndex, @Nonnull Store<ChunkStore> store) {
            if (store.getResource(spatialNeedRebuild).invalidateAndReturnIfNeedRebuild()) {
                super.tick(dt, systemIndex, store);
            }
        }

        public Vector3d getPosition(@Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, int index) {
            BlockModule.BlockStateInfo blockInfo = archetypeChunk.getComponent(index, BlockModule.BlockStateInfo.getComponentType());
            Ref<ChunkStore> chunkRef = blockInfo.getChunkRef();
            if (chunkRef != null && chunkRef.isValid()) {
                BlockChunk blockChunk = chunkRef.getStore().getComponent(chunkRef, BlockChunk.getComponentType());
                int worldX = blockChunk.getX() << 5 | ChunkUtil.xFromBlockInColumn(blockInfo.getIndex());
                int worldY = ChunkUtil.yFromBlockInColumn(blockInfo.getIndex());
                int worldZ = blockChunk.getZ() << 5 | ChunkUtil.zFromBlockInColumn(blockInfo.getIndex());
                return new Vector3d(worldX, worldY, worldZ);
            } else {
                return null;
            }
        }

        @Nullable
        public Query<ChunkStore> getQuery() {
            return QUERY;
        }
    }

    public static class SpatialNeedRebuild implements Resource<ChunkStore> {
        private boolean needRebuild;

        public SpatialNeedRebuild() {
            this.needRebuild = false;
        }

        public SpatialNeedRebuild(boolean needRebuild) {
            this.needRebuild = needRebuild;
        }

        public boolean invalidateAndReturnIfNeedRebuild() {
            if (this.needRebuild) {
                this.needRebuild = false;
                return true;
            } else {
                return false;
            }
        }

        public void markAsNeedRebuild() {
            this.needRebuild = true;
        }

        public Resource<ChunkStore> clone() {
            return new SpatialNeedRebuild(this.needRebuild);
        }
    }
}
