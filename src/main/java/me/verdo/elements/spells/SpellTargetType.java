package me.verdo.elements.spells;

public enum SpellTargetType {
    SELF,
    TOUCH,
    PROJECTILE;

    public static SpellTargetType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "SELF" -> SELF;
            case "TOUCH" -> TOUCH;
            case "PROJECTILE" -> PROJECTILE;
            default -> throw new IllegalArgumentException("Invalid SpellTargetType: " + value);
        };
    }
}

