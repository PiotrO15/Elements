package me.verdo.elements.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.screen.KnowledgeBookScreen;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OpenKnowledgeBookInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<OpenKnowledgeBookInteraction> CODEC = BuilderCodec.builder(OpenKnowledgeBookInteraction.class, OpenKnowledgeBookInteraction::new).documentation("Opens knowledge book").build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) return;

        Ref<EntityStore> playerRef = interactionContext.getEntity();
        Entity entity = EntityUtils.getEntity(playerRef, interactionContext.getCommandBuffer());

        if (entity instanceof Player playerComponent) {
            PlayerRef playerRefComponent = commandBuffer.getStore().getComponent(playerRef, PlayerRef.getComponentType());
            if (playerRefComponent != null) {
                playerComponent.getPageManager().openCustomPage(playerRef, commandBuffer.getStore(), new KnowledgeBookScreen(playerRefComponent));
            }
        }
    }
}
