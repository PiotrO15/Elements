package me.verdo.elements.spells;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SpellProjectileMissInteraction extends SimpleInstantInteraction {
  public static final BuilderCodec<SpellProjectileMissInteraction> CODEC = BuilderCodec
      .builder(SpellProjectileMissInteraction.class, SpellProjectileMissInteraction::new)
      .documentation("Projectile miss cleanup for spells").build();

  @Override
  protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext interactionContext,
      @Nonnull CooldownHandler cooldown) {

    CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
    if (commandBuffer == null) {
      System.out.println("No command buffer found in projectile miss interaction context.");
      return;
    }

    Ref<EntityStore> projectileRef = interactionContext.getEntity();
    if (projectileRef == null) {
      System.out.println("No projectile entity found in projectile miss interaction context.");
      return;
    }

    commandBuffer.run((writeStore) -> writeStore.removeEntity(projectileRef, RemoveReason.REMOVE));
  }
}