package me.verdo.elements.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.GolemSealComponent;
import me.verdo.elements.npc.sensor.builder.BuilderCheckActiveSealSensor;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Locale;

public class CheckActiveSealSensor extends SensorBase {
    protected final SealType sealType;

    public CheckActiveSealSensor(@NonNullDecl BuilderCheckActiveSealSensor builderSensorBase, BuilderSupport support) {
        super(builderSensorBase);
        this.sealType = SealType.valueOf(builderSensorBase.getSealType(support).toUpperCase(Locale.ROOT));
    }

    @NullableDecl
    @Override
    public InfoProvider getSensorInfo() {
        return null;
    }

    @Override
    public boolean matches(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, double dt, @NonNullDecl Store<EntityStore> store) {
        GolemSealComponent golemSealComponent = store.getComponent(ref, ElementsPlugin.get().golemStorage);
        if (golemSealComponent == null) return false;

        return golemSealComponent.getStoredSeal().getItemId().equals(sealType.getSealItem());
    }

    public enum SealType {
        HARVESTING("Harvesting_Seal"),
        GATHERING("Gathering_Seal");

        private final String sealItem;

        SealType(String sealItem) {
            this.sealItem = sealItem;
        }

        public String getSealItem() {
            return sealItem;
        }

        public static SealType findSealByItem(String itemId) {
            for (SealType sealType : SealType.values()) {
                if (itemId.equals(sealType.getSealItem())) {
                    return sealType;
                }
            }
            return null;
        }
    }
}
