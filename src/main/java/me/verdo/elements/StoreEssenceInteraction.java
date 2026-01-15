package me.verdo.elements;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
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
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StoreEssenceInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<StoreEssenceInteraction> CODEC = BuilderCodec.builder(StoreEssenceInteraction.class, StoreEssenceInteraction::new).documentation("Store essence using EssenceStorage component").build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType interactionType, @Nonnull InteractionContext context, @Nullable ItemStack itemStack, @Nonnull Vector3i vector3i, @Nonnull CooldownHandler cooldownHandler) {
        int chunkX = ChunkUtil.chunkCoordinate(vector3i.x);
        int chunkZ = ChunkUtil.chunkCoordinate(vector3i.z);

        world.getChunkAsync(chunkX, chunkZ).thenAccept(chunk -> {
            if (chunk == null) {
                return;
            }

            System.out.println(vector3i);
            int localX = ChunkUtil.localCoordinate(vector3i.x);
            int localZ = ChunkUtil.localCoordinate(vector3i.z);
            Ref<ChunkStore> chunkStoreRef = chunk.getBlockComponentEntity(localX, vector3i.y, localZ);
            System.out.println(chunkStoreRef);

            if (chunkStoreRef == null) {
                System.out.println("Attempted interaction for entity " + context.getEntity());
                return;
            }

            System.out.println("A");

            EssenceStorageComponent c = chunkStoreRef.getStore().ensureAndGetComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage);
            System.out.println("Storing " + c.getStoredEssenceAmount() + " of " + c.getStoredEssenceType());
            if (c.canStore(context.getHeldItem())) {
                System.out.println("B");

                if (context.getHeldItem() == null) {
                    return;
                }

                c.setStoredEssenceType(EssenceType.fromId(context.getHeldItem().getItemId()));
                c.setStoredEssenceAmount(context.getHeldItem().getQuantity() + c.getStoredEssenceAmount());

                chunkStoreRef.getStore().replaceComponent(chunkStoreRef, ElementsPlugin.get().essenceStorage, c);

                Ref<EntityStore> ref = context.getEntity();
                Entity entity = EntityUtils.getEntity(ref, commandBuffer);
                if (entity instanceof Player player) {
                    player.getInventory().getCombinedHotbarFirst().removeItemStackFromSlot(player.getInventory().getActiveHotbarSlot());
                }

                System.out.println("Inserted " + c.getStoredEssenceAmount() + " of " + c.getStoredEssenceType());
            }
        });
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nullable ItemStack itemStack, @Nonnull World world, @Nonnull Vector3i vector3i) {

    }
}
