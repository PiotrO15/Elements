package me.verdo.elements.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class GolemSealComponent implements Component<EntityStore> {
    public static final BuilderCodec<GolemSealComponent> CODEC;
    private ItemStack storedSeal;
    private Vector3d center;

    public GolemSealComponent() {
        storedSeal = ItemStack.EMPTY;
        center = new Vector3d(0, 0, 0);
    }

    public GolemSealComponent(ItemStack storedSeal, Vector3d center) {
        this.storedSeal = storedSeal;
        this.center = center;
    }

    @Override
    public Component<EntityStore> clone() {
        return new GolemSealComponent(storedSeal, center);
    }

    public ItemStack getStoredSeal() {
        return storedSeal;
    }

    public void setStoredSeal(ItemStack storedSeal) {
        this.storedSeal = storedSeal;
    }

    public Vector3d getCenter() {
        return center;
    }

    public void setCenter(Vector3d center) {
        this.center = center;
    }

    static {
        CODEC = BuilderCodec.builder(GolemSealComponent.class, GolemSealComponent::new)
                .append(new KeyedCodec<>("StoredSeal", ItemStack.CODEC, true), (c, s) -> c.storedSeal = s, (c) -> c.storedSeal).add()
                .append(new KeyedCodec<>("Center", Vector3d.CODEC, true), (c, s) -> c.center = s, (c) -> c.center).add()
                .build();
    }
}
