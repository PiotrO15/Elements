package me.verdo.elements;

import com.hypixel.hytale.protocol.Color;

import java.util.List;

public enum EssenceType {
    FIRE("Ingredient_Fire_Essence", "Fire", 0xFF4500),
    ICE("Ingredient_Ice_Essence", "Ice", 0x87CEEB),
    LIFE("Ingredient_Life_Essence", "Life", 0x79ff00),
    LIGHTNING("Ingredient_Lightning_Essence", "Lightning", 0xFFD700),
    VOID("Ingredient_Void_Essence", "Void", 0x2E0854),
    WATER("Ingredient_Water_Essence", "Water", 0x1E90FF),
    EARTH("Earth_Essence", "Earth", 0x8B4513),
    HARVEST("Harvest_Essence", "Harvest", 0xc8ac4c);

    private static final List<EssenceType> baseEssenceTypes = List.of(FIRE, ICE, LIFE, LIGHTNING, VOID, WATER);

    private final String itemId;
    private final String friendlyName;
    private final int color;

    EssenceType(String itemId, String friendlyName, int color) {
        this.itemId = itemId;
        this.friendlyName = friendlyName;
        this.color = color;
    }

    public static EssenceType fromId(String id) {
        for (EssenceType type : values()) {
            if (type.itemId.equals(id)) {
                return type;
            }
        }
        return null;
    }

    public String getItemId() {
        return itemId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Color getColor() {
        return new Color((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8)  & 0xFF), (byte) (color  & 0xFF));
    }

    public static List<EssenceType> getBaseEssenceTypes() {
        return baseEssenceTypes;
    }
}
