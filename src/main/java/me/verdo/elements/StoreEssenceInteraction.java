package me.verdo.elements;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.component.EssenceStorageComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StoreEssenceInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<StoreEssenceInteraction> CODEC = BuilderCodec.builder(StoreEssenceInteraction.class, StoreEssenceInteraction::new).documentation("Store essence using EssenceStorage component").build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType interactionType, @Nonnull InteractionContext context, @Nullable ItemStack itemStack, @Nonnull Vector3i vector3i, @Nonnull CooldownHandler cooldownHandler) {
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(vector3i.x, vector3i.z));
        if (chunk == null) {
            return;
        }

        int localX = ChunkUtil.localCoordinate(vector3i.x);
        int localZ = ChunkUtil.localCoordinate(vector3i.z);
        Ref<ChunkStore> chunkStoreRef = chunk.getBlockComponentEntity(localX, vector3i.y, localZ);

        if (chunkStoreRef == null) {
            ElementsPlugin.LOGGER.atSevere().log("StoreEssenceInteraction failed: Essence Jar has no components.");
            return;
        }

        EssenceStorageComponent c = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage);

        Ref<EntityStore> ref = context.getEntity();
        Entity entity = EntityUtils.getEntity(ref, commandBuffer);

        if (entity instanceof Player player) {
            ItemStack heldItem = context.getHeldItem();
            if (heldItem != null && c.canStore(heldItem)) {
                int toStore = Math.min(100 - c.getStoredEssenceAmount(), heldItem.getQuantity());

                c.setStoredEssenceType(EssenceType.fromId(heldItem.getItemId()));
                c.setStoredEssenceAmount(c.getStoredEssenceAmount() + toStore);

                chunkStoreRef.getStore().replaceComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage, c);
                displayEssence(chunk, vector3i, c);

                player.getInventory().getCombinedHotbarFirst().removeItemStackFromSlot(player.getInventory().getActiveHotbarSlot(), toStore);
                chunk.markNeedsSaving();
            } else {
                MovementStatesComponent component = world.getEntityStore().getStore().getComponent(player.getReference(), MovementStatesComponent.getComponentType());
                if (component != null && !component.getMovementStates().crouching) {
                    if (c.getStoredEssenceType() == null || c.getStoredEssenceAmount() == 0) {
                        return;
                    }

                    ItemStack drop = new ItemStack(c.getStoredEssenceType().getItemId());
                    drop = drop.withQuantity(c.getStoredEssenceAmount());

                    if (drop == null)
                        return;

                    if (player.getInventory().getCombinedHotbarFirst().canAddItemStack(drop)) {
                        c.setStoredEssenceType(null);
                        c.setStoredEssenceAmount(0);

                        chunkStoreRef.getStore().replaceComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage, c);

                        player.getInventory().getCombinedHotbarFirst().addItemStack(drop);
                        displayEssence(chunk, vector3i, c);
                        chunk.markNeedsSaving();
                    }
                    return;
                }

                player.sendMessage(Message.raw("Storage: " + c.getStoredEssenceAmount() + "/100"));
            }
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nullable ItemStack itemStack, @Nonnull World world, @Nonnull Vector3i vector3i) {

    }

    private void displayEssence(WorldChunk chunk, Vector3i targetBlock, EssenceStorageComponent c) {
        if (c.getStoredEssenceAmount() == 0 || c.getStoredEssenceType() == null) {
            setState(chunk, targetBlock, "0");
            return;
        }

        StringBuilder newState = new StringBuilder();
        newState.append(c.getStoredEssenceType().getItemId()).append("_");
        switch (c.getStoredEssenceAmount() / 33 + 1) {
            case 1:
                if (c.getStoredEssenceAmount() > 0)
                    newState.append("25");
                else
                    newState.append("0");
                break;
            case 2:
                newState.append("50");
                break;
            case 3:
                newState.append("75");
                break;
            case 4:
                if (c.getStoredEssenceAmount() != 100) {
                    newState.append("75");
                } else {
                    newState.append("100");
                }
                break;
        }

        setState(chunk, targetBlock, newState.toString());
    }

    private void setState(WorldChunk chunk, Vector3i targetBlock, String newState) {
        BlockType current = chunk.getBlockType(targetBlock);
        if (current == null)
            return;

        String newBlock = current.getBlockKeyForState(newState);

        if (newBlock != null) {
            int newBlockId = BlockType.getAssetMap().getIndex(newBlock);
            if (newBlockId == Integer.MIN_VALUE) {
                System.out.println("State change failed");
                return;
            }

            BlockType newBlockType = BlockType.getAssetMap().getAsset(newBlockId);
            if (newBlockType == null)
                return;

            int rotation = chunk.getRotationIndex(targetBlock.x, targetBlock.y, targetBlock.z);

            chunk.setBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), newBlockId, newBlockType, rotation, 0, 130);
        }
    }
}
