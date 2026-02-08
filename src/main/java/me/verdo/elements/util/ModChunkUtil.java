package me.verdo.elements.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class ModChunkUtil {
    public static Ref<ChunkStore> getBlockComponentEntity(World world, Vector3d blockPos) {
        if (world == null) {
            return null;
        }

        WorldChunk chunk = world.getChunkIfInMemory(com.hypixel.hytale.math.util.ChunkUtil.indexChunkFromBlock(blockPos.x, blockPos.z));

        if (chunk == null) {
            return null;
        }

        int localX = com.hypixel.hytale.math.util.ChunkUtil.localCoordinate((long) blockPos.x);
        int localZ = com.hypixel.hytale.math.util.ChunkUtil.localCoordinate((long) blockPos.z);
        return chunk.getBlockComponentEntity(localX, (int) blockPos.y, localZ);
    }

    public static Ref<ChunkStore> getBlockComponentEntity(World world, Vector3i blockPos) {
        return getBlockComponentEntity(world, new Vector3d(blockPos.x, blockPos.y, blockPos.z));
    }
}
