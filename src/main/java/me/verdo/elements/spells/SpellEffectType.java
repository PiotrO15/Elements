package me.verdo.elements.spells;

import java.util.Arrays;
import java.util.List;

public enum SpellEffectType {
    DAMAGE,
    HEAL,
    DEBUFF,
    UNDEFINED; // for error handling when parsing from string

    public static SpellEffectType fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "DAMAGE" -> DAMAGE;
            case "HEAL" -> HEAL;
            case "DEBUFF" -> DEBUFF;
            default -> UNDEFINED;
        };
    }

    public static List<String> getValidTypes() {
        return Arrays.stream(SpellEffectType.values())
                .map(Enum::name)
                .filter(name -> !name.equals(UNDEFINED.name()))
                .toList();
    }
}
