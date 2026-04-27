package me.verdo.elements.spells.spell_parts;

import java.util.List;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.EntitySource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.spells.SpellDefinition;

public final class HealSpellPart extends AbstractSpellPart {
    public static final int COST = 0;
    public static final String ID = "heal";
    public static final String NAME = "Heal";
    public static final String DESCRIPTION = "Restores health to targets.";
    public static final String ELEMENT = null;
    public static final String PROJECTILE_CONFIG_NAME = "Default_Spell_Projectile";

    public HealSpellPart() {
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
            notifyPlayer(store, target, "You are healed for " + spell.getStrength() + " health.");

            TransformComponent transform = store.getComponent(target, TransformComponent.getComponentType());
            if (transform == null) {
                continue;
            }

            EntitySource damageSource = new EntitySource(casterRef);
            Damage damage = new Damage(damageSource, 1, -1 * spell.getStrength());
            Vector4d targetLocation = Vector4d.newPosition(transform.getPosition());
            damage.putMetaObject(Damage.HIT_LOCATION, targetLocation);
            damage.putMetaObject(Damage.HIT_ANGLE, 0f);
            store.invoke(target, damage);
        }
    }

    @Override
    public void onResolveBlock() {
    }
}