package me.verdo.elements.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EssenceCollectorComponent implements Component<ChunkStore> {
    public static final BuilderCodec<EssenceCollectorComponent> CODEC;

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new  EssenceCollectorComponent();
    }

    static {
        CODEC = BuilderCodec.builder(EssenceCollectorComponent.class, EssenceCollectorComponent::new).build();
    }
}
