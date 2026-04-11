package me.verdo.elements.npc.action.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.verdo.elements.npc.action.ApplySealAction;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderApplySealAction extends BuilderActionBase {
    @NullableDecl
    @Override
    public String getShortDescription() {
        return "Used to apply a seal on a golem";
    }

    @NullableDecl
    @Override
    public String getLongDescription() {
        return "";
    }

    @NullableDecl
    @Override
    public Action build(BuilderSupport builderSupport) {
        return new ApplySealAction(this);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }
}
