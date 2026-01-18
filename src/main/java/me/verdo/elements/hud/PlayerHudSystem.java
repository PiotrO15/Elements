package me.verdo.elements.hud;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.component.ComplexEssenceStorageComponent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PlayerHudSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    private final Query<EntityStore> query;
    private final Map<PlayerRef, EssenceHudProvider> huds = new HashMap<>();

    public PlayerHudSystem() {
        this.query = Query.any();
    }

    @Override
    public void tick(float v, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        final Holder<EntityStore> holder = EntityUtils.toHolder(index, archetypeChunk);
        final Player player = holder.getComponent(Player.getComponentType());
        final PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }

        if (player.getInventory().getUtilityItem() != null && player.getInventory().getUtilityItem().getItemId().equals("Copper_Wand")) {
            ComplexEssenceStorageComponent component = player.getInventory().getUtilityItem().getFromMetadataOrNull(ComplexEssenceStorageComponent.METADATA_KEY, ComplexEssenceStorageComponent.CODEC);
            if (component != null) {
                player.getHudManager().setCustomHud(playerRef, new EssenceHudProvider(playerRef, component));
            }
        } else {
            player.getHudManager().setCustomHud(playerRef, new CustomUIHud(playerRef) {
                @Override
                protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {

                }
            });
        }
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }
}
