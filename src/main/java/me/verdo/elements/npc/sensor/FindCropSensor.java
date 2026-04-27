package me.verdo.elements.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.GolemSealComponent;
import me.verdo.elements.npc.sensor.builder.BuilderFindCropSensor;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FindCropSensor extends SensorBase {
    protected final double range;
    protected final PositionProvider blockPosition = new PositionProvider();

    public FindCropSensor(@NonNullDecl BuilderFindCropSensor builderSensorBase, BuilderSupport support) {
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

        Vector3i closest = null;
        double closestDist = Double.MAX_VALUE;
        int golemRange = 8;
        for (int x = (int) Math.floor(golemCenter.x - golemRange); x <= Math.ceil(golemCenter.x + golemRange); x++) {
            for (int y = (int) Math.floor(golemCenter.y - golemRange); y <= Math.ceil(golemCenter.y + golemRange); y++) {
                for (int z = (int) Math.floor(golemCenter.z - golemRange); z <= Math.ceil(golemCenter.z + golemRange); z++) {
                    if (world.getBlockType(x, y, z) != null && world.getBlockType(x, y, z).getId().contains("Plant_Crop") && world.getBlockType(x, y, z).getId().contains("StageFinal")) {
                        double distFromPos = pos.distanceTo(x + 0.5, y + 0.5, z + 0.5);
                        if (distFromPos > range) continue;
                        if (distFromPos < closestDist) {
                            closestDist = distFromPos;
                            closest = new Vector3i(x, y, z);
                        }
                    }
                }
            }
        }

        if (closest != null) {
            blockPosition.setTarget(closest.x + 0.5, closest.y + 0.5, closest.z + 0.5);
            return true;
        }
        return false;
    }
}
