package me.verdo.elements.spells.spell_parts;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.EntitySource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public abstract class SpellHelpers {
	protected SpellHelpers() {
	}

	public static void dealDamage(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> casterRef,
			@Nonnull Ref<EntityStore> targetRef, int amount) {
		TransformComponent transform = store.getComponent(targetRef, TransformComponent.getComponentType());
		EntitySource damageSource = targetRef.equals(casterRef) ? null : new EntitySource(casterRef);
		Damage damage = new Damage(damageSource, 1, amount);

		if (transform != null) {
			Vector4d hitLocation = Vector4d.newPosition(transform.getPosition());
			damage.putMetaObject(Damage.HIT_LOCATION, hitLocation);
			damage.putMetaObject(Damage.HIT_ANGLE, 0f);
		}

		store.invoke(targetRef, damage);
	}

  public static void healEntity(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> casterRef, @Nonnull Ref<EntityStore> targetRef, int amount) {
      // Healing implemented as negative damage
      dealDamage(store, casterRef, targetRef, -amount);
  }
}
