package me.verdo.elements.spells;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SpellCastInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<SpellCastInteraction> CODEC = BuilderCodec
            .builder(SpellCastInteraction.class, SpellCastInteraction::new).documentation("Casts a spell").build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext interactionContext,
            @Nonnull CooldownHandler cooldown) {
        final CommandBuffer<EntityStore> buffer = interactionContext.getCommandBuffer();
        if (buffer == null) {
            return;
        }

        final Ref<EntityStore> entityRef = interactionContext.getEntity();
        Entity entity = EntityUtils.getEntity(entityRef, interactionContext.getCommandBuffer().getStore());

        final World world = buffer.getExternalData().getWorld();

        ItemStack heldItem = InventoryComponent.getItemInHand(buffer, entityRef);

        if (entity instanceof Player player) {

            SpellDefinition spell = SpellSlotsComponent.getSpellFromItemBySlot(heldItem, 0);

            PlayerRef playerRefComponent = buffer.getStore().getComponent(entityRef, PlayerRef.getComponentType());
            if (playerRefComponent != null && spell != null) {
                // System.out.println("Player " + player.getName() + " is casting spell: " + spell.getName());

                SpellCastResolver.handleSpellCast(entityRef, spell, null, buffer.getStore(), buffer);
            }
        }
    }


}
