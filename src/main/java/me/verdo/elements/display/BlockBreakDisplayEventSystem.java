package me.verdo.elements.display;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.util.ModChunkUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockBreakDisplayEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public BlockBreakDisplayEventSystem(@Nonnull Class<BreakBlockEvent> eventType) {
        super(eventType);
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull BreakBlockEvent event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(ref, Player.getComponentType());

        if (player == null)
            return;

        World world = player.getWorld();

        Ref<ChunkStore> chunkStoreRef = ModChunkUtil.getBlockComponentEntity(world, event.getTargetBlock());

        if (chunkStoreRef == null)
            return;

        StoredItemComponent storedItem = chunkStoreRef.getStore().getComponent(chunkStoreRef, ElementsPlugin.get().storedItem);
        if (storedItem != null) {
            System.out.println(storedItem.getStoredItem().getItemId());
            System.out.println(storedItem.getDisplayedItemUUID());
            List<ItemStack> allItemStacks = List.of(storedItem.getStoredItem());
            Vector3d dropPosition = event.getTargetBlock().toVector3d().add(0.5F, 0.0F, 0.5F);
            Holder<EntityStore>[] itemEntityHolders = ItemComponent.generateItemDrops(store, allItemStacks, dropPosition, Vector3f.ZERO);
            commandBuffer.run(_ -> ItemDisplayManager.removeDisplayEntity(world, storedItem.getDisplayedItemUUID()));
            world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
