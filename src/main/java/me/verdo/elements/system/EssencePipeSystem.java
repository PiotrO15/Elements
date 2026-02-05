package me.verdo.elements.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EssencePipeSystem {
    private enum PipeShape {
        ZERO("Pipe_Empty"),
        ONE("Pipe_One", Vector3i.NORTH),
        TWO_STRAIGHT("Pipe_Two_H", Vector3i.WEST, Vector3i.EAST),
        TWO_CORNER("Pipe_Two_D", Vector3i.WEST, Vector3i.DOWN),
        THREE_HORIZONTAL("Pipe_Three_H", Vector3i.EAST, Vector3i.NORTH, Vector3i.SOUTH),
        THREE_VERTICAL("Pipe_Three_V", Vector3i.NORTH, Vector3i.SOUTH, Vector3i.UP),
        THREE_DIRECTIONAL("Pipe_Three_D", Vector3i.WEST, Vector3i.NORTH, Vector3i.DOWN),
        FOUR_CROSS("Pipe_Four_Cross", Vector3i.NORTH, Vector3i.SOUTH, Vector3i.EAST, Vector3i.WEST),
        FOUR_DIRECTIONAL("Pipe_Four_D", Vector3i.NORTH, Vector3i.SOUTH, Vector3i.WEST, Vector3i.DOWN),
        FIVE_WAY("Pipe_Five", Vector3i.NORTH, Vector3i.SOUTH, Vector3i.EAST, Vector3i.WEST, Vector3i.DOWN),
        SIX_WAY("Pipe_Six", Vector3i.NORTH, Vector3i.SOUTH, Vector3i.EAST, Vector3i.WEST, Vector3i.UP, Vector3i.DOWN);

        final String name;
        final Set<Vector3i> connections;

        PipeShape(String name, Vector3i... connections) {
            this.name = name;
            this.connections = Set.of(connections);
        }

        public static PipeShape getMatchingShape(Set<Vector3i> currentConnections) {
            return switch (currentConnections.size()) {
                case 1 -> ONE;
                case 2 -> getPlaneCount(currentConnections) == 1 ? TWO_STRAIGHT : TWO_CORNER;
                case 3 -> {
                    if (getPlaneCount(currentConnections) == 3) yield THREE_DIRECTIONAL;

                    boolean hasVertical = currentConnections.contains(Vector3i.UP) || currentConnections.contains(Vector3i.DOWN);
                    if (hasVertical) yield THREE_VERTICAL;
                    yield THREE_HORIZONTAL;
                }
                case 4 -> getPlaneCount(currentConnections) == 3 ? FOUR_DIRECTIONAL : FOUR_CROSS;
                case 5 -> FIVE_WAY;
                case 6 -> SIX_WAY;
                default -> ZERO;
            };
        }

        private static int getPlaneCount(Set<Vector3i> connections) {
            Set<Integer> planes = new HashSet<>();
            for (Vector3i dir : connections) {
                if (dir.equals(Vector3i.UP) || dir.equals(Vector3i.DOWN)) {
                    planes.add(1);
                } else if (dir.equals(Vector3i.NORTH) || dir.equals(Vector3i.SOUTH)) {
                    planes.add(2);
                } else if (dir.equals(Vector3i.EAST) || dir.equals(Vector3i.WEST)) {
                    planes.add(3);
                }
            }
            return planes.size();
        }

        public static int getRotation(PipeShape shape, Set<Vector3i> connections) {
            RotationTuple[] rotations = RotationTuple.VALUES;

            for (RotationTuple rotation : rotations) {
                Set<Vector3i> rotatedConnections = new HashSet<>();
                for (Vector3i dir : shape.connections) {
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
        updatePipeShape(world, pos);
        updatePipeShape(world, new Vector3i(pos).add(Vector3i.NORTH));
        updatePipeShape(world, new Vector3i(pos).add(Vector3i.SOUTH));
        updatePipeShape(world, new Vector3i(pos).add(Vector3i.EAST));
        updatePipeShape(world, new Vector3i(pos).add(Vector3i.WEST));
        updatePipeShape(world, new Vector3i(pos).add(Vector3i.UP));
        updatePipeShape(world, new Vector3i(pos).add(Vector3i.DOWN));
    }

    public static void updatePipeShape(World world, Vector3i pos) {
        if (!Objects.requireNonNull(world.getBlockType(pos)).getId().contains("Essence_Pipe"))
            return;

        WorldChunk chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));

        if (chunk == null) {
            return;
        }

        Set<Vector3i> connections = new HashSet<>();

        if (canPipeConnect(world, pos, new Vector3i(pos).add(Vector3i.NORTH))) {
            connections.add(Vector3i.NORTH);
        }
        if (canPipeConnect(world, pos, new Vector3i(pos).add(Vector3i.SOUTH))) {
            connections.add(Vector3i.SOUTH);
        }
        if (canPipeConnect(world, pos, new Vector3i(pos).add(Vector3i.EAST))) {
            connections.add(Vector3i.EAST);
        }
        if (canPipeConnect(world, pos, new Vector3i(pos).add(Vector3i.WEST))) {
            connections.add(Vector3i.WEST);
        }
        if (canPipeConnect(world, pos, new Vector3i(pos).add(Vector3i.UP))) {
            connections.add(Vector3i.UP);
        }
        if (canPipeConnect(world, pos, new Vector3i(pos).add(Vector3i.DOWN))) {
            connections.add(Vector3i.DOWN);
        }

        PipeShape newShape = PipeShape.getMatchingShape(connections);
        BlockType essencePipe = BlockType.getAssetMap().getAsset("Essence_Pipe");

        if (essencePipe == null) {
            return;
        }

        String blockType = essencePipe.getBlockKeyForState(newShape.name);
        chunk.setBlock(pos.x, pos.y, pos.z, BlockType.getAssetMap().getIndex(blockType), essencePipe, PipeShape.getRotation(newShape, connections), 0, 0);
    }

    private static boolean canPipeConnect(World world, Vector3i from, Vector3i to) {
        BlockType blockType = world.getBlockType(to);
        if (blockType == null)
            return false;

        return blockType.getId().contains("Essence_Pipe") || (blockType.getId().contains("Essence_Jar") && from.y > to.y);
    }

    public static class PipePlaceEvent extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
        public PipePlaceEvent(@NonNullDecl Class<PlaceBlockEvent> eventType) {
            super(eventType);
        }

        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl PlaceBlockEvent placeBlockEvent) {
            commandBuffer.run(_ -> updateNeighborPipes(store.getExternalData().getWorld(), placeBlockEvent.getTargetBlock()));
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
            commandBuffer.run(_ -> updateNeighborPipes(store.getExternalData().getWorld(), placeBlockEvent.getTargetBlock()));
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.any();
        }
    }
}
