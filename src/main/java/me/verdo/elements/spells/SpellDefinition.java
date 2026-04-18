package me.verdo.elements.spells;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.Objects;

public final class SpellDefinition {
    private String name = "";
    private SpellTargetType targetType = SpellTargetType.SELF;
    private SpellEffectType effectType = SpellEffectType.DAMAGE;
    private int cost = 0; // TODO: implement spell cost
    private int cooldownTicks = 0; // TODO: implement spell cooldown
    private int durationTicks = 0; // TODO: implement spell duration
    private int aoeRadius = 0; // TODO: implement spell AOE radius
    private int strength = 10; // TODO: implement spell strength

    private SpellDefinition() {
    }

    public SpellDefinition(
            @Nonnull String name,
            @Nonnull SpellTargetType targetType,
            @Nonnull SpellEffectType effectType
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.targetType = Objects.requireNonNull(targetType, "targetType must not be null");
        this.effectType = Objects.requireNonNull(effectType, "effectType must not be null");
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public SpellDefinition setName(@Nonnull String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        return this;
    }

    @Nonnull
    public SpellTargetType getTargetType() {
        return targetType;
    }

    @Nonnull
    public SpellEffectType getEffectType() {
        return effectType;
    }

    public int getCost() {
        return cost;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }
    
    public int getDurationTicks() {
        return durationTicks;
    }

    public int getAoeRadius() {
        return aoeRadius;
    }

    public int getStrength() {
        return strength;
    }

    public static SpellDefinition makeTestSpell() {
        return new SpellDefinition("test_spell", SpellTargetType.SELF, SpellEffectType.DAMAGE);
    }

    // make codecs, equals, hashCode, toString as needed
    // ...
    
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpellDefinition that = (SpellDefinition) o;
        return cost == that.cost && cooldownTicks == that.cooldownTicks && durationTicks == that.durationTicks && aoeRadius == that.aoeRadius && strength == that.strength && name.equals(that.name) && targetType == that.targetType && effectType == that.effectType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetType, effectType, cost, cooldownTicks, durationTicks, aoeRadius, strength);
    }

    @Override
    public String toString() {
        return "SpellDefinition{" +
                "name='" + name + '\'' +
                ", targetType=" + targetType +
                ", effectType=" + effectType +
                ", cost=" + cost +
                ", cooldownTicks=" + cooldownTicks +
                ", durationTicks=" + durationTicks +
                ", aoeRadius=" + aoeRadius +
                ", strength=" + strength +
                '}';
    }

    public static final BuilderCodec<SpellDefinition> CODEC = BuilderCodec.builder(SpellDefinition.class, SpellDefinition::new)
            .append(new KeyedCodec<>("Name", Codec.STRING, true), (s, o) -> s.name = o, (s) -> s.name).add()
            .append(new KeyedCodec<>("TargetType", Codec.STRING, true), (s, o) -> s.targetType = SpellTargetType.fromString(o), (s) -> s.targetType.toString()).add()
            .append(new KeyedCodec<>("EffectType", Codec.STRING, true), (s, o) -> s.effectType = SpellEffectType.fromString(o), (s) -> s.effectType.toString()).add()
            .build();
}

