package me.verdo.elements.spells;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.EntitySource;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.ElementsPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class SpellCastResolver {
    private static final long MILLIS_PER_TICK = 50L;

    public static void handleSpellCast(@Nonnull Ref<EntityStore> casterRef, @Nonnull SpellDefinition spell,
            @Nullable Ref<EntityStore> target, @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        List<Ref<EntityStore>> targets = selectTargets(casterRef, spell, target, commandBuffer, store);

        // If no targets found, notify player and exit. For projectile spells we allow
        // no initial targets since they will apply effects on hit.
        if (targets.isEmpty()) {
            if (spell.getTargetType() != SpellTargetType.PROJECTILE) {
                notifyPlayer(store, casterRef, "Spell failed: no valid target.");
            }
            return;
        }

        List<Ref<EntityStore>> finalTargets = expandAoeTargets(targets);

        notifyPlayer(store, casterRef, "Spell hits " + targets.size() + " target(s).");

        // SpellModifierSet modifiers = event.getSpell().getModifiers();

        int casts = 1; // TODO: implement modifiers
        int delayTicks = 0; // TODO: implement modifiers

        World world = store.getExternalData().getWorld();
        for (int castIndex = 0; castIndex < casts; castIndex++) {
            final int iteration = castIndex + 1;
            long delayMs = (long) delayTicks * castIndex * MILLIS_PER_TICK;
            HytaleServer.SCHEDULED_EXECUTOR.schedule(
                    () -> world.execute(() -> applySpellOnce(store, casterRef, spell, finalTargets)),
                    delayMs, TimeUnit.MILLISECONDS);
        }
    }

    private static List<Ref<EntityStore>> selectTargets(Ref<EntityStore> casterRef, SpellDefinition spell,
            @Nullable Ref<EntityStore> initialTarget, CommandBuffer<EntityStore> commandBuffer,
            @Nonnull Store<EntityStore> store) {

        return switch (spell.getTargetType()) {
            case SELF -> List.of(casterRef);
            case TOUCH -> {
                Ref<EntityStore> target = initialTarget;
                if (target == null) {
                    yield List.of();
                } else {
                    yield List.of(target);
                }
            }
            case PROJECTILE -> shootProjectile(casterRef, spell, commandBuffer, store);
            default -> List.of();
        };
    }

    private static List<Ref<EntityStore>> shootProjectile(Ref<EntityStore> casterRef, SpellDefinition spell,
            CommandBuffer<EntityStore> commandBuffer, @Nonnull Store<EntityStore> store) {

        ProjectileConfig config = ProjectileConfig.getAssetMap().getAsset("Default_Spell_Projectile");

        // Get caster position, with direction based on caster look vector
        TransformComponent transform = store.getComponent(casterRef, TransformComponent.getComponentType());
        if (transform == null)
            return List.of();

        Vector3d position = transform.getPosition().clone();
        position.y += 1.6; // Eye height

        HeadRotation headRotation = store.getComponent(casterRef, HeadRotation.getComponentType());
        Vector3d direction = headRotation.getDirection();

        // Create projectile
        ProjectileModule module = ProjectileModule.get();
        Ref<EntityStore> projectileRef;

        try {
            projectileRef = module.spawnProjectile(
                    casterRef,
                    commandBuffer,
                    config,
                    position,
                    direction);

        } catch (Exception ex) {
            System.out.println("Failed to spawn projectile: " + ex.getMessage());
            return List.of();
        }

        // Attach spell data to projectile so we can apply effects on hit
        SpellProjectileComponent spellProjectileComponent = new SpellProjectileComponent(spell);
        commandBuffer.run((writeStore) -> writeStore.addComponent(projectileRef,
                ElementsPlugin.get().spellProjectileComponent, spellProjectileComponent));

        // Wait until the projectile hits something, no target selection for now
        return List.of();
    }

    private static List<Ref<EntityStore>> expandAoeTargets(@Nonnull List<Ref<EntityStore>> selectedTargets) {
        int aoeIncrease = 0;
        if (aoeIncrease <= 0) {
            return selectedTargets;
        }

        // TODO: Implement AOE target expansion logic
        return selectedTargets;
    }

    public static void applySpellOnce(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> casterRef,
            @Nonnull SpellDefinition spell,
            @Nonnull List<Ref<EntityStore>> targets) {
        switch (spell.getEffectType()) {
            case DAMAGE -> {
                handleDamageApplication(store, casterRef, spell, targets);
            }
            case HEAL -> {
                handleHealingApplication(store, casterRef, spell, targets);
            }
            case DEBUFF -> {
                for (Ref<EntityStore> target : targets) {
                    notifyPlayer(store, target, "You are afflicted for " + spell.getDurationTicks() + " ticks.");
                }
            }
            default -> {
                System.out.println("Unhandled spell effect type: " + spell.getEffectType());
            }
        }

        notifyPlayer(store, casterRef,
                "Cast " + spell.getName() + " hit " + targets.size() + " target(s).");
    }

    private static void handleDamageApplication(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> casterRef,
            @Nonnull SpellDefinition spell,
            @Nonnull List<Ref<EntityStore>> targets) {
        for (Ref<EntityStore> target : targets) {
            notifyPlayer(store, target, "You take " + spell.getStrength() + " spell damage.");

            EntitySource damageSource = new EntitySource(casterRef);

            Damage damage = new Damage(damageSource, 1, spell.getStrength()); // TODO: add damage source/type
            // Add metadata

            Vector4d targetLocation = Vector4d.newPosition(
                    store.getComponent(target, TransformComponent.getComponentType()).getPosition());
            damage.putMetaObject(Damage.HIT_LOCATION, targetLocation);
            damage.putMetaObject(Damage.HIT_ANGLE, 0f); // TODO: determine hit angle based on spell/target
                                                        // properties

            // Fire the damage event
            store.invoke(target, damage);
        }
    }

    private static void handleHealingApplication(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> casterRef,
            @Nonnull SpellDefinition spell,
            @Nonnull List<Ref<EntityStore>> targets) {
        for (Ref<EntityStore> target : targets) {
            notifyPlayer(store, target, "You are healed for " + spell.getStrength() + " health.");

            EntitySource damageSource = new EntitySource(casterRef);

            Damage damage = new Damage(damageSource, 1, -1 * spell.getStrength()); // TODO: add damage source/type
            // Add metadata

            Vector4d targetLocation = Vector4d.newPosition(
                    store.getComponent(target, TransformComponent.getComponentType()).getPosition());
            damage.putMetaObject(Damage.HIT_LOCATION, targetLocation);
            damage.putMetaObject(Damage.HIT_ANGLE, 0f); // TODO: determine hit angle based on spell/target
                                                        // properties

            // Fire the damage event
            store.invoke(target, damage);
        }
    }

    private static void printAvailableProjectileConfigs() {
        // Debug: print all loaded configs to verify our config is loading correctly
        ProjectileConfig.getAssetMap().getAssetMap().forEach((k, v) -> System.out.println("Projectile config: " + k));
    }

    private static void notifyPlayer(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> entityRef,
            @Nonnull String message) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw(message));
        }
    }
}
