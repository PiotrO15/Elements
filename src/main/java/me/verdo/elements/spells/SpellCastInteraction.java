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

import me.verdo.elements.util.ItemStackUtil;

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
        Entity entity = EntityUtils.getEntity(entityRef, interactionContext.getCommandBuffer());

        final World world = buffer.getExternalData().getWorld();

        ItemStack heldItem = InventoryComponent.getItemInHand(buffer, entityRef);

        heldItem = addTestSpellToItem(heldItem); // TODO: determine spell based on interaction context
                                                 // (e.g. item used, player state, etc.)

        if (entity instanceof Player player) {

            // update held item with new spell metadata
            ItemStackUtil.replaceActiveItemInPlayerHand(player, heldItem);

            SpellDefinition spell = getSpellFromItem(heldItem, 0);

            PlayerRef playerRefComponent = buffer.getStore().getComponent(entityRef, PlayerRef.getComponentType());
            if (playerRefComponent != null) {

                player.sendMessage(Message.raw("Casting spell of type - " + spell.getEffectType().name()));

                SpellCastResolver.handleSpellCast(0, entityRef, spell, null, buffer.getStore(), buffer);
            }
        }
    }

    private static ItemStack addTestSpellToItem(ItemStack itemStack) {
        // debug method to add a test spell to an item - TODO: remove

        printSpellsInItem(itemStack); // debug - before
        SpellDefinition defaultSpell = SpellDefinition.makeTestSpell();

        // add spell to held item's metadata for testing - in the future, this will be
        // determined by the item type and/or NBT data
        SpellSlotsComponent spellSlotsComponent = getSpellSlotsComponent(itemStack);
        if (spellSlotsComponent == null) {
            spellSlotsComponent = new SpellSlotsComponent(3);
            spellSlotsComponent.addSpell(defaultSpell, 0);
        } else {
            SpellDefinition existingSpell = spellSlotsComponent.getSpell(1);
            spellSlotsComponent.addSpell(existingSpell.setName(existingSpell.getName() + " (updated)"), 1);
        }

        itemStack = setSpellSlotsComponent(itemStack, spellSlotsComponent); // TODO: update held item with new spell
                                                                            // metadata (currently broken :/)
        printSpellsInItem(itemStack); // debug - after
        return itemStack;
    }

    private static SpellSlotsComponent getSpellSlotsComponent(ItemStack itemStack) {
        return itemStack.getFromMetadataOrNull(SpellSlotsComponent.METADATA_KEY, SpellSlotsComponent.CODEC);
    }

    private static ItemStack setSpellSlotsComponent(ItemStack itemStack, SpellSlotsComponent spellSlotsComponent) {
        return itemStack.withMetadata(SpellSlotsComponent.METADATA_KEY, SpellSlotsComponent.CODEC, spellSlotsComponent);
    }

    private static SpellDefinition getSpellFromItem(ItemStack itemStack, int slot) {
        SpellSlotsComponent spellSlotsComponent = itemStack.getFromMetadataOrNull(SpellSlotsComponent.METADATA_KEY,
                SpellSlotsComponent.CODEC);
        if (spellSlotsComponent == null) {
            return null;
        }
        return spellSlotsComponent.getSpell(slot);
    }

    private static void printSpellsInItem(ItemStack itemStack) {
        // debug method to print spells stored in an item's metadata
        SpellSlotsComponent spellSlotsComponent = getSpellSlotsComponent(itemStack);
        if (spellSlotsComponent == null) {
            System.out.println("No SpellSlotsComponent found in item metadata.");
            return;
        }
        spellSlotsComponent.printStoredSpells();
    }
}
