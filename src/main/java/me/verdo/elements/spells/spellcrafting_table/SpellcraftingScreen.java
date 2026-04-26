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
  private ItemStack editedItem;

  public static class Data {
    static BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
      .append(new KeyedCodec<>("CurrentSlot", Codec.STRING, true),
        (d, v) -> d.currentSlot = Integer.parseInt(v),
        d -> String.valueOf(d.currentSlot))
      .add()
      .append(new KeyedCodec<>("SelectedTargetType", Codec.STRING, true),
        (d, v) -> d.selectedTargetType = v,
        d -> d.selectedTargetType)
      .add()
      .append(new KeyedCodec<>("SelectedEffectType", Codec.STRING, true),
        (d, v) -> d.selectedEffectType = v,
        d -> d.selectedEffectType)
      .add()
      .append(new KeyedCodec<>("Action", Codec.STRING, true),
        (d, v) -> d.action = v,
        d -> d.action)
      .add()
        .build();

    public ItemStack editedItem = null;
    public int currentSlot = 0;
    public SpellDefinition currentSpell = null;
    public String selectedTargetType = null;
    public String selectedEffectType = null;
    public String action = null;
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
      List<String> targetTypes, int currentSlot, SpellDefinition currentSpell) {

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

      EventData eventData = EventData.of("CurrentSlot", String.valueOf(currentSlot))
          .append("SelectedTargetType", targetType);

      if (currentSpell != null && currentSpell.getEffectType() != null) {
        eventData.append("SelectedEffectType", currentSpell.getEffectType().toString());
      }

      uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonSelector, eventData, false);
    }
  }

  private static void updateEffectButtons(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder,
        List<String> effectTypes, int currentSlot, SpellDefinition currentSpell) {
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

      EventData eventData = EventData.of("CurrentSlot", String.valueOf(currentSlot))
          .append("SelectedEffectType", effectType);

      if (currentSpell != null && currentSpell.getTargetType() != null) {
        eventData.append("SelectedTargetType", currentSpell.getTargetType().toString());
      }

      uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonSelector, eventData, false);
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

  private static void updateSpellPreview(UICommandBuilder uiCommandBuilder, SpellDefinition spell) {
    String targetTypeText = "";
    String effectTypeText = "";

    if (spell != null) {
      targetTypeText = formatButtonText(spell.getTargetType().toString());
      effectTypeText = formatButtonText(spell.getEffectType().toString());
    }

    uiCommandBuilder.set("#SpellPreviewPanel #PreviewTargetTypeText.Text", targetTypeText);
    uiCommandBuilder.set("#SpellPreviewPanel #PreviewSpellEffectText.Text", effectTypeText);
  }

  private static void updateSaveResetButtons(UIEventBuilder uiEventBuilder, int currentSlot, SpellDefinition currentSpell) {
    EventData saveEventData = EventData.of("CurrentSlot", String.valueOf(currentSlot))
        .append("Action", "Save");

    EventData resetEventData = EventData.of("CurrentSlot", String.valueOf(currentSlot))
        .append("Action", "Reset");

    if (currentSpell != null) {
      if (currentSpell.getTargetType() != null) {
        String selectedTargetType = currentSpell.getTargetType().toString();
        saveEventData.append("SelectedTargetType", selectedTargetType);
        resetEventData.append("SelectedTargetType", selectedTargetType);
      }

      if (currentSpell.getEffectType() != null) {
        String selectedEffectType = currentSpell.getEffectType().toString();
        saveEventData.append("SelectedEffectType", selectedEffectType);
        resetEventData.append("SelectedEffectType", selectedEffectType);
      }
    }

    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", saveEventData, false);
    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ResetButton", resetEventData, false);
  }

  private static SpellDefinition ensureCurrentSpell(Data data, SpellSlotsComponent spellSlotsComponent, int maxSlots) {
    if (data.currentSpell != null) {
      return data.currentSpell;
    }

    if (data.currentSlot < 0 || data.currentSlot >= maxSlots) {
      return null;
    }

    SpellDefinition createdSpell = new SpellDefinition(
        "spell_" + (data.currentSlot + 1),
        SpellTargetType.SELF,
        SpellEffectType.DAMAGE);

    if (spellSlotsComponent != null) {
      spellSlotsComponent.addSpell(createdSpell, data.currentSlot);
    }

    data.currentSpell = createdSpell;
    return data.currentSpell;
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

    if (data.selectedTargetType != null && !data.selectedTargetType.isBlank()) {
      SpellDefinition spell = ensureCurrentSpell(data, spellSlotsComponent, maxSlots);
      if (spell != null) {
        spell.setTargetType(SpellTargetType.fromString(data.selectedTargetType));
      }
      data.selectedTargetType = null;
    }

    if (data.selectedEffectType != null && !data.selectedEffectType.isBlank()) {
      SpellDefinition spell = ensureCurrentSpell(data, spellSlotsComponent, maxSlots);
      if (spell != null) {
        spell.setEffectType(SpellEffectType.fromString(data.selectedEffectType));
      }
      data.selectedEffectType = null;
    }

    if ("Save".equals(data.action)) {
      if (data.editedItem != null && data.currentSpell != null) {
        System.out.println("Saving spell: target=" + data.currentSpell.getTargetType() + ", effect=" + data.currentSpell.getEffectType() + " to slot " + data.currentSlot);
        data.editedItem = SpellSlotsComponent.setSpellInItemBySlot(data.editedItem, data.currentSpell, data.currentSlot);
        editedItem = data.editedItem;
        spellSlotsComponent = SpellSlotsComponent.getSpellsFromItem(data.editedItem);
        data.currentSpell = spellSlotsComponent != null ? spellSlotsComponent.getSpell(data.currentSlot) : null;
        SpellSlotsComponent.printSpellsInItem(data.editedItem); // debug - print spells after saving
      }
      data.action = null;
    } else if ("Reset".equals(data.action)) {
      if (data.editedItem != null) {
        data.currentSpell = SpellSlotsComponent.getSpellFromItemBySlot(data.editedItem, data.currentSlot);
      } else {
        data.currentSpell = null;
      }
      data.selectedTargetType = null;
      data.selectedEffectType = null;
      data.action = null;
    }

    updateTargetButtons(commandBuilder, eventBuilder, spellTargetTypes, data.currentSlot, data.currentSpell);
    updateEffectButtons(commandBuilder, eventBuilder, spellEffectTypes, data.currentSlot, data.currentSpell);
    updateRightTabButtons(commandBuilder, eventBuilder, maxSlots, data.currentSlot);
    updateSaveResetButtons(eventBuilder, data.currentSlot, data.currentSpell);
    updateSpellPreview(commandBuilder, data.currentSpell);

    sendUpdate(commandBuilder, eventBuilder, true);
  }
}
