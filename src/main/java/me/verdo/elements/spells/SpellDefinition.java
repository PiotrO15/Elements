package me.verdo.elements.spells;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import me.verdo.elements.spells.spell_parts.AbstractSpellPart;
import me.verdo.elements.spells.spell_parts.SpellPartRegistry;

import java.util.Objects;

public final class SpellDefinition {
    private String name = "";
    private SpellTargetType targetType = SpellTargetType.SELF;
    private AbstractSpellPart effectPart = SpellPartRegistry.getDefault();
    private int cost = 0; // TODO: implement spell cost
    private int cooldownTicks = 0; // TODO: implement spell cooldown
    private int durationTicks = 1000; // TODO: implement spell duration
    private int aoeRadius = 0; // TODO: implement spell AOE radius
    private int strength = 10; // TODO: implement spell strength

    private SpellDefinition() {
    }

    public SpellDefinition(
            @Nonnull String name,
            @Nonnull SpellTargetType targetType,
            @Nonnull AbstractSpellPart effectPart
    ) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.targetType = Objects.requireNonNull(targetType, "targetType must not be null");
        setEffectPart(Objects.requireNonNull(effectPart, "effectPart must not be null"));
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

    public SpellDefinition setTargetType(@Nonnull SpellTargetType targetType) {
        this.targetType = Objects.requireNonNull(targetType, "targetType must not be null");
        return this;
    }

    public SpellDefinition setEffectPart(@Nonnull AbstractSpellPart effectPart) {
        this.effectPart = SpellPartRegistry.fromId(Objects.requireNonNull(effectPart, "effectPart must not be null").getId());
        return this;
    }

    @Nonnull
    public AbstractSpellPart getEffectPart() {
        return effectPart;
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
        return new SpellDefinition("test_spell", SpellTargetType.SELF, SpellPartRegistry.getDefault());
    }

    // make codecs, equals, hashCode, toString as needed
    // ...
    
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpellDefinition that = (SpellDefinition) o;
        return cost == that.cost && cooldownTicks == that.cooldownTicks && durationTicks == that.durationTicks && aoeRadius == that.aoeRadius && strength == that.strength && name.equals(that.name) && targetType == that.targetType && effectPart.getId().equals(that.effectPart.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetType, effectPart.getId(), cost, cooldownTicks, durationTicks, aoeRadius, strength);
    }

    @Override
    public String toString() {
        return "SpellDefinition{" +
                "name='" + name + '\'' +
                ", targetType=" + targetType +
                ", spellPartId='" + effectPart.getId() + '\'' +
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
            .append(new KeyedCodec<>("SpellPartId", Codec.STRING, true), (s, o) -> s.setEffectPart(SpellPartRegistry.fromId(o)), (s) -> s.effectPart.getId()).add()
            .append(new KeyedCodec<>("EffectType", Codec.STRING, true), (s, o) -> s.setEffectPart(SpellPartRegistry.fromId(o)), (s) -> s.effectPart.getId()).add()
            .build();
}

