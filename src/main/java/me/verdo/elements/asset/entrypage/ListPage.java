package me.verdo.elements.asset.entrypage;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import me.verdo.elements.screen.KnowledgeBookScreen;

import java.util.ArrayList;
import java.util.List;

public class ListPage implements EntryPage {
    public static final String id = "ListPage";

    private String title = "";
    private ListPageEntry[] entries = new ListPageEntry[0];

    public static final Codec<ListPage> CODEC;

    @Override
    public String id() {
        return id;
    }

    @Override
    public void buildPage(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, KnowledgeBookScreen.Data data, boolean left) {
        String sideSelector = left? "#LeftPage" : "#RightPage";

        commandBuilder.append(sideSelector, "Pages/Templates/ListPage.ui");
        commandBuilder.set(sideSelector + " #Title.Text", title);

        for (int i = 0; i < Math.min(entries.length, 6); i++) {
            commandBuilder.append("#Entries", "Pages/Templates/ListEntry.ui");
            String entrySelector = "#Entries[" + i + "]";

            commandBuilder.set(entrySelector + " #Title.Text", entries[i].title);
            commandBuilder.set(entrySelector + " #ItemIcon.ItemId", entries[i].itemIcon);

            List<KnowledgeBookScreen.PageHistoryEntry> newHistory = new ArrayList<>(data.pageHistory);
            newHistory.add(new KnowledgeBookScreen.PageHistoryEntry(data.currentEntry, data.currentPageGroup));

            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, entrySelector,
                    EventData.of("Entry", entries[i].link)
                            .append("Group", "0")
                            .append("PageHistory", KnowledgeBookScreen.PageHistoryEntry.encode(newHistory)),
                    false
                    );
        }
    }

    static {
        CODEC = BuilderCodec.builder(ListPage.class, ListPage::new)
                .append(new KeyedCodec<>("Title", Codec.STRING), (d, v) -> d.title = v, d -> d.title).add()
                .append(new KeyedCodec<>("Entries", new ArrayCodec<>(ListPageEntry.CODEC, ListPageEntry[]::new), true), (knowledgeBookEntry, strings) -> knowledgeBookEntry.entries = strings, knowledgeBookEntry -> knowledgeBookEntry.entries).add()
                .build();
    }

    public static final class ListPageEntry {
        private String itemIcon;
        private String title;
        private String link;

        public static final Codec<ListPageEntry> CODEC;

        static {
                CODEC = BuilderCodec.builder(ListPageEntry.class, ListPageEntry::new)
                        .append(new KeyedCodec<>("ItemIcon", Codec.STRING), (t, s) -> t.itemIcon = s, t -> t.itemIcon).add()
                        .append(new KeyedCodec<>("Title", Codec.STRING), (t, s) -> t.title = s, t -> t.title).add()
                        .append(new KeyedCodec<>("Link", Codec.STRING), (t, s) ->  t.link = s, t -> t.link).add()
                        .build();
            }
        }
}
