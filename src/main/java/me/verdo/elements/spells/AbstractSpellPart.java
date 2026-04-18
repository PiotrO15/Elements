package me.verdo.elements.spells;

import javax.annotation.Nullable;

import com.hypixel.hytale.server.core.entity.Entity;

public abstract class AbstractSpellPart {
    // This class can be used to represent different parts of a spell [could be chained in the future for more complex spells]

    
    public int COST = 0; // TODO: implement spell cost
    public String NAME = "Unnamed Spell Part";
    public String DESCRIPTION = "No description available.";
    public @Nullable String ELEMENT = "None";

    public AbstractSpellPart(int cost, String name, String description, @Nullable String element) {
        this.COST = cost;
        this.NAME = name;
        this.DESCRIPTION = description;
        this.ELEMENT = element;
    }

    public AbstractSpellPart() {
    }

    public void onResolveEntity(Entity entity) {
        // This method can be overridden by subclasses to define what happens when the spell part is resolved on an entity
    }

    public void onResolveBlock(Entity entity) {
        // This method can be overridden by subclasses to define what happens when the spell part is resolved on a block
    }

    @Override
    public String toString() {
        return "AbstractSpellPart{" +
                "COST=" + COST +
                ", NAME='" + NAME + '\'' +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                ", ELEMENT='" + ELEMENT + '\'' +
                '}';
    }

}