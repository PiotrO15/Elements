package me.verdo.elements.npc.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import me.verdo.elements.npc.sensor.CheckActiveSealSensor;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderCheckActiveSealSensor extends BuilderSensorBase {
    protected final StringHolder sealTypeHolder = new StringHolder();

    @Override
    public Builder<Sensor> readConfig(JsonElement data) {
        this.requireString(data, "SealType", sealTypeHolder, StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The type of seal to check for", null);
        return this;
    }

    @NullableDecl
    @Override
    public String getShortDescription() {
        return "Returns the seal type stored by the golem";
    }

    @NullableDecl
    @Override
    public String getLongDescription() {
        return "";
    }

    @NullableDecl
    @Override
    public Sensor build(BuilderSupport builderSupport) {
        return new CheckActiveSealSensor(this, builderSupport);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    public String getSealType(BuilderSupport support) {
        return this.sealTypeHolder.get(support.getExecutionContext());
    }
}
