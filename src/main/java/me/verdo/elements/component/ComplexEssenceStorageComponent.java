package me.verdo.elements.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.EssenceType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ComplexEssenceStorageComponent implements Component<EntityStore> {
    public static final BuilderCodec<ComplexEssenceStorageComponent> CODEC;
    private Map<String, Integer> storage;
    private int maxStorage = 25;

    public static String METADATA_KEY = "EssenceStorage";

    public ComplexEssenceStorageComponent() {
        storage = new HashMap<>();
        for (EssenceType type : EssenceType.values()) {
            storage.put(type.getItemId(), 0);
        }
    }

    public ComplexEssenceStorageComponent(Map<String, Integer> essenceAmounts, int maxStorage) {
        this.storage = new HashMap<>();
        this.maxStorage = maxStorage;

        // Initialize with provided values, falling back to defaults
        for (EssenceType type : EssenceType.values()) {
            this.storage.put(type.getItemId(), essenceAmounts.getOrDefault(type.getItemId(), 0));
        }
    }

    public int getStoredEssence(EssenceType essenceType) {
        return storage.get(essenceType.getItemId());
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public ItemStack store(ItemStack itemStack) {
        EssenceType essenceType = EssenceType.fromId(itemStack.getItemId());

        if (essenceType == null) {
            return itemStack;
        }

        int capacity = getMaxStorage() - getStoredEssence(essenceType);

        if (itemStack.getQuantity() > capacity) {
            storage.put(essenceType.getItemId(), maxStorage);

            return itemStack.withQuantity(itemStack.getQuantity() - capacity);
        }

        storage.put(essenceType.getItemId(), getStoredEssence(essenceType) + itemStack.getQuantity());
        return null;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new ComplexEssenceStorageComponent(
                new HashMap<>(storage),
                maxStorage
        );
    }

    static {
        CODEC = BuilderCodec.builder(ComplexEssenceStorageComponent.class, ComplexEssenceStorageComponent::new)
                .addField(new KeyedCodec<>("MaxStorage", Codec.INTEGER, true), (s, o) -> s.maxStorage = o, (s) -> s.maxStorage)
                .addField(new KeyedCodec<>("StoredEssence", new MapCodec<Integer, Map<String, Integer>>(Codec.INTEGER, HashMap::new), true), (s, o) -> s.storage = new HashMap<>(o), s -> s.storage)
                .build();
    }
}
