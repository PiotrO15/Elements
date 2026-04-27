package me.verdo.elements.spells;

import java.util.Arrays;
import java.util.List;

public enum SpellTargetType {
    SELF,
    TOUCH,
    PROJECTILE,
    UNDEFINED; // for error handling when parsing from string

    public static SpellTargetType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "SELF" -> SELF;
            case "TOUCH" -> TOUCH;
            case "PROJECTILE" -> PROJECTILE;
            default -> UNDEFINED;
        };
    }

    public static List<String> getValidTypes() {
        return Arrays.stream(SpellTargetType.values())
                .map(Enum::name)
                .filter(name -> !name.equals(UNDEFINED.name()))
                .toList();
    }
}
