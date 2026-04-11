package me.verdo.elements.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.HarvestingDropType;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class HarvestCropAction extends ActionBase {
    public HarvestCropAction(@NonNullDecl BuilderActionBase builderActionBase) {
        super(builderActionBase);
    }

    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);

        if (sensorInfo instanceof PositionProvider positionProvider) {
            if (!positionProvider.hasPosition()) {
                return true;
            }

            World world = store.getExternalData().getWorld();
            Vector3i target = new Vector3i((int) positionProvider.getX(), (int) positionProvider.getY(), (int) positionProvider.getZ());
            WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(positionProvider.getX(), positionProvider.getZ()));
            if  (worldChunk == null) return false;

            BlockType blockType = worldChunk.getBlockType(target);
            if (blockType == null) return false;

            BlockGathering blockGathering = blockType.getGathering();
            if (blockGathering == null) return false;
            HarvestingDropType harvest = blockGathering.getHarvest();
            if (harvest == null) return false;

            harvest(ref, store, world, target, blockType);
        }

        return true;
    }

    public void harvest(Ref<EntityStore> ref, Store<EntityStore> entityStore, World world, Vector3i pos, BlockType blockType) {
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();

        long chunkIndex = ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ());
        Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(chunkIndex);
        if (chunkRef == null || !chunkRef.isValid()) return;

        Ref<ChunkStore> sectionRef = world.getChunkStore().getChunkSectionReference(ChunkUtil.chunkCoordinate(pos.getX()), ChunkUtil.chunkCoordinate(pos.getY()), ChunkUtil.chunkCoordinate(pos.getZ()));
        if (sectionRef != null && sectionRef.isValid()) {
            BlockSection section = chunkStore.getComponent(sectionRef, BlockSection.getComponentType());
            if (section != null) {
                int filler = section.getFiller(pos.getX(), pos.getY(), pos.getZ());
                BlockHarvestUtils.performPickupByInteraction(ref, pos, blockType, filler, chunkRef, entityStore, chunkStore);
            }
        }
    }
}
