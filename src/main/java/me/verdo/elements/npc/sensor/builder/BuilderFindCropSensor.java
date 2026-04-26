package me.verdo.elements.npc.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import me.verdo.elements.npc.sensor.FindCropSensor;

import javax.annotation.Nonnull;

public class BuilderFindCropSensor extends BuilderSensorBase {
    protected final DoubleHolder range = new DoubleHolder();

    @Override
    public Builder<Sensor> readConfig(JsonElement data) {
        this.requireDouble(data, "Range", this.range, DoubleRangeValidator.fromExclToIncl(0.0D, Double.MAX_VALUE), BuilderDescriptorState.Stable, "The range to search for harvestable crops in", null);
        this.provideFeature(Feature.Position);
        return this;
    }

    @Override
    public String getShortDescription() {
        return "Finds nearby crops";
    }

    @Override
    public String getLongDescription() {
        return "Parses the positions of nearby crops";
    }

    @Override
    public Sensor build(BuilderSupport builderSupport) {
        return new FindCropSensor(this, builderSupport);
    }

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    public double getRange(@Nonnull BuilderSupport support) {
        return this.range.get(support.getExecutionContext());
    }
}
