package me.verdo.elements.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
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

        Vector3d pos = transform.getPosition();
        for (int x = (int) Math.floor(pos.x - range); x <= Math.ceil(pos.x + range); x++) {
            for (int y = (int) Math.floor(pos.y - range); y <= Math.ceil(pos.y + range); y++) {
                for (int z = (int) Math.floor(pos.z - range); z <= Math.ceil(pos.z + range); z++) {
                    if (world.getBlockType(x, y, z) != null && world.getBlockType(x, y, z).getId().contains("Crop")) {
                        blockPosition.setTarget(x, y, z);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
