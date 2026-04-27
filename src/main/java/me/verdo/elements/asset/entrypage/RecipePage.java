package me.verdo.elements.asset.entrypage;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ResourceType;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import me.verdo.elements.screen.KnowledgeBookScreen;

import java.util.ArrayList;
import java.util.List;

public class RecipePage implements EntryPage {
    public static final String id = "RecipePage";

    private String title = "Crafting";
    private String text = "";
    private MaterialQuantity output = null;
    private MaterialQuantity[] input = new MaterialQuantity[0];
    private String craftedItemId = null;

    public static final Codec<RecipePage> CODEC;

    @Override
    public String id() {
        return id;
    }

    @Override
    public void buildPage(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, KnowledgeBookScreen.Data data, boolean left) {
        String sideSelector = left? "#LeftPage" : "#RightPage";
        commandBuilder.append(sideSelector, "Pages/Templates/RecipePage.ui");

        commandBuilder.set(sideSelector + " #Title.Text", title);
        commandBuilder.set(sideSelector + " #Body.Text", text);

        Item craftedItem = Item.getAssetMap().getAsset(craftedItemId);
        if (craftedItem != null && craftedItem.hasRecipesToGenerate()) {
            List<CraftingRecipe> recipes = new ArrayList<>();
            craftedItem.collectRecipesToGenerate(recipes);

            input = recipes.getFirst().getInput();
            output = recipes.getFirst().getPrimaryOutput();
        }

        if (output.getItemId() != null) {
            commandBuilder.set(sideSelector + " #RecipeContent #Output #ItemIcon.ItemId", output.getItemId());
            commandBuilder.set(sideSelector + " #RecipeContent #Output #Count.Text", String.valueOf(output.getQuantity()));
        }

        for (int i = 0; i < input.length; i++) {
            commandBuilder.append(sideSelector + " #Input", "Pages/Templates/RecipeSlot.ui");
            String entrySelector = sideSelector + " #Input[" + i + "]";

            MaterialQuantity entry = input[i];

            if (entry.getItemId() != null) {
                commandBuilder.set(entrySelector + " #ItemIcon.ItemId", entry.getItemId());
            } else if (entry.getResourceTypeId() != null) {
                ResourceType resourceType = ResourceType.getAssetMap().getAsset(entry.getResourceTypeId());

                if (resourceType == null) continue;

                commandBuilder.set(entrySelector + " #ResourceTypeIcon.AssetPath", resourceType.getIcon());
                commandBuilder.set(entrySelector + " #ResourceTypeIcon.Visible", true);
                commandBuilder.set(entrySelector + " #ItemIcon.Visible", false);
            }

            commandBuilder.set(entrySelector + " #Quantity.Text", String.valueOf(entry.getQuantity()));
        }
    }

    static {
        CODEC = BuilderCodec.builder(RecipePage.class, RecipePage::new)
                .append(new KeyedCodec<>("Title", Codec.STRING), (d, v) -> d.title = v, d -> d.title).add()
                .append(new KeyedCodec<>("Text", Codec.STRING), (d, v) -> d.text = v, d -> d.text).add()
                .append(new KeyedCodec<>("Output", MaterialQuantity.CODEC), (d, v) -> d.output = v, d -> d.output).add()
                .append(new KeyedCodec<>("Input", new ArrayCodec<>(MaterialQuantity.CODEC, MaterialQuantity[]::new)), (craftingRecipe, objects) -> craftingRecipe.input = objects, (craftingRecipe) -> craftingRecipe.input).add()
                .append(new KeyedCodec<>("CraftedItem", Codec.STRING), (d, v) -> d.craftedItemId = v, d -> d.craftedItemId).add()
                .build();
    }
}
