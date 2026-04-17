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
import me.verdo.elements.asset.entrypage.EntryPage;

public class KnowledgeBookEntry implements JsonAssetWithMap<String, DefaultAssetMap<String, KnowledgeBookEntry>> {
    private EntryPage[] pages;

    public static final AssetBuilderCodec<String, KnowledgeBookEntry> CODEC;
    private static AssetStore<String, KnowledgeBookEntry, DefaultAssetMap<String, KnowledgeBookEntry>> ASSET_STORE;
    protected String id;
    private AssetExtraInfo.Data data;

    @Override
    public String getId() {
        return id;
    }

    public static AssetStore<String, KnowledgeBookEntry, DefaultAssetMap<String, KnowledgeBookEntry>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(KnowledgeBookEntry.class);
        }

        return ASSET_STORE;
    }

    public EntryPage getPage(int page) {
        return pages[page];
    }

    public int getBookSize() {
        return pages.length;
    }

    public boolean hasNext(int pageGroup) {
        if (pages == null || pages.length == 0)
            return false;

        return (pageGroup + 1) * 2 < pages.length;
    }

    public boolean hasPrevious(int pageGroup) {
        if (pages == null || pages.length == 0)
            return false;

        return pageGroup > 0;
    }

    static {
        CODEC = AssetBuilderCodec.builder(KnowledgeBookEntry.class, KnowledgeBookEntry::new, Codec.STRING,
                (recipe, s) -> recipe.id = s, (recipe) -> recipe.id,
                (asset, data) -> asset.data = data, (asset) -> asset.data
        )
                .append(new KeyedCodec<>("Pages", new ArrayCodec<>(EntryPage.CODEC, EntryPage[]::new), true), (knowledgeBookEntry, strings) -> knowledgeBookEntry.pages = strings, knowledgeBookEntry -> knowledgeBookEntry.pages).add()
                .build();
    }
}
