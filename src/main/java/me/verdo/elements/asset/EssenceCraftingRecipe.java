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

public class EssenceCraftingRecipe implements JsonAssetWithMap<String, DefaultAssetMap<String, EssenceCraftingRecipe>> {
    protected EssenceStorageComponent[] essenceInputs;
    protected MaterialQuantity mainInput;
    protected MaterialQuantity[] pedestalInputs;
    protected MaterialQuantity output;
    protected int outputQuantity;
    protected String id;

    public static final AssetBuilderCodec<String, EssenceCraftingRecipe> CODEC;
    private static AssetStore<String, EssenceCraftingRecipe, DefaultAssetMap<String, EssenceCraftingRecipe>> ASSET_STORE;
    private AssetExtraInfo.Data data;

    public static AssetStore<String, EssenceCraftingRecipe, DefaultAssetMap<String, EssenceCraftingRecipe>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(EssenceCraftingRecipe.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, EssenceCraftingRecipe> getAssetMap() {
        return getAssetStore().getAssetMap();
    }

    @Override
    public String getId() {
        return id;
    }

    public MaterialQuantity[] getPedestalInputs() {
        return pedestalInputs;
    }

    public EssenceStorageComponent[] getEssenceInputs() {
        return essenceInputs;
    }

    public MaterialQuantity getMainInput() {
        return mainInput;
    }

    public MaterialQuantity getOutput() {
        return output;
    }

    static {
        CODEC = AssetBuilderCodec.builder(EssenceCraftingRecipe.class, EssenceCraftingRecipe::new, Codec.STRING, (recipe, s) -> recipe.id = s, (recipe) -> recipe.id, (asset, data) -> asset.data = data, (asset) -> asset.data)
                .addField(new KeyedCodec<>("PedestalInputs", new ArrayCodec<>(MaterialQuantity.CODEC, MaterialQuantity[]::new), true), (r, o) -> r.pedestalInputs = o, (r) -> r.pedestalInputs).validator(Validators.nonNull())
                .addField(new KeyedCodec<>("MainInput", MaterialQuantity.CODEC, true), (r, u) -> r.mainInput = u, (r) -> r.mainInput).validator(Validators.nonNull())
                .addField(new KeyedCodec<>("EssenceInputs", new ArrayCodec<>(EssenceStorageComponent.CODEC, EssenceStorageComponent[]::new), true), (r, s) -> r.essenceInputs = s, (r) -> r.essenceInputs).validator(Validators.nonNull())
                .addField(new KeyedCodec<>("Output", MaterialQuantity.CODEC, true), (r, u) -> r.output = u, (r) -> r.output).validator(Validators.nonNull())
                .build();
    }
}
