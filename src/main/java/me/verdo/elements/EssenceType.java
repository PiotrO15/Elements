package me.verdo.elements;

import java.util.List;

public enum EssenceType {
    FIRE("Ingredient_Fire_Essence", "Fire", 0xFF4500),
    ICE("Ingredient_Ice_Essence", "Ice", 0x87CEEB),
    LIFE("Ingredient_Life_Essence", "Life", 0x32CD32),
    LIGHTNING("Ingredient_Lightning_Essence", "Lightning", 0xFFD700),
    VOID("Ingredient_Void_Essence", "Void", 0x2E0854),
    WATER("Ingredient_Water_Essence", "Water", 0x1E90FF);

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

    public int getColor() {
        return color;
    }

    public static List<EssenceType> getBaseEssenceTypes() {
        return baseEssenceTypes;
    }
}
