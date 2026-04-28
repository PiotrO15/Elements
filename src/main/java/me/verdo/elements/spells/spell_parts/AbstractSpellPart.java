package me.verdo.elements.spells.spell_parts;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.spells.SpellDefinition;

public abstract class AbstractSpellPart {
    public static final int DEFAULT_COST = 0;
    public static final String DEFAULT_NAME = "Unnamed Spell Part";
    public static final String DEFAULT_DESCRIPTION = "No description available.";
    public static final String DEFAULT_ELEMENT = "None";
    public static final String DEFAULT_PROJECTILE_CONFIG_NAME = "Default_Spell_Projectile";

    protected AbstractSpellPart() {
    }

    public static AbstractSpellPart fromId(@Nullable String id) {
        return SpellPartRegistry.fromId(id);
    }

    public static List<AbstractSpellPart> getAvailableSpellParts() {
        return SpellPartRegistry.getAll();
    }

    @Nonnull
    public abstract String getId();

    @Nonnull
    public String getName() {
        return DEFAULT_NAME;
    }

    @Nonnull
    public String getDescription() {
        return DEFAULT_DESCRIPTION;
    }

    @Nullable
    public String getElement() {
        return DEFAULT_ELEMENT;
    }

    public int getCost() {
        return DEFAULT_COST;
    }

    @Nonnull
    public String getProjectileConfigName() {
        return DEFAULT_PROJECTILE_CONFIG_NAME;
    }

    public abstract void onResolveEntity(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> casterRef,
            @Nonnull SpellDefinition spell,
            @Nonnull List<Ref<EntityStore>> targets);

    public abstract void onResolveBlock();

    protected void notifyPlayer(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> entityRef,
            @Nonnull String message) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw(message));
        }
    }

    @Override
    public String toString() {
        return "AbstractSpellPart{" +
                "COST=" + getCost() +
                ", NAME='" + getName() + '\'' +
                ", DESCRIPTION='" + getDescription() + '\'' +
                ", ELEMENT='" + getElement() + '\'' +
                '}';
    }

}