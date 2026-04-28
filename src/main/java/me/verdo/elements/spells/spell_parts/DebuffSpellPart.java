package me.verdo.elements.spells.spell_parts;

import java.util.List;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.spells.SpellDefinition;

public final class DebuffSpellPart extends AbstractSpellPart {
    public static final int COST = 0;
    public static final String ID = "debuff";
    public static final String NAME = "Debuff";
    public static final String DESCRIPTION = "Applies a lingering spell effect to targets.";
    public static final String ELEMENT = null;
    public static final String PROJECTILE_CONFIG_NAME = "Default_Spell_Projectile";

    public DebuffSpellPart() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getElement() {
        return ELEMENT;
    }

    @Override
    public int getCost() {
        return COST;
    }

    @Override
    public String getProjectileConfigName() {
        return PROJECTILE_CONFIG_NAME;
    }

    @Override
    public void onResolveEntity(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> casterRef,
            @Nonnull SpellDefinition spell, @Nonnull List<Ref<EntityStore>> targets) {
        for (Ref<EntityStore> target : targets) {
            notifyPlayer(store, target, "You are afflicted for " + spell.getDurationTicks() + " ticks.");
        }
    }

    @Override
    public void onResolveBlock() {
    }
}