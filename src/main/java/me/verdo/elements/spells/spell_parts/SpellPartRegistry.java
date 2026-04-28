package me.verdo.elements.spells.spell_parts;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SpellPartRegistry {
    private static final List<AbstractSpellPart> REGISTERED_PARTS = List.of(
            new IceBoltSpellPart(),
            new FireballSpellPart(),
            new HealSpellPart()
    );

    private static final Map<String, AbstractSpellPart> BY_ID = REGISTERED_PARTS.stream()
            .collect(Collectors.toUnmodifiableMap(part -> normalizeId(part.getId()), Function.identity()));

    private SpellPartRegistry() {
    }

    @Nonnull
    public static List<AbstractSpellPart> getAll() {
        return REGISTERED_PARTS;
    }

    @Nonnull
    public static AbstractSpellPart getDefault() {
        return REGISTERED_PARTS.get(0);
    }

    @Nonnull
    public static AbstractSpellPart fromId(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return getDefault();
        }
        return BY_ID.getOrDefault(normalizeId(id), getDefault());
    }

    @Nonnull
    private static String normalizeId(@Nonnull String id) {
        return id.trim().toLowerCase();
    }
}