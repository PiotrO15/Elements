package me.verdo.elements.screen;

import com.hypixel.hytale.builtin.crafting.state.BenchState;
import com.hypixel.hytale.builtin.crafting.window.BenchWindow;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class AlchemicalFurnaceWindow extends BenchWindow implements ItemContainerWindow {
    private CombinedItemContainer itemContainer;

    public AlchemicalFurnaceWindow(@NonNullDecl BenchState benchState) {
        super(WindowType.Processing, benchState);
    }

    @NonNullDecl
    @Override
    public ItemContainer getItemContainer() {
        return itemContainer;
    }
}
