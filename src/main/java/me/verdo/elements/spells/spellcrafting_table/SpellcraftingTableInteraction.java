package me.verdo.elements.spells.spellcrafting_table;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.display.ItemDisplayManager;
import me.verdo.elements.recipe.RootboundCraftingRecipe;
import me.verdo.elements.util.ModChunkUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SpellcraftingTableInteraction extends SimpleBlockInteraction {
    private final ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType = BlockModule.BlockStateInfo
            .getComponentType();

    public static final BuilderCodec<SpellcraftingTableInteraction> CODEC = BuilderCodec
            .builder(SpellcraftingTableInteraction.class, SpellcraftingTableInteraction::new)
            .documentation("Interaction for opening spell crafting table.").build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer,
            @Nonnull InteractionType interactionType, @Nonnull InteractionContext context,
            @Nullable ItemStack itemStack, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        Ref<ChunkStore> chunkStoreRef = ModChunkUtil.getBlockComponentEntity(world, targetBlock);
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));

        if (chunk == null)
            return;

        if (world.getBlockType(targetBlock) == null)
            return;

        if (chunkStoreRef == null) {
            return;
        }

        BlockModule.BlockStateInfo blockStateInfo = world.getChunkStore().getStore().getComponent(chunkStoreRef,
                this.blockStateInfoComponentType);

        List<String> supportedBlocks = List.of("Spellcrafting_Table");
        if (!supportedBlocks.contains(world.getBlockType(targetBlock).getId())) {
            return;
        }

        StoredItemComponent storedItem = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef,
                ElementsPlugin.get().storedItem);

        Ref<EntityStore> entityRef = context.getEntity();
        Entity entity = EntityUtils.getEntity(entityRef, context.getCommandBuffer());

        if (entity instanceof Player player) {
            CombinedItemContainer inventory = InventoryComponent.getCombined(commandBuffer, entityRef,
                    InventoryComponent.HOTBAR_FIRST);
            ItemStack heldItem = context.getHeldItem();
            if (heldItem != null && heldItem.getItemId().equals("Rootbound_Wand")) {
                RootboundCraftingRecipe.craft(blockStateInfo, targetBlock, commandBuffer);
                return;
            }

            if (context.getHeldItem() != null && storedItem.getStoredItem().isEmpty()) {
                storedItem.setStoredItem(context.getHeldItem().withQuantity(1));
                commandBuffer.run(_ -> ItemDisplayManager.createOrUpdateDisplay(storedItem, world, targetBlock.x,
                        targetBlock.y, targetBlock.z, chunkStoreRef));
                chunk.markNeedsSaving();

                inventory.removeItemStackFromSlot(context.getHeldItemSlot(), 1);

                // open spell crafting UI here
                PlayerRef playerRefComponent = commandBuffer.getStore().getComponent(entityRef, PlayerRef.getComponentType());

                if (playerRefComponent != null) {
                    player.getPageManager().openCustomPage(entityRef, commandBuffer.getStore(), new SpellcraftingScreen(playerRefComponent, storedItem.getStoredItem()));
                }
            } else {
                ItemStack stored = storedItem.getStoredItem();
                if (stored != null) {
                    if (inventory.addItemStack(stored, true, false, true).succeeded()) {
                        storedItem.setStoredItem(ItemStack.EMPTY);
                        commandBuffer.run(_ -> ItemDisplayManager.removeDisplayEntity(world, chunkStoreRef, chunk));
                    }
                }
            }
        }
        System.out.println("Item on spellcrafting table: " + storedItem.toString());
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType interactionType,
            @Nonnull InteractionContext interactionContext, @Nullable ItemStack itemStack, @Nonnull World world,
            @Nonnull Vector3i vector3i) {
    }
}
