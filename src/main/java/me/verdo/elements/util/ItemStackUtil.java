package me.verdo.elements.util;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;

public class ItemStackUtil {
  public static void replaceActiveItemInPlayerHand(Player player, ItemStack newItemStack) {
    ItemContainer hotbar = player.getInventory().getHotbar();
    byte activeHotbarIndex = player.getInventory().getActiveHotbarSlot();
    hotbar.setItemStackForSlot(activeHotbarIndex, newItemStack);
  }
}
