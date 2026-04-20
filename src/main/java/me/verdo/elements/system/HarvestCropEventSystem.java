package me.verdo.elements.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.util.ModChunkUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;

public class HarvestCropEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public HarvestCropEventSystem(@NonNullDecl Class<BreakBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl BreakBlockEvent event) {
        System.out.println("Handling break block event in HarvestCropEventSystem");

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(ref, Player.getComponentType());

        if (player == null)
            return;

        World world = player.getWorld();

        if (world == null)
            return;
        System.out.println("0");

        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(event.getTargetBlock().x, event.getTargetBlock().z));

        if (chunk == null) {
            return;
        }

        System.out.println("A");

//        int localX = ChunkUtil.localCoordinate(event.getTargetBlock().x);
//        int localZ = ChunkUtil.localCoordinate(event.getTargetBlock().z);
//        Ref<ChunkStore> chunkStoreRef = chunk.getBlockComponentEntity(localX, event.getTargetBlock().y, localZ);
//
//        if (chunkStoreRef == null)
//            return;

        System.out.println("A2");

        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();

        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = chunkStore.getResource(ElementsPlugin.get().essenceCollectorSpatialResourceType);
        List<Ref<ChunkStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(event.getTargetBlock().toVector3d(), 10, 10, 10, results);

        System.out.println("B");

        if (!results.isEmpty()) {
            for (Ref<ChunkStore> result : results) {
                BlockModule.BlockStateInfo blockStateInfoI = world.getChunkStore().getStore().getComponent(result, BlockModule.BlockStateInfo.getComponentType());
                Vector3i blockPosI = ModChunkUtil.getBlockPosFromIndex(blockStateInfoI);

                Vector3d from = event.getTargetBlock().toVector3d().add(0.5, 0.5, 0.5);
                Vector3d to = blockPosI.clone().toVector3d().add(0.5, 0.5, 0.5);

                double distance = from.distanceTo(to);
                int steps = (int) (distance / 0.5); // one particle every 0.5 blocks

                for (int step = 0; step <= steps; step++) {
                    double t = steps == 0 ? 0 : (double) step / steps;
                    Vector3d point = new Vector3d(
                            from.x + (to.x - from.x) * t,
                            from.y + (to.y - from.y) * t,
                            from.z + (to.z - from.z) * t
                    );

                    ParticleUtil.spawnParticleEffect("GreenOrbImpact", point, world.getEntityStore().getStore());
                }

                System.out.println("Found block at " + blockPosI);
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.or(Player.getComponentType(), ElementsPlugin.get().golemStorage);
    }
}
