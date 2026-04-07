package me.verdo.elements.system;

import com.hypixel.hytale.builtin.crafting.component.ProcessingBenchBlock;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.EssenceType;
import me.verdo.elements.component.EssenceStorageComponent;
import me.verdo.elements.component.EssenceExtractorBlock;
import me.verdo.elements.interaction.StoreEssenceInteraction;
import me.verdo.elements.util.ModChunkUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.List;

public class EssenceTransferSystem extends EntityTickingSystem<ChunkStore> {
    private final ComponentType<ChunkStore, EssenceExtractorBlock> componentType;
    private static final int TRANSFER_COOLDOWN = 20;

    public EssenceTransferSystem(@Nonnull ComponentType<ChunkStore, EssenceExtractorBlock> componentType) {
        this.componentType = componentType;
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        BlockModule.BlockStateInfo blockStateInfo = archetypeChunk.getComponent(i, BlockModule.BlockStateInfo.getComponentType());

        if (blockStateInfo == null) {
            return;
        }

        Vector3i blockPos = ModChunkUtil.getBlockPosFromIndex(blockStateInfo);
        World world = store.getExternalData().getWorld();

        if (world.getTick() % TRANSFER_COOLDOWN != 0) {
            return;
        }

        WorldChunk worldChunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(blockPos.x, blockPos.z));

        if (worldChunk == null) {
            return;
        }

        ProcessingBenchBlock processingBenchBlock = archetypeChunk.getComponent(i, ProcessingBenchBlock.getComponentType());

        ItemContainer outputContainer = processingBenchBlock.getItemContainer().getContainer(2);

        if (outputContainer.isEmpty()) {
            return;
        }

        BlockType blockType = world.getBlockType(blockPos.clone().add(0, 2, 0));

        if (blockType != null && blockType.getId().contains("Essence_Pipe")) {
            List<Vector3i> jars = EssencePipeSystem.findConnectedJars(world, blockPos.clone().add(0, 2, 0));

            for (int slot = 0; slot < outputContainer.getCapacity(); slot++) {
                ItemStack stack = outputContainer.getItemStack((short) slot);
                if (stack == null || stack.isEmpty()) {
                    continue;
                }

                for (Vector3i jarPos : jars) {
                    Ref<ChunkStore> chunkStoreRef = ModChunkUtil.getBlockComponentEntity(world, jarPos);

                    if (chunkStoreRef == null) {
                        continue;
                    }

                    EssenceStorageComponent c = chunkStoreRef.getStore().getComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage);

                    if (c == null) {
                        continue;
                    }

                    int amountToTransfer = Math.min(Math.min(stack.getQuantity(), ElementsPlugin.get().getCommonConfig().get().getMaxEssenceStorage() - c.getStoredEssenceAmount()), 2);

                    if (c.storeEssence(EssenceType.fromId(stack.getItemId()), amountToTransfer)) {
                        outputContainer.removeItemStackFromSlot((short) slot, amountToTransfer);
                        commandBuffer.replaceComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage, c);
                        StoreEssenceInteraction.displayEssence(worldChunk, jarPos, c);
                        worldChunk.markNeedsSaving();

                        return;
                    }
                }
            }
        }
    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return this.componentType;
    }

    @Override
    public void onSystemRegistered() {
        super.onSystemRegistered();
    }
}
