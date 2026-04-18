package me.verdo.elements.spells;

import java.util.HashMap;
import java.util.Map;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SpellSlotsComponent implements Component<EntityStore> { // Currently used only for item metadata, but could be used for entities/blocks in the future?
    public static final BuilderCodec<SpellSlotsComponent> CODEC;
    public static String METADATA_KEY = "SpellSlots";

    private int maxSlots;
    private Map<String, SpellDefinition> storedSpells;

    public SpellSlotsComponent() {
        this.maxSlots = 0;
        this.storedSpells = new HashMap<>();
    }

    public SpellSlotsComponent(int maxSlots) {
        this.maxSlots = maxSlots;
        this.storedSpells = new HashMap<>();
    }

    public Component clone() {
        SpellSlotsComponent clone = new SpellSlotsComponent(maxSlots);
        clone.storedSpells = new HashMap<>(storedSpells);
        return clone;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public Map<String, SpellDefinition> getStoredSpells() {
        return storedSpells;
    }

    public void setStoredSpells(Map<String, SpellDefinition> storedSpells) {
        this.storedSpells = storedSpells;
    }

    public boolean addSpell(SpellDefinition spell, Integer slot) {
        if (slot >= maxSlots) {
            return false;
        }
        storedSpells.put(slot.toString(), spell);
        return true;
    }

    public boolean removeSpell(Integer slot) {
        return storedSpells.remove(slot.toString()) != null;
    }

    public SpellDefinition getSpell(Integer slot) {
        return storedSpells.get(slot.toString());
    }

    @Override
    public String toString() {
        return "SpellSlotsComponent{" +
                "maxSlots=" + maxSlots +
                ", storedSpells=" + storedSpells +
                '}';
    }

    public void printStoredSpells() {
        // Debug method to print stored spells to console
        if (storedSpells == null) {
            System.out.println("No SpellSlotsComponent found in item metadata.");
            return;
        }
        storedSpells.forEach((slot, spell) -> 
            System.out.println("Slot: " + slot + ", Spell: " + spell)
        );
    }

    static {
        CODEC = BuilderCodec.builder(SpellSlotsComponent.class, SpellSlotsComponent::new)
            .append(new KeyedCodec<>("MaxSlots", Codec.INTEGER, true), (s, o) -> s.maxSlots = o, (s) -> s.maxSlots).add()
            .append(new KeyedCodec<>("StoredSpells",new MapCodec<SpellDefinition, HashMap<String, SpellDefinition>>(SpellDefinition.CODEC,HashMap::new),true), 
                (s, o) -> s.storedSpells = new HashMap<>(o), 
                s -> s.storedSpells)
                .add().build();
    }

}
