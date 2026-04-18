package me.verdo.elements.spells;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class SpellCastResolver  {
    private static final long MILLIS_PER_TICK = 50L;

    public static void handleSpellCast(int i, @Nonnull Ref<EntityStore> casterRef, @Nonnull SpellDefinition spell,
            @Nullable Ref<EntityStore> target,@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        List<Ref<EntityStore>> targets = selectTargets(casterRef, spell, target);
        if (targets.isEmpty()) {
            notifyPlayer(store, casterRef, "Spell failed: no valid target.");
            return;
        }

        List<Ref<EntityStore>> finalTargets = expandAoeTargets(targets);

        notifyPlayer(store, casterRef, "Spell hits " + targets.size() + " target(s).");

        // SpellModifierSet modifiers = event.getSpell().getModifiers();

        int casts = 1; // TODO: implement modifiers
        int delayTicks = 0; // TODO: implement modifiers

        if (delayTicks <= 0) {
            for (int castIndex = 0; castIndex < casts; castIndex++) {
                applySpellOnce(store, casterRef, spell, finalTargets, castIndex + 1, casts);
            }
            return;
        }

        World world = store.getExternalData().getWorld();
        for (int castIndex = 0; castIndex < casts; castIndex++) {
            final int iteration = castIndex + 1;
            long delayMs = (long) delayTicks * castIndex * MILLIS_PER_TICK;
            HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> world.execute(() -> applySpellOnce(store, casterRef, spell, finalTargets, iteration, casts)), delayMs, TimeUnit.MILLISECONDS);
        }
    }

    private static List<Ref<EntityStore>> selectTargets(Ref<EntityStore> casterRef, SpellDefinition spell, @Nullable Ref<EntityStore> initialTarget) {
        // final TransformComponent transform = buffer.getComponent(entityRef, TransformComponent.getComponentType());

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
            case PROJECTILE -> List.of(); // TODO: implement projectile logic
        };
    }

    private static List<Ref<EntityStore>> expandAoeTargets(@Nonnull List<Ref<EntityStore>> selectedTargets) {
        int aoeIncrease = 0;
        if (aoeIncrease <= 0) {
            return selectedTargets;
        }

        // TODO: Implement AOE target expansion logic
        return selectedTargets;
    }

    private static void applySpellOnce(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> casterRef,
            @Nonnull SpellDefinition spell,
            @Nonnull List<Ref<EntityStore>> targets,
            int currentCast,
            int totalCasts
    ) {
        switch (spell.getEffectType()) {
            case DAMAGE -> {
                for (Ref<EntityStore> target : targets) {
                    notifyPlayer(store, target, "You take " + spell.getStrength() + " spell damage.");
                }
            }
            case BUFF -> {
                for (Ref<EntityStore> target : targets) {
                    notifyPlayer(store, target, "You gain a buff for " + spell.getDurationTicks() + " ticks.");
                }
            }
            case DEBUFF -> {
                for (Ref<EntityStore> target : targets) {
                    notifyPlayer(store, target, "You are afflicted for " + spell.getDurationTicks() + " ticks.");
                }
            }
        }

        notifyPlayer(store, casterRef, "Cast " + currentCast + "/" + totalCasts + " hit " + targets.size() + " target(s).");
    }

    private static void notifyPlayer(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> entityRef, @Nonnull String message) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw(message));
        }
    }
}
