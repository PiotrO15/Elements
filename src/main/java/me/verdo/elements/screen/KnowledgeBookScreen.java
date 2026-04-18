package me.verdo.elements.screen;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.asset.KnowledgeBookEntry;
import me.verdo.elements.asset.entrypage.EntryPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KnowledgeBookScreen extends InteractiveCustomUIPage<KnowledgeBookScreen.Data> {
    public static class Data {
        public int currentPageGroup = 0;
        public String currentEntry = "Root";
        public List<PageHistoryEntry> pageHistory;

        static BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Group", Codec.STRING), (d, v) -> d.currentPageGroup = Integer.parseInt(v), d -> String.valueOf(d.currentPageGroup)).add()
                .append(new KeyedCodec<>("Entry", Codec.STRING), (d, v) -> d.currentEntry = v, d -> d.currentEntry).add()
                .append(new KeyedCodec<>("PageHistory", Codec.STRING),
                        (d, v) -> d.pageHistory = PageHistoryEntry.decode(v),
                        d -> PageHistoryEntry.encode(d.pageHistory)).add()
                .build();
    }

    public static class PageHistoryEntry {
        private String entry = "";
        private int group = 0;

        public static final Codec<PageHistoryEntry> CODEC;

        public PageHistoryEntry() {}

        public PageHistoryEntry(String entry, int group) {
            this.entry = entry;
            this.group = group;
        }

        static {
            CODEC = BuilderCodec.builder(PageHistoryEntry.class, PageHistoryEntry::new)
                    .append(new KeyedCodec<>("Entry", Codec.STRING), (d, v) -> d.entry = v, d -> d.entry).add()
                    .append(new KeyedCodec<>("Group", Codec.STRING), (d, v) -> d.group = Integer.parseInt(v), d -> String.valueOf(d.group)).add()
                    .build();
        }

        public static String encode(List<PageHistoryEntry> history) {
            if (history == null || history.isEmpty()) return "";
            return history.stream()
                    .map(e -> e.entry + ":" + e.group)
                    .collect(Collectors.joining(","));
        }

        public static List<PageHistoryEntry> decode(String raw) {
            if (raw == null || raw.isEmpty()) return new ArrayList<>();
            return Arrays.stream(raw.split(","))
                    .map(s -> {
                        String[] parts = s.split(":", 2);
                        return new PageHistoryEntry(parts[0], Integer.parseInt(parts[1]));
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public KnowledgeBookScreen(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/RootsOfKnowledge.ui");

        Data baseData = new Data();
        baseData.currentPageGroup = 0;
        baseData.currentEntry = "Root";
        baseData.pageHistory = new ArrayList<>();

        handleDataEvent(ref, store, baseData);
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl Data data) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();

        KnowledgeBookEntry entry = KnowledgeBookEntry.getAssetStore().getAssetMap().getAsset(data.currentEntry);

        if (entry == null) {
            return;
        }

        commandBuilder.append("Pages/RootsOfKnowledge.ui");

        EntryPage leftPage = entry.getPage(data.currentPageGroup * 2);
        leftPage.buildPage(commandBuilder, eventBuilder, data, true);

        if (entry.getBookSize() > data.currentPageGroup * 2 + 1) {
            EntryPage rightPage = entry.getPage(data.currentPageGroup * 2 + 1);
            rightPage.buildPage(commandBuilder, eventBuilder, data, false);
        }

        if (data.pageHistory != null && !data.pageHistory.isEmpty()) {
            PageHistoryEntry lastEntry = data.pageHistory.getLast();
            List<PageHistoryEntry> history = new ArrayList<>(data.pageHistory);
            history.removeLast();

            commandBuilder.set("#Back.Visible", true);
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Back",
                    EventData.of("Group", String.valueOf(lastEntry.group))
                            .append("Entry", lastEntry.entry)
                            .append("PageHistory", PageHistoryEntry.encode(history)),
                    false
            );
        } else {
            commandBuilder.set("#Back.Visible", false);
        }

        if (entry.hasNext(data.currentPageGroup)) {
            commandBuilder.set("#ArrowRight.Visible", true);
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ArrowRight",
                    EventData.of("Group", String.valueOf(data.currentPageGroup + 1))
                            .append("Entry", data.currentEntry)
                            .append("PageHistory", PageHistoryEntry.encode(data.pageHistory)),
                    false);
        } else {
            commandBuilder.set("#ArrowRight.Visible", false);
        }

        if (entry.hasPrevious(data.currentPageGroup)) {
            commandBuilder.set("#ArrowLeft.Visible", true);
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ArrowLeft",
                    EventData.of("Group", String.valueOf(data.currentPageGroup - 1))
                            .append("Entry", data.currentEntry)
                            .append("PageHistory", PageHistoryEntry.encode(data.pageHistory)),
                    false);
        } else {
            commandBuilder.set("#ArrowLeft.Visible", false);
        }

        sendUpdate(commandBuilder, eventBuilder, true);
        int soundEventIndex = SoundEvent.getAssetMap().getIndex("SFX_Drag_Items_Paper");
        SoundUtil.playSoundEvent2d(ref, soundEventIndex, SoundCategory.UI, store);
    }
}
