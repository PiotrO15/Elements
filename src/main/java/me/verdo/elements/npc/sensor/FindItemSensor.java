package me.verdo.elements.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.EntityPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.GolemSealComponent;
import me.verdo.elements.npc.sensor.builder.BuilderFindItemSensor;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;

public class FindItemSensor extends SensorBase {
    protected final double range;
    protected final EntityPositionProvider positionProvider = new EntityPositionProvider();

    public FindItemSensor(@NonNullDecl BuilderFindItemSensor builderSensorBase, BuilderSupport support) {
        super(builderSensorBase);
        range = builderSensorBase.getRange(support);
    }

    @NullableDecl
    @Override
    public InfoProvider getSensorInfo() {
        return this.positionProvider;
    }

    @Override
    public boolean matches(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, double dt, @NonNullDecl Store<EntityStore> store) {
        if (!super.matches(ref, role, dt, store)) {
            positionProvider.clear();
            return false;
        }

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

        SpatialResource<Ref<EntityStore>, EntityStore> blockStateSpatialStructure = store.getResource(EntityModule.get().getItemSpatialResourceType());
        List<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(golemCenter, range, range, range, results);

        Ref<EntityStore> closest = null;
        double closestDist = Double.MAX_VALUE;
        int golemRange = 8;
        for (Ref<EntityStore> result : results) {
            TransformComponent resultTransform = store.getComponent(result, TransformComponent.getComponentType());
            if (resultTransform == null) continue;
            Vector3d resultPos = resultTransform.getPosition();

            ItemComponent itemComponent = store.getComponent(result, ItemComponent.getComponentType());
            if (itemComponent == null || !itemComponent.canPickUp()) {
                continue;
            }

            double distFromPos = pos.distanceTo(resultPos);
            if (distFromPos > golemRange) continue;
            if (distFromPos < closestDist) {
                closestDist = distFromPos;
                closest = result;
            }
        }

        if (closest != null) {
            positionProvider.setTarget(closest, store);
            return true;
        }
        positionProvider.clear();
        return false;
    }
}
