package me.verdo.elements.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.EssenceType;

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

    public boolean storeEssence(EssenceType essenceType, int amount) {
        if (storedEssenceType != null && storedEssenceType != essenceType) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        if (storedEssenceAmount + amount > ElementsPlugin.get().getCommonConfig().get().getMaxEssenceStorage()) {
            return false;
        }

        storedEssenceType = essenceType;
        storedEssenceAmount += amount;

        return true;
    }

    public boolean extractEssence(int amount) {
        if (storedEssenceType == null || storedEssenceAmount <= 0) {
            return false;
        }

        if (amount > storedEssenceAmount) {
            return false;
        }

        storedEssenceAmount -= amount;

        if (storedEssenceAmount == 0) {
            storedEssenceType = null;
        }

        return true;
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

        return Objects.equals(storedEssenceType.getItemId(), itemStack.getItemId());
    }

    static {
        CODEC = BuilderCodec.builder(EssenceStorageComponent.class, EssenceStorageComponent::new)
                .append(new KeyedCodec<>("StoredEssenceAmount", Codec.INTEGER, true), (s, o) -> s.storedEssenceAmount = o, (s) -> s.storedEssenceAmount).add()
                .append(new KeyedCodec<>("StoredEssenceType", Codec.STRING, true), (s, o) -> s.storedEssenceType = EssenceType.fromId(o), (s) -> s.storedEssenceType != null ? s.storedEssenceType.getItemId() : null).add()
                .build();
    }
}
