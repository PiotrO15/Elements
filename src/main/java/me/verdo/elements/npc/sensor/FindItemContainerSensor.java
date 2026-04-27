package me.verdo.elements.npc.sensor;

import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.GolemSealComponent;
import me.verdo.elements.npc.sensor.builder.BuilderFindItemContainerSensor;
import me.verdo.elements.util.ModChunkUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;

public class FindItemContainerSensor extends SensorBase {
    protected final double range;
    protected final PositionProvider blockPosition = new PositionProvider();

    public FindItemContainerSensor(@NonNullDecl BuilderFindItemContainerSensor builderSensorBase, BuilderSupport support) {
        super(builderSensorBase);
        range = builderSensorBase.getRange(support);
    }

    @NullableDecl
    @Override
    public InfoProvider getSensorInfo() {
        return this.blockPosition;
    }

    @Override
    public boolean matches(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, double dt, @NonNullDecl Store<EntityStore> store) {
        if (!super.matches(ref, role, dt, store)) {
            return false;
        }

        World world = store.getExternalData().getWorld();

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            return false;
        }

        GolemSealComponent golem = store.getComponent(ref, ElementsPlugin.get().golemStorage);

        if (golem == null) {
            return false;
        }

        Vector3d golemCenter = golem.getCenter();
        Vector3d pos = transform.getPosition();

        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();

        int golemRange = 8;
        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = chunkStore.getResource(BlockModule.get().getItemContainerSpatialResourceType());
        List<Ref<ChunkStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(golemCenter, golemRange, golemRange, golemRange, results);

        Vector3i closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Ref<ChunkStore> result : results) {
            BlockModule.BlockStateInfo blockStateInfoI = chunkStore.getComponent(result, BlockModule.BlockStateInfo.getComponentType());
            if (blockStateInfoI == null) continue;
            Vector3i blockPos = ModChunkUtil.getBlockPosFromIndex(blockStateInfoI);

            BlockType blockType = world.getBlockType(blockPos);
            if (blockType == null || !blockType.getId().contains("Chest"))
                continue;

            double distFromPos = pos.distanceTo(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5);
            if (distFromPos > range) continue;
            if (distFromPos < closestDist) {
                closestDist = distFromPos;
                closest = blockPos.clone();
            }
        }

        if (closest != null) {
            blockPosition.setTarget(closest.x + 0.5, closest.y + 0.5, closest.z + 0.5);
            return true;
        }
        return false;
    }
}
