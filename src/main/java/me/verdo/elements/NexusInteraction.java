package me.verdo.elements;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.display.ItemDisplayManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NexusInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<NexusInteraction> CODEC = BuilderCodec.builder(NexusInteraction.class, NexusInteraction::new).documentation("Interact with Rootbound Nexus").build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType interactionType, @Nonnull InteractionContext context, @Nullable ItemStack itemStack, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
        if (chunk == null) {
            return;
        }

        int localX = ChunkUtil.localCoordinate(targetBlock.x);
        int localZ = ChunkUtil.localCoordinate(targetBlock.z);
        Ref<ChunkStore> chunkStoreRef = chunk.getBlockComponentEntity(localX, targetBlock.y, localZ);

        if (world.getBlockType(targetBlock) == null)
            return;

        int shift = world.getBlockType(targetBlock).getId().equals("Rootbound_Nexus") ? 1 : 0;

        if (chunkStoreRef == null) {
            ElementsPlugin.LOGGER.atSevere().log("StoreEssenceInteraction failed: Essence Jar has no components.");
            return;
        }

        BlockState nexusState = world.getState(targetBlock.x, targetBlock.y, targetBlock.z, true);

        if (nexusState instanceof ItemContainerState containerState) {
            Ref<EntityStore> ref = context.getEntity();
            Entity entity = EntityUtils.getEntity(ref, commandBuffer);

            if (entity instanceof Player player) {
                ItemStack heldItem = context.getHeldItem();
                if (heldItem != null && heldItem.getItemId().equals("Copper_Wand")) {
                    return;
                }

                if (context.getHeldItem() != null && containerState.getItemContainer().canAddItemStack(context.getHeldItem())) {
                    containerState.getItemContainer().addItemStack(context.getHeldItem());

                    player.getInventory().getCombinedHotbarFirst().removeItemStackFromSlot(player.getInventory().getActiveHotbarSlot());

                    commandBuffer.run(s -> ItemDisplayManager.createOrUpdateDisplay(containerState, world, targetBlock.x + shift, targetBlock.y + (shift == 1 ? 0 : 0.25), targetBlock.z + shift, chunkStoreRef));
                } else {
                    ItemStack stored = containerState.getItemContainer().getItemStack((short) 0);
                    if (stored != null) {
                        if (player.getInventory().getCombinedHotbarFirst().canAddItemStack(stored)) {
                            player.getInventory().getCombinedHotbarFirst().addItemStack(stored);
                            containerState.getItemContainer().removeItemStack(stored);
                            commandBuffer.run(s -> ItemDisplayManager.createOrUpdateDisplay(containerState, world, targetBlock.x + shift, targetBlock.y + (shift == 1 ? 0 : 0.25), targetBlock.z + shift, chunkStoreRef));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nullable ItemStack itemStack, @Nonnull World world, @Nonnull Vector3i vector3i) {

    }
}
