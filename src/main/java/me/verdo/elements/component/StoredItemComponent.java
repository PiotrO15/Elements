package me.verdo.elements.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nullable;
import java.util.UUID;

public class StoredItemComponent implements Component<ChunkStore> {
    public static final BuilderCodec<StoredItemComponent> CODEC;
    private ItemStack storedItem;
    private UUID displayedItemUUID;

    public StoredItemComponent() {
        storedItem = ItemStack.EMPTY;
    }

    public StoredItemComponent(ItemStack storedItem, UUID displayedItemUUID) {
        this.storedItem = storedItem;
        this.displayedItemUUID = displayedItemUUID;
    }

    @Nullable
    @Override
    public Component<ChunkStore> clone() {
        return new StoredItemComponent(storedItem, displayedItemUUID);
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public void setStoredItem(ItemStack storedItem) {
        this.storedItem = storedItem;
    }

    public UUID getDisplayedItemUUID() {
        return displayedItemUUID;
    }

    public void setDisplayedItemUUID(UUID displayedItemUUID) {
        this.displayedItemUUID = displayedItemUUID;
    }

    static {
        CODEC = BuilderCodec.builder(StoredItemComponent.class, StoredItemComponent::new)
                .append(new KeyedCodec<>("StoredItem", ItemStack.CODEC, true), (c, s) -> c.storedItem = s, (c) -> c.storedItem).add()
                .append(new KeyedCodec<>("StoredUUID", Codec.UUID_STRING, true), (c, u) -> c.displayedItemUUID = u, (c) -> c.displayedItemUUID).add()
                .build();
    }
}
