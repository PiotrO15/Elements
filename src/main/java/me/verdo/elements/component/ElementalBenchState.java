package me.verdo.elements.component;

import com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.EssenceType;
import me.verdo.elements.interaction.StoreEssenceInteraction;
import me.verdo.elements.system.EssencePipeSystem;
import me.verdo.elements.util.ModChunkUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.List;

public class ElementalBenchState extends ProcessingBenchState {
    public static final BuilderCodec<ElementalBenchState> CODEC;
    private static final int TRANSFER_COOLDOWN = 20;

    static {
        CODEC = BuilderCodec.builder(ElementalBenchState.class, ElementalBenchState::new, (BuilderCodec<ProcessingBenchState>) ProcessingBenchState.CODEC).build();
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return super.clone();
    }

    @Override
    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        super.tick(dt, index, archetypeChunk, store, commandBuffer);

        if (getChunk().getWorld().getTick() % TRANSFER_COOLDOWN != 0) {
            return;
        }

        ItemContainer outputContainer = getItemContainer().getContainer(2);

        if (outputContainer.isEmpty()) {
            return;
        }

        if (getChunk().getWorld().getBlockType(getBlockPosition().add(0, 2, 0)).getId().contains("Essence_Pipe")) {
            List<Vector3i> jars = EssencePipeSystem.findConnectedJars(getChunk().getWorld(), getBlockPosition().add(0, 2, 0));

            for (int slot = 0; slot < outputContainer.getCapacity(); slot++) {
                ItemStack stack = outputContainer.getItemStack((short) slot);
                if (stack == null || stack.isEmpty()) {
                    continue;
                }

                for (Vector3i jarPos : jars) {
                    Ref<ChunkStore> chunkStoreRef = ModChunkUtil.getBlockComponentEntity(getChunk().getWorld(), jarPos);

                    if (chunkStoreRef == null) {
                        continue;
                    }

                    EssenceStorageComponent c = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage);

                    int amountToTransfer = Math.min(Math.min(stack.getQuantity(), ElementsPlugin.get().getCommonConfig().get().getMaxEssenceStorage() - c.getStoredEssenceAmount()), 2);

                    if (c.storeEssence(EssenceType.fromId(stack.getItemId()), amountToTransfer)) {
                        outputContainer.removeItemStackFromSlot((short) slot, amountToTransfer);
                        chunkStoreRef.getStore().replaceComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage, c);
                        StoreEssenceInteraction.displayEssence(getChunk(), jarPos, c);
                        getChunk().markNeedsSaving();

                        return;
                    }
                }
            }
        }
    }
}
