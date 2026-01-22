package me.verdo.elements;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.asset.EssenceCraftingRecipe;
import me.verdo.elements.component.ComplexEssenceStorageComponent;
import me.verdo.elements.component.EssenceStorageComponent;
import me.verdo.elements.component.RenderedItemComponent;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.display.BlockBreakDisplayEventSystem;
import me.verdo.elements.system.hud.PlayerHudSystem;
import me.verdo.elements.interaction.NexusInteraction;
import me.verdo.elements.interaction.StoreEssenceInteraction;
import me.verdo.elements.system.BlockBreakEventSystem;
import me.verdo.elements.system.EssencePickupSystem;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class ElementsPlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static ElementsPlugin instance;

    public ComponentType<ChunkStore, EssenceStorageComponent> essenceStorage;
    public ComponentType<EntityStore, ComplexEssenceStorageComponent> storedEssence;
    public ComponentType<ChunkStore, RenderedItemComponent> renderedItem;
    public ComponentType<ChunkStore, StoredItemComponent> storedItem;

    public ElementsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
        instance = this;
    }

    public static ElementsPlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        essenceStorage = getChunkStoreRegistry().registerComponent(EssenceStorageComponent.class, "EssenceStorage", EssenceStorageComponent.CODEC);
        storedEssence = getEntityStoreRegistry().registerComponent(ComplexEssenceStorageComponent.class, "StoredEssence", ComplexEssenceStorageComponent.CODEC);
        renderedItem = getChunkStoreRegistry().registerComponent(RenderedItemComponent.class, "RenderedItem", RenderedItemComponent.CODEC);
        storedItem = getChunkStoreRegistry().registerComponent(StoredItemComponent.class, "StoredItem", StoredItemComponent.CODEC);

        this.getCodecRegistry(Interaction.CODEC).register("StoreEssence", StoreEssenceInteraction.class, StoreEssenceInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("NexusInteraction", NexusInteraction.class, NexusInteraction.CODEC);

        getEntityStoreRegistry().registerSystem(new BlockBreakEventSystem(BreakBlockEvent.class));
        getEntityStoreRegistry().registerSystem(new EssencePickupSystem());
        getEntityStoreRegistry().registerSystem(new PlayerHudSystem());
        getEntityStoreRegistry().registerSystem(new BlockBreakDisplayEventSystem(BreakBlockEvent.class));

        getAssetRegistry().register(HytaleAssetStore.builder(EssenceCraftingRecipe.class, new DefaultAssetMap<>())
                .setPath("EssenceCraftingRecipe")
                .setCodec(EssenceCraftingRecipe.CODEC)
                .setKeyFunction(EssenceCraftingRecipe::getId)
                .build());
    }
}