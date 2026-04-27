package me.verdo.elements.spells;

import java.util.List;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;

public class SpellProjectileImpactInteraction extends SimpleInstantInteraction {
  public static final BuilderCodec<SpellProjectileImpactInteraction> CODEC = BuilderCodec
      .builder(SpellProjectileImpactInteraction.class, SpellProjectileImpactInteraction::new)
      .documentation("Projectile impact interaction for spells").build();

  @Override
  protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext interactionContext,
      @Nonnull CooldownHandler cooldown) {

    // printDebugInfo(interactionContext);

    CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
    if (commandBuffer == null) {
      System.out.println("No command buffer found in interaction context.");
      return;
    }

    Ref<EntityStore> owningEntityRef = interactionContext.getOwningEntity();
    if (owningEntityRef == null) {
      System.out.println("No owning entity found in interaction context.");
      return;
    }

    Ref<EntityStore> projectileRef = interactionContext.getEntity();
    if (projectileRef == null) {
      System.out.println("No projectile entity found in interaction context.");
      return;
    }

    Ref<EntityStore> targetRef = interactionContext.getTargetEntity();
    if (targetRef == null) {
        System.out.println("No target entity found in interaction context.");
        return;
    }

    commandBuffer.run((writeStore) -> {
      SpellProjectileComponent projectileComponent = writeStore.getComponent(projectileRef, ElementsPlugin.get().spellProjectileComponent);
      if (projectileComponent == null || !projectileComponent.hasSpell()) {
        System.out.println("Projectile does not have spell metadata.");
        return;
      }

      System.out.println("Found projectile component with spell metadata: " + projectileComponent.getSpell().getName());
      SpellDefinition spell = projectileComponent.getSpell();

      SpellCastResolver.applySpellOnce(
        writeStore,
        owningEntityRef,
        spell,
          List.of(targetRef)// TODO: determine targets based on hit area/effect radius
      );
    });
  }
  
  private static void printDebugInfo(InteractionContext interactionContext) {
    System.out.println("Spell projectile hit something!");
    System.out.println("Interaction context: " + interactionContext);
    System.out.println("Owning entity: " + interactionContext.getOwningEntity());
    System.out.println("Entity: " + interactionContext.getEntity());
    System.out.println("Target entity: " + interactionContext.getTargetEntity());
  }
}
