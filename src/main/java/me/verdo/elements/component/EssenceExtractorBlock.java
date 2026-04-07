package me.verdo.elements.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class EssenceExtractorBlock implements Component<ChunkStore> {
    public static final BuilderCodec<EssenceExtractorBlock> CODEC;

    @Override
    public Component<ChunkStore> clone() {
        return new EssenceExtractorBlock();
    }

    static {
        CODEC = BuilderCodec.builder(EssenceExtractorBlock.class, EssenceExtractorBlock::new).build();
    }
}
