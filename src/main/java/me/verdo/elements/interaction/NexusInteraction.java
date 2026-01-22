package me.verdo.elements.interaction;

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
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.display.ItemDisplayManager;
import me.verdo.elements.item.ItemPedestalState;
import me.verdo.elements.recipe.RootboundCraftingRecipe;
import me.verdo.elements.util.ModChunkUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NexusInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<NexusInteraction> CODEC = BuilderCodec.builder(NexusInteraction.class, NexusInteraction::new).documentation("Interact with Rootbound Nexus").build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType interactionType, @Nonnull InteractionContext context, @Nullable ItemStack itemStack, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        Ref<ChunkStore> chunkStoreRef = ModChunkUtil.getBlockComponentEntity(world, targetBlock);
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));

        if (chunk == null)
            return;

        if (world.getBlockType(targetBlock) == null)
            return;

        int shift = world.getBlockType(targetBlock).getId().equals("Rootbound_Nexus") ? 1 : 0;

        if (chunkStoreRef == null) {
            ElementsPlugin.LOGGER.atSevere().log("StoreEssenceInteraction failed: Essence Jar has no components.");
            return;
        }

        BlockState nexusState = world.getState(targetBlock.x, targetBlock.y, targetBlock.z, true);

        List<String> supportedBlocks = List.of("Rootbound_Nexus", "Rootbound_Pedestal");
        if (!supportedBlocks.contains(world.getBlockType(targetBlock).getId())) {
            return;
        }

        StoredItemComponent storedItem = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().storedItem);

        Ref<EntityStore> ref = context.getEntity();
        Entity entity = EntityUtils.getEntity(ref, commandBuffer);

        if (entity instanceof Player player) {
            ItemStack heldItem = context.getHeldItem();
            if (heldItem != null && heldItem.getItemId().equals("Copper_Wand")) {
                RootboundCraftingRecipe.craft(nexusState, commandBuffer);
                return;
            }

            if (context.getHeldItem() != null && storedItem.getStoredItem().isEmpty()) {
                storedItem.setStoredItem(context.getHeldItem().withQuantity(1));
                commandBuffer.run(_ -> ItemDisplayManager.createOrUpdateDisplay(storedItem, world, targetBlock.x, targetBlock.y, targetBlock.z, chunkStoreRef));
                chunk.markNeedsSaving();

                player.getInventory().getCombinedHotbarFirst().removeItemStackFromSlot(player.getInventory().getActiveHotbarSlot(), 1);
            } else {
                ItemStack stored = storedItem.getStoredItem();
                if (stored != null) {
                    if (player.getInventory().getCombinedHotbarFirst().canAddItemStack(stored)) {
                        player.getInventory().getCombinedHotbarFirst().addItemStack(stored);
                        storedItem.setStoredItem(ItemStack.EMPTY);
                        System.out.println("Removing display for item UUID: " + storedItem.getDisplayedItemUUID());
                        commandBuffer.run(_ -> ItemDisplayManager.removeDisplayEntity(world, chunkStoreRef, chunk));
                    }
                }
            }
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nullable ItemStack itemStack, @Nonnull World world, @Nonnull Vector3i vector3i) {

    }
}
