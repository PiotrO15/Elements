package me.verdo.elements.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.verdo.elements.component.ComplexEssenceStorageComponent;
import me.verdo.elements.EssenceType;

import javax.annotation.Nonnull;

public class EssenceHudProvider extends CustomUIHud {

    ComplexEssenceStorageComponent component;

    public EssenceHudProvider(@Nonnull PlayerRef playerRef, ComplexEssenceStorageComponent component) {
        super(playerRef);
        this.component = component;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Hud/EssenceDisplay.ui");

        uiCommandBuilder.set("#Fire #Fire.Value", (double) component.getStoredEssence(EssenceType.FIRE) / component.getMaxStorage());
    }
}
