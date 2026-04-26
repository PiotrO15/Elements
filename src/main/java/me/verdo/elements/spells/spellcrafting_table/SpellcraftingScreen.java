package me.verdo.elements.spells.spellcrafting_table;

import java.util.List;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
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

public class SpellcraftingScreen extends InteractiveCustomUIPage<SpellcraftingScreen.Data> {

  public static class Data {
    static BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
        .build();
  }

  public List<SpellDefinition> spellsOnItem; // TODO: populate with actual spells from current item

  private final static List<String> spellTargetTypes = SpellTargetType.getValidTypes();
  private final static List<String> spellEffectTypes = SpellEffectType.getValidTypes();

  public SpellcraftingScreen(@NonNullDecl PlayerRef playerRef) {
    super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
  }

  @Override
  public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder,
      @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
    Data baseData = new Data();
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

  private static void updateEffectButtons(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder, List<String> effectTypes) {
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

    commandBuilder.append("Spellcrafting/SpellcraftingTable.ui");
    commandBuilder.append("Spellcrafting/TargetTypeSelector.ui");
    commandBuilder.append("Spellcrafting/SpellEffectSelector.ui");
    commandBuilder.append("Spellcrafting/SpellPreview.ui");

    updateTargetButtons(commandBuilder, eventBuilder, spellTargetTypes);
    updateEffectButtons(commandBuilder, eventBuilder, spellEffectTypes);

    sendUpdate(commandBuilder, eventBuilder, true);
  }
}
