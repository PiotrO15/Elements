package me.verdo.elements.npc.action;

import com.hypixel.hytale.builtin.adventure.farming.FarmingUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.HarvestingDropType;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
import me.verdo.elements.util.WorldUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

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

//            if (!blockType.getId().contains("StageFinal")) return false;

//            if (blockType.getId().contains("Eternal") || blockType.getId().contains("Plant_Crop_Berry")) {
//                harvestEternal(ref, store, world, target, blockType);
//            } else {
//                harvest(ref, store, world, target, blockType);
//            }
            harvestEternal(ref, store, world, target, blockType);
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

//                if (blockType.getGathering() == null) {
//                    System.out.println("Block type " + blockType.getId() + " does not have gathering information, cannot harvest.");
//                    return;
//                }
//                HarvestingDropType harvest = blockType.getGathering().getHarvest();
//                String itemId = harvest.getItemId();
//                String dropListId = harvest.getDropListId();
//
//                Vector3d dropPosition = pos.toVector3d().add(0.5F, 0.0F, 0.5F);
//                List<ItemStack> itemStacks = BlockHarvestUtils.getDrops(blockType, 1, itemId, dropListId);
//                WorldUtil.dropItems(entityStore, dropPosition, itemStacks);
//
//                BlockHarvestUtils.performBlockBreak(ref, ItemStack.EMPTY, pos, chunkRef, entityStore, chunkStore);

                BreakBlockEvent event = new BreakBlockEvent(ItemStack.EMPTY, pos, blockType);
                entityStore.invoke(ref, event);
            }
        }
    }

    public void harvestEternal(Ref<EntityStore> ref, Store<EntityStore> entityStore, World world, Vector3i targetBlock, BlockType blockType) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);

        if  (chunkRef == null || !chunkRef.isValid()) return;

        BlockChunk blockChunkComponent = chunkStore.getStore().getComponent(chunkRef, BlockChunk.getComponentType());
        BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(targetBlock.y);

        int rotationIndex = blockSection.getRotationIndex(targetBlock.x, targetBlock.y, targetBlock.z);
        FarmingUtil.harvest(world, entityStore, ref, blockType, rotationIndex, targetBlock);

        BreakBlockEvent event = new BreakBlockEvent(ItemStack.EMPTY, targetBlock, blockType);
        entityStore.invoke(ref, event);
    }
}
