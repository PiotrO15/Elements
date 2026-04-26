package me.verdo.elements.spells;

import java.util.Arrays;
import java.util.List;

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

    public static List<String> getValidTypes() {
        return Arrays.stream(SpellEffectType.values())
                .map(Enum::name)
                .toList();
    }
}

