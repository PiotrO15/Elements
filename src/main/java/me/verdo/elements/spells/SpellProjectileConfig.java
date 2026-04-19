package me.verdo.elements.spells;

import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.soundevent.validator.SoundEventValidators;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;


public class SpellProjectileConfig extends ProjectileConfig {
    // This class can be used to define specific properties for spell projectiles, separate from regular projectiles
    // For example, we could add fields for the spell effect type, element, etc. that can be used when the projectile hits something

    @Nonnull
    // public static final AssetBuilderCodec<String, SpellProjectileConfig> CODEC;

    public SpellProjectileConfig() {
        super();
    }

    @Nonnull
    public static DefaultAssetMap<String, ProjectileConfig> getAssetMap() {
        return (DefaultAssetMap)getAssetStore().getAssetMap();
    }
  //   static {
  //     CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ProjectileConfig.class, ProjectileConfig::new, Codec.STRING, (config, s) -> config.id = s, (config) -> config.id, (asset, data) -> asset.data = data, (asset) -> asset.data).appendInherited(new KeyedCodec("Physics", PhysicsConfig.CODEC), (o, i) -> o.physicsConfig = i, (o) -> o.physicsConfig, (o, p) -> o.physicsConfig = p.physicsConfig).add()).appendInherited(new KeyedCodec("Model", Codec.STRING), (o, i) -> o.model = i, (o) -> o.model, (o, p) -> o.model = p.model).addValidator(Validators.nonNull()).addValidator(ModelAsset.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec("LaunchForce", Codec.DOUBLE), (o, i) -> o.launchForce = i, (o) -> o.launchForce, (o, p) -> o.launchForce = p.launchForce).add()).appendInherited(new KeyedCodec("SpawnOffset", ProtocolCodecs.VECTOR3F), (o, i) -> o.spawnOffset = i, (o) -> o.spawnOffset, (o, p) -> o.spawnOffset = p.spawnOffset).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("SpawnRotationOffset", ProtocolCodecs.DIRECTION), (o, i) -> {
  //        o.spawnRotationOffset = i;
  //        Direction var10000 = o.spawnRotationOffset;
  //        var10000.yaw *= ((float)Math.PI / 180F);
  //        var10000 = o.spawnRotationOffset;
  //        var10000.pitch *= ((float)Math.PI / 180F);
  //        var10000 = o.spawnRotationOffset;
  //        var10000.roll *= ((float)Math.PI / 180F);
  //     }, (o) -> o.spawnRotationOffset, (o, p) -> o.spawnRotationOffset = p.spawnRotationOffset).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("Interactions", new EnumMapCodec(InteractionType.class, RootInteraction.CHILD_ASSET_CODEC)), (o, i) -> o.interactions = i, (o) -> o.interactions, (o, p) -> o.interactions = p.interactions).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getMapValueValidator().late()).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("LaunchLocalSoundEventId", Codec.STRING), (o, i) -> o.launchLocalSoundEventId = i, (o) -> o.launchLocalSoundEventId, (o, p) -> o.launchLocalSoundEventId = p.launchLocalSoundEventId).addValidator(SoundEventValidators.ONESHOT).documentation("The sound event played to the throwing player when the projectile is spawned/launched").add()).appendInherited(new KeyedCodec("LaunchWorldSoundEventId", Codec.STRING), (o, i) -> o.launchWorldSoundEventId = i, (o) -> o.launchWorldSoundEventId, (o, p) -> o.launchWorldSoundEventId = p.launchWorldSoundEventId).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).documentation("The positioned sound event played to surrounding players when the projectile is spawned/launched").add()).appendInherited(new KeyedCodec("ProjectileSoundEventId", Codec.STRING), (o, i) -> o.projectileSoundEventId = i, (o) -> o.projectileSoundEventId, (o, p) -> o.projectileSoundEventId = p.projectileSoundEventId).addValidator(SoundEventValidators.LOOPING).addValidator(SoundEventValidators.MONO).documentation("The looping sound event to attach to the projectile.").add()).afterDecode(ProjectileConfig::processConfig)).build();
  //     VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(ProjectileConfig::getAssetStore));
  //  }

}
