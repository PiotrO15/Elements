package me.verdo.elements.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class GolemSealComponent implements Component<EntityStore> {
    public static final BuilderCodec<GolemSealComponent> CODEC;
    private ItemStack storedSeal;

    public GolemSealComponent() {
        storedSeal = ItemStack.EMPTY;
    }

    public GolemSealComponent(ItemStack storedSeal) {
        this.storedSeal = storedSeal;
    }

    @Override
    public Component<EntityStore> clone() {
        return new GolemSealComponent(storedSeal);
    }

    public ItemStack getStoredSeal() {
        return storedSeal;
    }

    public void setStoredSeal(ItemStack storedSeal) {
        this.storedSeal = storedSeal;
    }

    static {
        CODEC = BuilderCodec.builder(GolemSealComponent.class, GolemSealComponent::new)
                .append(new KeyedCodec<>("StoredSeal", ItemStack.CODEC, true), (c, s) -> c.storedSeal = s, (c) -> c.storedSeal).add()
                .build();
    }
}
