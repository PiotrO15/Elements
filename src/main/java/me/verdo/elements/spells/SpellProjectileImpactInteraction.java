package me.verdo.elements.spells;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

public class SpellProjectileImpactInteraction extends SimpleInstantInteraction {
  public static final BuilderCodec<SpellProjectileImpactInteraction> CODEC = BuilderCodec
      .builder(SpellProjectileImpactInteraction.class, SpellProjectileImpactInteraction::new)
      .documentation("Projectile impact interaction for spells").build();

  @Override
  protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext interactionContext,
      @Nonnull CooldownHandler cooldown) {

      System.out.println("Spell projectile hit something! Interaction context: " + interactionContext);
  }
  
}
