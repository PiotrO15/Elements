package me.verdo.elements.spells;

import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

// TODO: Remove? Currently unused
public class SpellProjectileConfig extends ProjectileConfig  {
    // This class can be used to define specific properties for spell projectiles, separate from regular projectiles
    // For example, we could add fields for the spell effect type, element, etc. that can be used when the projectile hits something

    @Nonnull
    // public static final AssetBuilderCodec<String, SpellProjectileConfig> CODEC;
    private static AssetStore<String, ProjectileConfig, DefaultAssetMap<String, ProjectileConfig>> ASSET_STORE;


    public SpellProjectileConfig() {
        super();
        InteractionType spellProjectileImpactInteractionType = InteractionType.ProjectileHit; // TODO: add more

        interactions = Collections.singletonMap(spellProjectileImpactInteractionType, "SpellProjectileImpactInteraction"); // Spells will handle interactions manually in code, so we can ignore interactions defined in the config for now
    }

    public void setInteractions(Map<InteractionType, String> interactions) {
        this.interactions = interactions;
    }

    @Nonnull
    public static AssetStore<String, ProjectileConfig, DefaultAssetMap<String, ProjectileConfig>> getAssetStore() {
      if (ASSET_STORE == null) {
          ASSET_STORE = AssetRegistry.getAssetStore(ProjectileConfig.class);
      }

      return ASSET_STORE;
    }

    public static DefaultAssetMap<String, ProjectileConfig> getAssetMap() {
        return getAssetStore().getAssetMap();
    }
}
