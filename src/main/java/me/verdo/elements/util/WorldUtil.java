package me.verdo.elements.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;

public class WorldUtil {
    public static void dropItems(World world, Vector3i pos, List<ItemStack> items) {
        Vector3d dropPosition = new Vector3d(pos.x + 0.5, pos.y, pos.z + 0.5);
        Store<EntityStore> store = world.getEntityStore().getStore();

        Holder<EntityStore>[] itemEntityHolders = ItemComponent.generateItemDrops(store, items, dropPosition, Vector3f.ZERO);
        if (itemEntityHolders.length > 0) {
            world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
        }
    }

    public static void dropItem(World world, Vector3i pos, ItemStack item) {
        dropItems(world, pos, List.of(item));
    }
}
