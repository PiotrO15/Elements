package me.verdo.elements;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;

public class EssenceStorageComponent implements Component<ChunkStore> {
    public static final BuilderCodec<EssenceStorageComponent> CODEC;

    private EssenceType storedEssenceType;
    private int storedEssenceAmount;

    public EssenceStorageComponent() {
        storedEssenceAmount = 0;
    }

    public EssenceStorageComponent(EssenceType storedEssenceType, int storedEssenceAmount) {
        this.storedEssenceType = storedEssenceType;
        this.storedEssenceAmount = storedEssenceAmount;
    }

    public Component clone() {
        return new EssenceStorageComponent(storedEssenceType, storedEssenceAmount);
    }

    public EssenceType getStoredEssenceType() {
        return storedEssenceType;
    }

    public int getStoredEssenceAmount() {
        return storedEssenceAmount;
    }

    public void setStoredEssenceAmount(int storedEssenceAmount) {
        this.storedEssenceAmount = storedEssenceAmount;
    }

    public void setStoredEssenceType(EssenceType storedEssenceType) {
        this.storedEssenceType = storedEssenceType;
    }

    public boolean canStore(ItemStack itemStack) {
        if (itemStack == null || itemStack == ItemStack.EMPTY) {
            return false;
        }

        if (EssenceType.fromId(itemStack.getItemId()) == null) {
            return false;
        }

        if (storedEssenceType == null)
            return true;

        return Objects.equals(storedEssenceType.id, itemStack.getItemId());
    }

    static {
        CODEC = BuilderCodec.builder(EssenceStorageComponent.class, EssenceStorageComponent::new)
                .addField(new KeyedCodec<>("StoredEssenceAmount", Codec.INTEGER, true), (s, o) -> s.storedEssenceAmount = o, (s) -> s.storedEssenceAmount)
                .addField(new KeyedCodec<>("StoredEssenceType", Codec.STRING, true), (s, o) -> s.storedEssenceType = EssenceType.fromId(o), (s) -> s.storedEssenceType != null ? s.storedEssenceType.id : null)
                .build();
    }
}
