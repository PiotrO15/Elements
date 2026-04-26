package me.verdo.elements.spellcrafting_table;

import java.util.List;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
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

        private final List<String> spellTargetTypes = SpellTargetType.getValidTypes();
        private final List<String> spellEffectTypes = SpellEffectType.getValidTypes();

    public SpellcraftingScreen(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        Data baseData = new Data();
        handleDataEvent(ref, store, baseData);
    }

    private static void updateTargetButtons(UICommandBuilder uiCommandBuilder, List<String> targetTypes) {
        setButtonSlot(
                uiCommandBuilder,
                "#TargetSelfButton", "#TargetSelfText",
                targetTypes,
                0
        );
        setButtonSlot(
                uiCommandBuilder,
                "#TargetTouchButton", "#TargetTouchText",
                targetTypes,
                1
        );
        setButtonSlot(
                uiCommandBuilder,
                "#TargetProjectileButton", "#TargetProjectileText",
                targetTypes,
                2
        );
    }

    private static void updateEffectButtons(UICommandBuilder uiCommandBuilder, List<String> effectTypes) {
        setButtonSlot(
                uiCommandBuilder,
                "#EffectAttackButton", "#EffectAttackText",
                effectTypes,
                0
        );
        setButtonSlot(
                uiCommandBuilder,
                "#EffectEmpty1", "#EffectEmpty1Text",
                effectTypes,
                1
        );
        setButtonSlot(
                uiCommandBuilder,
                "#EffectEmpty2", "#EffectEmpty2Text",
                effectTypes,
                2
        );
    }

    private static void setButtonSlot(
            UICommandBuilder uiCommandBuilder,
            String buttonSelector,
            String labelSelector,
            List<String> values,
            int index
    ) {
        if (index < values.size()) {
            uiCommandBuilder.set(buttonSelector + ".Visible", true);
            uiCommandBuilder.set(labelSelector + ".Text", formatButtonText(values.get(index)));
        } else {
            uiCommandBuilder.set(buttonSelector + ".Visible", false);
        }
    }

    private static String formatButtonText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.toLowerCase();
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl Data data) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();

        commandBuilder.append("Spellcrafting/SpellcraftingTable.ui");
        commandBuilder.append("Spellcrafting/TargetSelector.ui");
        commandBuilder.append("Spellcrafting/SpellEffectSelector.ui");
        commandBuilder.append("Spellcrafting/SpellPreview.ui");

        updateTargetButtons(commandBuilder, spellTargetTypes);
        updateEffectButtons(commandBuilder, spellEffectTypes);

        sendUpdate(commandBuilder, eventBuilder, true);
    }
}
