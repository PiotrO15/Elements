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
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.EssenceType;
import me.verdo.elements.util.ModChunkUtil;
import me.verdo.elements.util.ModParticleUtil;
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
        if (!blockType.getId().contains("Plant_Crop") || !blockType.getId().contains("StageFinal")) {
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
                if (blockStateInfoI == null) continue;
                Vector3i blockPosI = ModChunkUtil.getBlockPosFromIndex(blockStateInfoI);

                Vector3d from = event.getTargetBlock().toVector3d().add(0.5, 0.5, 0.5);
                Vector3d to = blockPosI.clone().toVector3d().add(0.5, 2.0, 0.5);

                double distance = from.distanceTo(to);
                int steps = (int) (distance / 0.5);

                Color particleColor;
                String essenceType;

                BlockType blockTypeI = world.getBlockType(to.toVector3i());
                if (blockTypeI == null) continue;

                if (blockTypeI.getId().equals("Essence_Collector_Harvest")) {
                    particleColor = EssenceType.HARVEST.getColor();
                    essenceType = "Harvest_Essence";
                } else {
                    particleColor = EssenceType.LIFE.getColor();
                    essenceType = "Ingredient_Life_Essence";
                }
                ModParticleUtil.createParticleFlow(world, from, to, particleColor);

                HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> world.execute(() -> {
                    ItemContainerBlock scheduledContainer = chunkStore.getComponent(result, ItemContainerBlock.getComponentType());
                    if (scheduledContainer == null) return;

                    if (scheduledContainer.getItemContainer().addItemStack(new ItemStack(essenceType)).succeeded()) {
                    }
                }), steps * 100L, TimeUnit.MILLISECONDS);

                break;
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.or(Player.getComponentType(), ElementsPlugin.get().golemStorage);
    }
}
