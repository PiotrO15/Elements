package me.verdo.elements.npc.action.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.verdo.elements.npc.action.DepositInContainerAction;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderDepositInContainerAction extends BuilderActionBase {
    @NullableDecl
    @Override
    public String getShortDescription() {
        return "";
    }

    @NullableDecl
    @Override
    public String getLongDescription() {
        return "";
    }

    @NullableDecl
    @Override
    public Action build(BuilderSupport builderSupport) {
        return new DepositInContainerAction(this);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }
}
