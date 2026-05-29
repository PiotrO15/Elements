package me.verdo.elements.npc.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3iUtil;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import it.unimi.dsi.fastutil.Pair;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.GolemSealComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3i;

public class SpawnGolemInteraction extends SimpleBlockInteraction {
    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        commandBuffer.run((store) -> {
            String entityToSpawn = "Straw_Golem";

            Pair<Ref<EntityStore>, INonPlayerCharacter> pair = NPCPlugin.get().spawnNPC(store, entityToSpawn, null, Vector3iUtil.toVector3d(targetBlock).add(0, 1, 0), new Rotation3f());

            if (pair == null) {
                return;
            }
            Ref<EntityStore> entityRef = pair.key();
            GolemSealComponent golemSealComponent = new GolemSealComponent();
            golemSealComponent.setCenter(Vector3iUtil.toVector3d(targetBlock));
            store.addComponent(entityRef, ElementsPlugin.get().golemStorage, golemSealComponent);
        });

        ItemStack heldItem = interactionContext.getHeldItem();
        if (heldItem != null) {
            interactionContext.getHeldItemContainer().removeItemStackFromSlot(interactionContext.getHeldItemSlot(), heldItem, 1);
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }

    public static final BuilderCodec<SpawnGolemInteraction> CODEC = BuilderCodec.builder(SpawnGolemInteraction.class, SpawnGolemInteraction::new).documentation("Spawn an Elements golem").build();
}
