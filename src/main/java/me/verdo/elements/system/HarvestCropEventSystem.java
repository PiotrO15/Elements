package me.verdo.elements.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.util.ModChunkUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HarvestCropEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public HarvestCropEventSystem(@NonNullDecl Class<BreakBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl BreakBlockEvent event) {
        BlockType blockType = event.getBlockType();
        if (!blockType.getId().contains("Crop")) {
            return;
        }

        World world = store.getExternalData().getWorld();
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();

        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = chunkStore.getResource(ElementsPlugin.get().essenceCollectorSpatialResourceType);
        List<Ref<ChunkStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(event.getTargetBlock().toVector3d(), 10, 10, 10, results);

        if (!results.isEmpty()) {
            for (Ref<ChunkStore> result : results) {
                BlockModule.BlockStateInfo blockStateInfoI = world.getChunkStore().getStore().getComponent(result, BlockModule.BlockStateInfo.getComponentType());
                if (blockStateInfoI == null) return;
                Vector3i blockPosI = ModChunkUtil.getBlockPosFromIndex(blockStateInfoI);

                Vector3d from = event.getTargetBlock().toVector3d().add(0.5, 0.5, 0.5);
                Vector3d to = blockPosI.clone().toVector3d().add(0.5, 0.5, 0.5);

                double distance = from.distanceTo(to);
                int steps = (int) (distance / 0.5);

//                ItemContainerBlock containerBlock = chunkStore.getComponent(result, ItemContainerBlock.getComponentType());
//                if (containerBlock != null) {
//                    if (containerBlock.getItemContainer().addItemStack(new ItemStack("Ingredient_Life_Essence")).succeeded()) {
//                    }
//                }

                double dx = to.x - from.x;
                double dy = to.y - from.y;
                double dz = to.z - from.z;

                float yaw   = (float) Math.atan2(-dx, -dz);
                float pitch = (float) Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));

                for (int step = 0; step <= steps - 1; step++) {
                    double t = (double) step / steps;
                    Vector3d point = new Vector3d(
                            from.x + dx * t,
                            from.y + dy * t,
                            from.z + dz * t
                    );

                    HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> world.execute(() -> {
                        SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = world.getEntityStore().getStore().getResource(EntityModule.get().getPlayerSpatialResourceType());
                        List<Ref<EntityStore>> playerRefs = SpatialResource.getThreadLocalReferenceList();
                        playerSpatialResource.getSpatialStructure().collect(point, 75.0F, playerRefs);
                        ParticleUtil.spawnParticleEffect("Essence_Collector_Transfer", point, yaw, pitch, 0f, null, playerRefs, world.getEntityStore().getStore());
                    }), step * 100L, TimeUnit.MILLISECONDS);
                }

                HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> world.execute(() -> {
                    ItemContainerBlock scheduledContainer = chunkStore.getComponent(result, ItemContainerBlock.getComponentType());
                    if (scheduledContainer == null) return;

                    if (scheduledContainer.getItemContainer().addItemStack(new ItemStack("Ingredient_Life_Essence")).succeeded()) {
                    }
                }), steps * 100L, TimeUnit.MILLISECONDS);
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.or(Player.getComponentType(), ElementsPlugin.get().golemStorage);
    }
}
