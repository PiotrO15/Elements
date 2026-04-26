package me.verdo.elements.spells;

import java.util.Arrays;
import java.util.List;

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

        public static List<String> getValidTypes() {
        return Arrays.stream(SpellTargetType.values())
                .map(Enum::name)
                .toList();
    }
}

