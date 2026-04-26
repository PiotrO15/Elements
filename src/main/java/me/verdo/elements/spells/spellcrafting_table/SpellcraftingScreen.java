package me.verdo.elements.spells.spellcrafting_table;

import java.util.List;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.spells.SpellDefinition;
import me.verdo.elements.spells.SpellEffectType;
import me.verdo.elements.spells.SpellTargetType;
import me.verdo.elements.spells.SpellSlotsComponent;

public class SpellcraftingScreen extends InteractiveCustomUIPage<SpellcraftingScreen.Data> {
  private final ItemStack editedItem;

  public static class Data {
    static BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
      .append(new KeyedCodec<>("CurrentSlot", Codec.STRING),
        (d, v) -> d.currentSlot = Integer.parseInt(v),
        d -> String.valueOf(d.currentSlot))
      .add()
        .build();

    public ItemStack editedItem = null;
    public int currentSlot = 0;
    public SpellDefinition currentSpell = null;
  }

  public List<SpellDefinition> spellsOnItem; // TODO: populate with actual spells from current item

  private final static List<String> spellTargetTypes = SpellTargetType.getValidTypes();
  private final static List<String> spellEffectTypes = SpellEffectType.getValidTypes();

  public SpellcraftingScreen(@NonNullDecl PlayerRef playerRef, ItemStack editedItem) {
    super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    this.editedItem = editedItem;
  }

  @Override
  public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder,
      @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
    Data baseData = new Data();
    baseData.editedItem = editedItem;
    baseData.currentSlot = 0;
    SpellSlotsComponent spellSlotsComponent = editedItem != null ? SpellSlotsComponent.getSpellsFromItem(editedItem) : null;
    baseData.currentSpell = spellSlotsComponent != null ? spellSlotsComponent.getSpell(baseData.currentSlot) : null;
    handleDataEvent(ref, store, baseData);
  }

  private static void updateTargetButtons(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder,
      List<String> targetTypes) {

    int targetColumns = 3;
    for (int i = 0; i < targetTypes.size(); i++) {
      String targetType = targetTypes.get(i);
      String buttonSelector = "#TargetTypeSelector #TargetButtons[" + i + "]";

      uiCommandBuilder.append("#TargetTypeSelector #TargetButtons", "Spellcrafting/TargetTypeButton.ui");

      Anchor anchor = new Anchor();
      anchor.setLeft(Value.of(35 + (i % targetColumns) * 150));
      anchor.setTop(Value.of(20 + (i / targetColumns) * 110));
      anchor.setWidth(Value.of(120));
      anchor.setHeight(Value.of(95));
      uiCommandBuilder.setObject(buttonSelector + ".Anchor", anchor);
      uiCommandBuilder.set(buttonSelector + " #TargetTypeName.Text", formatButtonText(targetType));
    }
  }

  private static void updateEffectButtons(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder,
      List<String> effectTypes) {
    int targetColumns = 5;

    for (int i = 0; i < effectTypes.size(); i++) {
      String effectType = effectTypes.get(i);

      String buttonSelector = "#SpellEffectSelector #EffectButtons[" + i + "]";
      uiCommandBuilder.append("#SpellEffectSelector #EffectButtons", "Spellcrafting/SpellEffectButton.ui");

      Anchor anchor = new Anchor();
      anchor.setLeft(Value.of(35 + i % targetColumns * (150)));
      anchor.setTop(Value.of(20 + (i / targetColumns) * 110));
      anchor.setWidth(Value.of(120));
      anchor.setHeight(Value.of(95));
      uiCommandBuilder.setObject(buttonSelector + ".Anchor", anchor);
      uiCommandBuilder.set(buttonSelector + " #EffectTypeName.Text", formatButtonText(effectType));
    }
  }

  private static void updateRightTabButtons(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder,
      int maxSlots, int currentSlot) {
    for (int i = 0; i < maxSlots; i++) {
      String buttonSelector = "#SpellcraftingRoot #RightTabs[" + i + "]";
      boolean isActive = i == currentSlot;

      uiCommandBuilder.append("#SpellcraftingRoot #RightTabs", "Spellcrafting/RightTabButton.ui");

      Anchor anchor = new Anchor();
      anchor.setLeft(Value.of(isActive ? -10 : 0));
      anchor.setTop(Value.of(i * 95));
      anchor.setWidth(Value.of(isActive ? 120 : 110));
      anchor.setHeight(Value.of(72));
      uiCommandBuilder.setObject(buttonSelector + ".Anchor", anchor);
      uiCommandBuilder.set(buttonSelector + " #RightTabText.Text", isActive ? ">" + (i + 1) + "<" : String.valueOf(i + 1));

      uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonSelector,
          EventData.of("CurrentSlot", String.valueOf(i)),
          false);
    }
  }

  private static int getEditedItemMaxSlots(ItemStack editedItem) {
    if (editedItem == null) {
      return 0;
    }

    SpellSlotsComponent spellSlotsComponent = SpellSlotsComponent.getSpellsFromItem(editedItem);
    if (spellSlotsComponent == null) {
      return 0;
    }

    return Math.max(0, spellSlotsComponent.getMaxSlots());
  }

  private static String formatButtonText(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }
    return value.toLowerCase();
  }

  @Override
  public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store,
      @NonNullDecl Data data) {
    UICommandBuilder commandBuilder = new UICommandBuilder();
    UIEventBuilder eventBuilder = new UIEventBuilder();

    if (data.editedItem == null) {
      data.editedItem = editedItem;
    }

    SpellSlotsComponent spellSlotsComponent = data.editedItem != null ? SpellSlotsComponent.getSpellsFromItem(data.editedItem) : null;
    data.currentSpell = spellSlotsComponent != null ? spellSlotsComponent.getSpell(data.currentSlot) : null;

    commandBuilder.append("Spellcrafting/SpellcraftingTable.ui");
    commandBuilder.append("Spellcrafting/TargetTypeSelector.ui");
    commandBuilder.append("Spellcrafting/SpellEffectSelector.ui");
    commandBuilder.append("Spellcrafting/SpellPreview.ui");

    int maxSlots = getEditedItemMaxSlots(data.editedItem);
    if (maxSlots > 0) {
      data.currentSlot = Math.max(0, Math.min(data.currentSlot, maxSlots - 1));
    } else {
      data.currentSlot = 0;
    }

    data.currentSpell = spellSlotsComponent != null ? spellSlotsComponent.getSpell(data.currentSlot) : null;

    updateTargetButtons(commandBuilder, eventBuilder, spellTargetTypes);
    updateEffectButtons(commandBuilder, eventBuilder, spellEffectTypes);
    updateRightTabButtons(commandBuilder, eventBuilder, maxSlots, data.currentSlot);

    sendUpdate(commandBuilder, eventBuilder, true);
  }
}
