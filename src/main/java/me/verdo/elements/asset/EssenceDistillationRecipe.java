package me.verdo.elements.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import me.verdo.elements.component.EssenceStorageComponent;

public class EssenceDistillationRecipe implements JsonAssetWithMap<String, DefaultAssetMap<String, EssenceDistillationRecipe>> {
    protected MaterialQuantity input;
    protected EssenceStorageComponent[] products;
    protected String id;

    public static final AssetBuilderCodec<String, EssenceDistillationRecipe> CODEC;
    private static AssetStore<String, EssenceDistillationRecipe, DefaultAssetMap<String, EssenceDistillationRecipe>> ASSET_STORE;
    private AssetExtraInfo.Data data;

    public static AssetStore<String, EssenceDistillationRecipe, DefaultAssetMap<String, EssenceDistillationRecipe>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(EssenceDistillationRecipe.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, EssenceDistillationRecipe> getAssetMap() {
        return getAssetStore().getAssetMap();
    }

    @Override
    public String getId() {
        return id;
    }

    static {
        CODEC = AssetBuilderCodec.builder(EssenceDistillationRecipe.class, EssenceDistillationRecipe::new, Codec.STRING, (recipe, s) -> recipe.id = s, (recipe) -> recipe.id, (asset, data) -> asset.data = data, (asset) -> asset.data)
                .append(new KeyedCodec<>("Input", MaterialQuantity.CODEC, true), (r, u) -> r.input = u, (r) -> r.input).addValidator(Validators.nonNull()).add()
                .append(new KeyedCodec<>("Products", new ArrayCodec<>(EssenceStorageComponent.CODEC, EssenceStorageComponent[]::new), true), (r, s) -> r.products = s, (r) -> r.products).addValidator(Validators.nonNull()).add()
                .build();
    }
}
