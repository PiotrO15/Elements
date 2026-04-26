package me.verdo.elements.asset.entrypage;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import me.verdo.elements.screen.KnowledgeBookScreen;

public class TextPage implements EntryPage {
    public static final String id = "TextPage";

    private String title = "";
    private String text = "";

    public static final Codec<TextPage> CODEC;

    @Override
    public String id() {
        return id;
    }

    @Override
    public void buildPage(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, KnowledgeBookScreen.Data data, boolean left) {
        String sideSelector = left? "#LeftPage" : "#RightPage";
        commandBuilder.append(sideSelector, "Pages/Templates/TextPage.ui");

        commandBuilder.set(sideSelector + " #Title.Text", title);
        commandBuilder.set(sideSelector + " #Body.Text", text);
    }

    static {
        CODEC = BuilderCodec.builder(TextPage.class, TextPage::new)
                .append(new KeyedCodec<>("Title", Codec.STRING), (d, v) -> d.title = v, d -> d.title).add()
                .append(new KeyedCodec<>("Text", Codec.STRING), (d, v) -> d.text = v, d -> d.text).add()
                .build();
    }
}
