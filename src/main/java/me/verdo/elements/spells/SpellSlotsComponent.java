package me.verdo.elements.spells;

import java.util.HashMap;
import java.util.Map;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SpellSlotsComponent implements Component<EntityStore> { // Currently used only for item metadata, but could
                                                                     // be used for entities/blocks in the future?
    public static final BuilderCodec<SpellSlotsComponent> CODEC;
    public static String METADATA_KEY = "SpellSlots";

    private int maxSlots;
    private Map<String, SpellDefinition> storedSpells;

    public SpellSlotsComponent() {
        this.maxSlots = 3; // default to 3 slots if not specified
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

    private static SpellSlotsComponent getSpellSlotsComponent(ItemStack itemStack) {
        return itemStack.getFromMetadataOrNull(SpellSlotsComponent.METADATA_KEY, SpellSlotsComponent.CODEC);
    }

    private static ItemStack setSpellSlotsComponent(ItemStack itemStack, SpellSlotsComponent spellSlotsComponent) {
        return itemStack.withMetadata(SpellSlotsComponent.METADATA_KEY, SpellSlotsComponent.CODEC, spellSlotsComponent);
    }

    public void printStoredSpells() {
        // Debug method to print stored spells to console
        if (storedSpells == null) {
            System.out.println("No SpellSlotsComponent found in item metadata.");
            return;
        }
        storedSpells.forEach((slot, spell) -> System.out.println("Slot: " + slot + ", Spell: " + spell));
    }

    public static void printSpellsInItem(ItemStack itemStack) {
        // debug method to print spells stored in an item's metadata
        SpellSlotsComponent spellSlotsComponent = itemStack.getFromMetadataOrNull(SpellSlotsComponent.METADATA_KEY,
                SpellSlotsComponent.CODEC);
        if (spellSlotsComponent == null) {
            System.out.println("No SpellSlotsComponent found in item metadata.");
            return;
        }
        spellSlotsComponent.printStoredSpells();
    }

    public static SpellSlotsComponent getSpellsFromItem(ItemStack itemStack) {
        SpellSlotsComponent spellSlotsComponent = itemStack.getFromMetadataOrNull(METADATA_KEY, CODEC);
        if (spellSlotsComponent == null) {
            return null;
        }
        return spellSlotsComponent;
    };

    public static SpellDefinition getSpellFromItemBySlot(ItemStack itemStack, int slot) {
        SpellSlotsComponent spellSlotsComponent = itemStack.getFromMetadataOrNull(SpellSlotsComponent.METADATA_KEY,
                SpellSlotsComponent.CODEC);
        if (spellSlotsComponent == null) {
            return null;
        }
        return spellSlotsComponent.getSpell(slot);
    }

    public static ItemStack setSpellsInItem(ItemStack itemStack, SpellSlotsComponent spellSlotsComponent) {
        return itemStack.withMetadata(METADATA_KEY, CODEC, spellSlotsComponent);
    }

    public static ItemStack setSpellInItemBySlot(ItemStack itemStack, SpellDefinition spell, int slot) {
        SpellSlotsComponent spellSlotsComponent = itemStack.getFromMetadataOrNull(METADATA_KEY, CODEC);
        if (spellSlotsComponent == null) {
            spellSlotsComponent = new SpellSlotsComponent(3); // default to 3 slots if not already present
        }
        spellSlotsComponent.addSpell(spell, slot);
        return itemStack.withMetadata(METADATA_KEY, CODEC, spellSlotsComponent);
    }
    
    // debug method to add a test spell to an item - TODO: remove
    public static ItemStack addTestSpellToItem(ItemStack itemStack) {

        // printSpellsInItem(itemStack); // debug - before
        SpellDefinition defaultSpell = SpellDefinition.makeTestSpell();

        // add spell to held item's metadata for testing - in the future, this will be customiseable
        SpellSlotsComponent spellSlotsComponent = getSpellSlotsComponent(itemStack);
        if (spellSlotsComponent == null) {
            spellSlotsComponent = new SpellSlotsComponent(3);
            spellSlotsComponent.addSpell(defaultSpell, 0);
            spellSlotsComponent.addSpell(defaultSpell.setName(defaultSpell.getName() + " (projectile)")
                    .setTargetType(SpellTargetType.PROJECTILE), 1);
        }

        itemStack = setSpellSlotsComponent(itemStack, spellSlotsComponent);

        // printSpellsInItem(itemStack); // debug - after
        return itemStack;
    }

    static {
        CODEC = BuilderCodec.builder(SpellSlotsComponent.class, SpellSlotsComponent::new)
                .append(new KeyedCodec<>("MaxSlots", Codec.INTEGER, true), (s, o) -> s.maxSlots = o, (s) -> s.maxSlots)
                .add()
                .append(new KeyedCodec<>("StoredSpells",
                        new MapCodec<SpellDefinition, HashMap<String, SpellDefinition>>(SpellDefinition.CODEC,
                                HashMap::new),
                        true),
                        (s, o) -> s.storedSpells = new HashMap<>(o),
                        s -> s.storedSpells)
                .add()
                .build();
    }

}
