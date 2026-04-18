package me.verdo.elements.spells;

public enum SpellEffectType {
    DAMAGE,
    BUFF,
    DEBUFF;

    public static SpellEffectType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "DAMAGE" -> DAMAGE;
            case "BUFF" -> BUFF;
            case "DEBUFF" -> DEBUFF;
            default -> throw new IllegalArgumentException("Invalid SpellEffectType: " + value);
        };
    }
}

