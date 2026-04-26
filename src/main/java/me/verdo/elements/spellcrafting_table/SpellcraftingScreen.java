package me.verdo.elements.spellcrafting_table;

import java.util.List;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.verdo.elements.spells.SpellDefinition;
import me.verdo.elements.spells.SpellEffectType;
import me.verdo.elements.spells.SpellTargetType;

public class SpellcraftingScreen extends InteractiveCustomUIPage<SpellcraftingScreen.Data> {
  private static final int TARGET_BUTTON_SLOTS = 3;
  private static final int EFFECT_BUTTON_SLOTS = 3;

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
    for (int i = 0; i < TARGET_BUTTON_SLOTS; i++) {
      // setButtonSlot(
      //     uiCommandBuilder,
      //     "#TargetButton" + targetTypes.get(i), "#TargetText",
      //     targetTypes,
      //     i);
    }
  }

  private static void updateEffectButtons(UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder,
      List<String> effectTypes) {
    for (int i = 0; i < EFFECT_BUTTON_SLOTS; i++) {
      // ButtonSlot buttonSlot = new ButtonSlot(effectTypes.get(i));

      uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Back",
          EventData.of("Group", "SpellEffectSelector")
              .append("Button","EffectButton" + effectTypes.get(i)),
          false);
    }
  }

  public static class ButtonSlot {
    public final String entry;

    public ButtonSlot(String entry) {
      this.entry = entry;
    }

    public static String encode(List<ButtonSlot> history) {
      // Simple encoding: join entries with a delimiter
      return String.join("|", history.stream().map(e -> e.entry).toArray(String[]::new));
    }
  }

  // private static void setButtonSlot(
  //     UICommandBuilder uiCommandBuilder,
  //     String buttonSelector,
  //     String labelSelector,
  //     List<String> values,
  //     int index) {
  //   if (index < values.size()) {
  //     uiCommandBuilder.set(buttonSelector + ".Visible", true);
  //     uiCommandBuilder.set(labelSelector + ".Text", formatButtonText(values.get(index)));
  //   } else {
  //     uiCommandBuilder.set(buttonSelector + ".Visible", false);
  //   }
  // }

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
    commandBuilder.append("Spellcrafting/TargetSelector.ui");
    commandBuilder.append("Spellcrafting/SpellEffectSelector.ui");
    commandBuilder.append("Spellcrafting/SpellPreview.ui");

    // updateTargetButtons(commandBuilder, eventBuilder, spellTargetTypes);
    updateEffectButtons(commandBuilder, eventBuilder, spellEffectTypes);

    sendUpdate(commandBuilder, eventBuilder, true);
  }
}
