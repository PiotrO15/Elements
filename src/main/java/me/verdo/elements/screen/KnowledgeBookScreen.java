package me.verdo.elements.screen;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class KnowledgeBookScreen extends InteractiveCustomUIPage<KnowledgeBookScreen.Data> {
    public static class Data {
//        public String leftTitle;
//        public String rightTitle;
//        public int currentPageIndex = 0;

        static BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
//                .append(new KeyedCodec<>("LeftTitle", Codec.STRING), (d, v) -> d.leftTitle = v, d -> d.leftTitle).add()
//                .append(new KeyedCodec<>("RightTitle", Codec.STRING), (d, v) -> d.rightTitle = v, d -> d.rightTitle).add()
//                .append(new KeyedCodec<>("Index", Codec.INTEGER), (d, v) -> d.currentPageIndex = v, d -> d.currentPageIndex).add()
                .build();
    }

    public KnowledgeBookScreen(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/RootsOfKnowledge.ui");

        uiCommandBuilder.append("#LeftPage", "Pages/Templates/TextPage.ui");
        uiCommandBuilder.set("#LeftPage #Title.Text", "Roots of Knowledge");
        uiCommandBuilder.set("#LeftPage #Body.Text", "Welcome to Elements!");

        uiCommandBuilder.append("#RightPage", "Pages/Templates/ListPage.ui");
        uiCommandBuilder.set("#RightPage #Title.Text", "Chapters");

        uiCommandBuilder.append("#Entries", "Pages/Templates/ListEntry.ui");
        uiCommandBuilder.set("#Entries[0] #ItemIcon.ItemId", "Essence_Jar");
        uiCommandBuilder.set("#Entries[0] #Title.Text", "Collecting Essence");

        uiCommandBuilder.append("#Entries", "Pages/Templates/ListEntry.ui");
        uiCommandBuilder.set("#Entries[1] #ItemIcon.ItemId", "Harvesting_Seal");
        uiCommandBuilder.set("#Entries[1] #Title.Text", "Golemancy");

        uiCommandBuilder.append("#Entries", "Pages/Templates/ListEntry.ui");
        uiCommandBuilder.set("#Entries[2] #ItemIcon.ItemId", "Weapon_Staff_Crystal_Ice");
        uiCommandBuilder.set("#Entries[2] #Title.Text", "Spellcrafting");
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl Data data) {
        super.handleDataEvent(ref, store, data);
    }
}

