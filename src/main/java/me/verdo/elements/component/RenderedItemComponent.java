package me.verdo.elements.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nullable;
import java.util.UUID;

public class RenderedItemComponent implements Component<ChunkStore> {
    public static final BuilderCodec<RenderedItemComponent> CODEC;

    private UUID storedUUID;

    public RenderedItemComponent() {
        storedUUID = null;
    }

    public RenderedItemComponent(UUID storedUUID) {
        this.storedUUID = storedUUID;
    }

    @Nullable
    @Override
    public Component<ChunkStore> clone() {
        return new RenderedItemComponent(storedUUID);
    }

    public UUID getStoredUUID() {
        return storedUUID;
    }

    public void setStoredUUID(UUID storedUUID) {
        this.storedUUID = storedUUID;
    }

    static {
        CODEC = BuilderCodec.builder(RenderedItemComponent.class, RenderedItemComponent::new)
                .addField(new KeyedCodec<>("StoredUUID", Codec.UUID_STRING, true), (c, u) -> c.storedUUID = u, (c) -> c.storedUUID)
                .build();

    }
}
