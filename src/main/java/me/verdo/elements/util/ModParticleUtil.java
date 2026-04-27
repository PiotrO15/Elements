package me.verdo.elements.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ModParticleUtil {
    public static void createParticleFlow(World world, Vector3d from, Vector3d to) {
        Color color = new Color((byte) 121, (byte) 255, (byte) 0);

        createParticleFlow(world, from, to, color);
    }

    public static void createParticleFlow(World world, Vector3d from, Vector3d to, Color color) {
        double distance = from.distanceTo(to);
        int steps = (int) (distance / 0.5);

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
                ParticleUtil.spawnParticleEffect("Essence_Collector_Transfer", point.x, point.y, point.z, yaw, pitch, 0f, 1.0f, color, null, playerRefs, world.getEntityStore().getStore());
            }), step * 100L, TimeUnit.MILLISECONDS);
        }
    }
}
