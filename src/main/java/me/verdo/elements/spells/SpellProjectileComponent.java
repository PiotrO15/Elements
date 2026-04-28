package me.verdo.elements.spells;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SpellProjectileComponent implements Component<EntityStore> {
  // This component can be added to a projectile entity to link it back to the
  // spell that created it, allowing us to apply spell effects when the projectile
  // hits something
  public static final BuilderCodec<SpellProjectileComponent> CODEC;
  public static String METADATA_KEY = "spell_projectile";

  private SpellDefinition spell;

  public SpellProjectileComponent() {
    this.spell = null;
  }

  public SpellProjectileComponent(SpellDefinition spell) {
    this.spell = spell;
  }

  public SpellDefinition getSpell() {
    return spell;
  }

  public void setSpell(SpellDefinition spell) {
    this.spell = spell;
  }

  public boolean hasSpell() {
    return spell != null;
  }

  @Override
  public Component clone() {
    return new SpellProjectileComponent(spell);
  }

  static {
    CODEC = BuilderCodec.builder(SpellProjectileComponent.class, SpellProjectileComponent::new)
        .append(new KeyedCodec<>("Spell", SpellDefinition.CODEC, true), (s, o) -> s.spell = o, (s) -> s.spell).add()
        .build();
  }

}
