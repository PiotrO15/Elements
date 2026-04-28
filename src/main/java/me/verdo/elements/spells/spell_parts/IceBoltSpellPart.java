package me.verdo.elements.spells.spell_parts;

import java.util.List;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.spells.SpellDefinition;

public final class IceBoltSpellPart extends AbstractSpellPart {
    public static final int COST = 0;
    public static final String ID = "ice_bolt";
    public static final String NAME = "Ice Bolt";
    public static final String DESCRIPTION = "Fires an ice bolt at targets.";
    public static final String ELEMENT = "ICE";
    public static final String PROJECTILE_CONFIG_NAME = "Ice_Spell_Projectile";

    public IceBoltSpellPart() {
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
            SpellHelpers.dealDamage(store, casterRef, target, spell.getStrength());
        }
    }

    @Override
    public void onResolveBlock() {
    }
}