package me.verdo.elements.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3iUtil;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EssencePipeSystem {
    private enum PipeShape {
        ZERO("Pipe_Empty"),
        ONE("Pipe_One", Vector3iUtil.NORTH),
        TWO_STRAIGHT("Pipe_Two_H", Vector3iUtil.WEST, Vector3iUtil.EAST),
        TWO_CORNER("Pipe_Two_D", Vector3iUtil.WEST, Vector3iUtil.DOWN),
        THREE_HORIZONTAL("Pipe_Three_H", Vector3iUtil.EAST, Vector3iUtil.NORTH, Vector3iUtil.SOUTH),
        THREE_VERTICAL("Pipe_Three_V", Vector3iUtil.NORTH, Vector3iUtil.SOUTH, Vector3iUtil.UP),
        THREE_DIRECTIONAL("Pipe_Three_D", Vector3iUtil.WEST, Vector3iUtil.NORTH, Vector3iUtil.DOWN),
        FOUR_CROSS("Pipe_Four_Cross", Vector3iUtil.NORTH, Vector3iUtil.SOUTH, Vector3iUtil.EAST, Vector3iUtil.WEST),
        FOUR_DIRECTIONAL("Pipe_Four_D", Vector3iUtil.NORTH, Vector3iUtil.SOUTH, Vector3iUtil.WEST, Vector3iUtil.DOWN),
        FIVE_WAY("Pipe_Five", Vector3iUtil.NORTH, Vector3iUtil.SOUTH, Vector3iUtil.EAST, Vector3iUtil.WEST, Vector3iUtil.DOWN),
        SIX_WAY("Pipe_Six", Vector3iUtil.NORTH, Vector3iUtil.SOUTH, Vector3iUtil.EAST, Vector3iUtil.WEST, Vector3iUtil.UP, Vector3iUtil.DOWN);

        final String name;
        final Set<Vector3ic> connections;

        PipeShape(String name, Vector3ic... connections) {
            this.name = name;
            this.connections = Set.of(connections);
        }

        public static PipeShape getMatchingShape(Set<Vector3ic> currentConnections) {
            return switch (currentConnections.size()) {
                case 1 -> ONE;
                case 2 -> getPlaneCount(currentConnections) == 1 ? TWO_STRAIGHT : TWO_CORNER;
                case 3 -> {
                    if (getPlaneCount(currentConnections) == 3) yield THREE_DIRECTIONAL;

                    boolean hasVertical = currentConnections.contains(Vector3iUtil.UP) || currentConnections.contains(Vector3iUtil.DOWN);
                    if (hasVertical) yield THREE_VERTICAL;
                    yield THREE_HORIZONTAL;
                }
                case 4 -> getPlaneCount(currentConnections) == 3 ? FOUR_DIRECTIONAL : FOUR_CROSS;
                case 5 -> FIVE_WAY;
                case 6 -> SIX_WAY;
                default -> ZERO;
            };
        }

        private static int getPlaneCount(Set<Vector3ic> connections) {
            Set<Integer> planes = new HashSet<>();
            for (Vector3ic dir : connections) {
                if (dir.equals(Vector3iUtil.UP) || dir.equals(Vector3iUtil.DOWN)) {
                    planes.add(1);
                } else if (dir.equals(Vector3iUtil.NORTH) || dir.equals(Vector3iUtil.SOUTH)) {
                    planes.add(2);
                } else if (dir.equals(Vector3iUtil.EAST) || dir.equals(Vector3iUtil.WEST)) {
                    planes.add(3);
                }
            }
            return planes.size();
        }

        public static int getRotation(PipeShape shape, Set<Vector3ic> connections) {
            RotationTuple[] rotations = RotationTuple.VALUES;

            for (RotationTuple rotation : rotations) {
                Set<Vector3i> rotatedConnections = new HashSet<>();
                for (Vector3ic dir : shape.connections) {
                    Vector3i rotatedDir = Rotation.rotate(dir, rotation.yaw(), rotation.pitch(), rotation.roll());
                    rotatedConnections.add(rotatedDir);
                }
                if (rotatedConnections.equals(connections)) {
                    return rotation.index();
                }
            }

            return 0;
        }
    }

    public static void updateNeighborPipes(World world, Vector3i pos) {
        updatePipeShape(world, null, pos);
        updatePipeShape(world, pos, new Vector3i(pos).add(Vector3iUtil.NORTH));
        updatePipeShape(world, pos, new Vector3i(pos).add(Vector3iUtil.SOUTH));
        updatePipeShape(world, pos, new Vector3i(pos).add(Vector3iUtil.EAST));
        updatePipeShape(world, pos, new Vector3i(pos).add(Vector3iUtil.WEST));
        updatePipeShape(world, pos, new Vector3i(pos).add(Vector3iUtil.UP));
        updatePipeShape(world, pos, new Vector3i(pos).add(Vector3iUtil.DOWN));
    }

    public static void updatePipeShape(World world, Vector3i from, Vector3i to) {
        BlockType toBlockType = world.getBlockType(to);
        if (toBlockType == null)
            return;

        if (!toBlockType.getId().contains("Essence_Pipe"))
            return;

        WorldChunk chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(to.x, to.z));

        if (chunk == null) {
            return;
        }

        Set<Vector3ic> connections = new HashSet<>();

        if (canPipeConnect(world, to, new Vector3i(to).add(Vector3iUtil.NORTH))) {
            connections.add(Vector3iUtil.NORTH);
        }
        if (canPipeConnect(world, to, new Vector3i(to).add(Vector3iUtil.SOUTH))) {
            connections.add(Vector3iUtil.SOUTH);
        }
        if (canPipeConnect(world, to, new Vector3i(to).add(Vector3iUtil.EAST))) {
            connections.add(Vector3iUtil.EAST);
        }
        if (canPipeConnect(world, to, new Vector3i(to).add(Vector3iUtil.WEST))) {
            connections.add(Vector3iUtil.WEST);
        }
        if (canPipeConnect(world, to, new Vector3i(to).add(Vector3iUtil.UP))) {
            connections.add(Vector3iUtil.UP);
        }
        if (canPipeConnect(world, to, new Vector3i(to).add(Vector3iUtil.DOWN))) {
            connections.add(Vector3iUtil.DOWN);
        }

        PipeShape newShape = PipeShape.getMatchingShape(connections);
        BlockType essencePipe = BlockType.getAssetMap().getAsset("Essence_Pipe");

        if (essencePipe == null) {
            return;
        }

        String blockType = essencePipe.getBlockKeyForState(newShape.name);
        chunk.setBlock(to.x, to.y, to.z, BlockType.getAssetMap().getIndex(blockType), essencePipe, PipeShape.getRotation(newShape, connections), 0, 0);
    }

    private static boolean canPipeConnect(World world, Vector3i from, Vector3i to) {
        BlockType blockType = world.getBlockType(to);
        if (blockType == null)
            return false;
        long chunkIndex = ChunkUtil.indexChunkFromBlock(to.x, to.z);
        WorldChunk worldChunk = world.getChunk(chunkIndex);
        int filler = worldChunk.getFiller(to.x, to.y, to.z);

        return blockType.getId().contains("Essence_Pipe") ||
                (blockType.getId().contains("Essence_Jar") && from.y > to.y) ||
                (blockType.getId().contains("Alchemical_Furnace") && from.y > to.y) ||
                (blockType.getId().contains("Essence_Collector") && FillerBlockUtil.unpackY(filler) == 0 && from.y == to.y);
    }

    public static class PipePlaceEvent extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
        public PipePlaceEvent(@NonNullDecl Class<PlaceBlockEvent> eventType) {
            super(eventType);
        }

        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl PlaceBlockEvent placeBlockEvent) {
            World world = store.getExternalData().getWorld();
            Vector3i target = new Vector3i(placeBlockEvent.getTargetBlock());
            if (placeBlockEvent.getItemInHand() != null && placeBlockEvent.getItemInHand().getItemId().contains("Alchemical_Furnace")) {
                target.add(Vector3iUtil.UP);
            }

            commandBuffer.run(_ -> updateNeighborPipes(store.getExternalData().getWorld(), target));
            HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> world.execute(() -> updateNeighborPipes(world, target)), 1, TimeUnit.MILLISECONDS);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.any();
        }
    }

    public static class PipeBreakEvent extends EntityEventSystem<EntityStore, BreakBlockEvent> {
        public PipeBreakEvent(@NonNullDecl Class<BreakBlockEvent> eventType) {
            super(eventType);
        }

        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl BreakBlockEvent placeBlockEvent) {
            Vector3i target = placeBlockEvent.getTargetBlock();
            if (placeBlockEvent.getBlockType().getId().contains("Alchemical_Furnace")) {
                target.add(Vector3iUtil.UP);
            }

            commandBuffer.run(_ -> updateNeighborPipes(store.getExternalData().getWorld(), target));
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.any();
        }
    }

    public static List<Vector3i> findConnectedJars(World world, Vector3i startPos) {
        List<Vector3i> jarPositions = new ArrayList<>();
        Set<Vector3i> visited = new HashSet<>();
        Queue<Vector3i> toVisit = new LinkedList<>();

        toVisit.add(startPos);
        visited.add(startPos);

        while (!toVisit.isEmpty() && visited.size() < 1000) {
            Vector3i current = toVisit.poll();

            for (Vector3ic direction : DIRECTIONS) {
                Vector3i neighbor = new Vector3i(current).add(direction);

                if (visited.contains(neighbor)) {
                    continue;
                }

                BlockType neighborBlock = world.getBlockType(neighbor);
                if (neighborBlock == null) {
                    continue;
                }

                if (neighborBlock.getId().contains("Essence_Jar") && neighbor.y < current.y) {
                    jarPositions.add(neighbor);
                    visited.add(neighbor);
                }

                else if (neighborBlock.getId().contains("Essence_Pipe")) {
                    toVisit.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return jarPositions;
    }

    private static final Vector3ic[] DIRECTIONS = {
            Vector3iUtil.NORTH, Vector3iUtil.SOUTH,
            Vector3iUtil.EAST, Vector3iUtil.WEST,
            Vector3iUtil.UP, Vector3iUtil.DOWN
    };
}
