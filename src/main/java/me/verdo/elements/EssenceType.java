package me.verdo.elements;

public enum EssenceType {
    FIRE("Ingredient_Fire_Essence"),
    ICE("Ingredient_Ice_Essence"),
    LIFE("Ingredient_Life_Essence"),
    LIGHTNING("Ingredient_Lightning_Essence"),
    VOID("Ingredient_Void_Essence"),
    WATER("Ingredient_Water_Essence");

    public final String id;

    EssenceType(String id) {
        this.id = id;
    }

    public static EssenceType fromId(String id) {
        for (EssenceType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
