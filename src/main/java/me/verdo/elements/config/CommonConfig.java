package me.verdo.elements.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class CommonConfig {
    public static final BuilderCodec<CommonConfig> CODEC = BuilderCodec.builder(CommonConfig.class, CommonConfig::new)
            .append(new KeyedCodec<>("MaxEssenceStorage", Codec.INTEGER),
                    (config, value) -> config.maxEssenceStorage = value,
                    (config) -> config.maxEssenceStorage).add()
            .build();

    private int maxEssenceStorage = 250;

    public CommonConfig() {}

    public int getMaxEssenceStorage() {
        return maxEssenceStorage;
    }
}
