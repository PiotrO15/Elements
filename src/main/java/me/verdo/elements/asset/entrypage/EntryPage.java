package me.verdo.elements.asset.entrypage;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import me.verdo.elements.screen.KnowledgeBookScreen;

public interface EntryPage {
    CodecMapCodec<EntryPage> CODEC = new CodecMapCodec<>("Type");

    String id();

    void buildPage(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, KnowledgeBookScreen.Data data, boolean left);
}
