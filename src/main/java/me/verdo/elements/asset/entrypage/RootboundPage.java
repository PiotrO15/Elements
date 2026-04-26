package me.verdo.elements.asset.entrypage;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.ResourceType;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import me.verdo.elements.asset.EssenceCraftingRecipe;
import me.verdo.elements.component.EssenceStorageComponent;
import me.verdo.elements.screen.KnowledgeBookScreen;

import java.util.ArrayList;
import java.util.List;

public class RootboundPage implements EntryPage {
    public static final String id = "RootboundPage";

    private String title = "Rootbound Ritual";
    private String craftedItemId = null;

    public static final Codec<RootboundPage> CODEC;

    @Override
    public String id() {
        return id;
    }

    @Override
    public void buildPage(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, KnowledgeBookScreen.Data data, boolean left) {
        String sideSelector = left? "#LeftPage" : "#RightPage";
        commandBuilder.append(sideSelector, "Pages/Templates/RootboundPage.ui");

        commandBuilder.set(sideSelector + " #Title.Text", title);

        EssenceCraftingRecipe recipe = EssenceCraftingRecipe.getAssetMap().getAsset(craftedItemId);
        if (recipe == null) {
            return;
        }

        MaterialQuantity output = recipe.getOutput();
        MaterialQuantity[] pedestalInputs = recipe.getPedestalInputs();
        MaterialQuantity mainInput = recipe.getMainInput();
        EssenceStorageComponent[] essenceInputs = recipe.getEssenceInputs();

        if (output.getItemId() != null) {
            commandBuilder.set(sideSelector + " #RecipeContent #Output #ItemIcon.ItemId", output.getItemId());
        }

        // Expand quantities into individual slots
        List<MaterialQuantity> slots = new ArrayList<>();
        for (MaterialQuantity entry : pedestalInputs) {
            int qty = entry.getQuantity();
            if (qty > 1) {
                for (int j = 0; j < qty; j++) {
                    slots.add(entry.clone(1));
                }
            } else {
                slots.add(entry);
            }
        }

        final double centerX = 175.0;
        final double centerY = 175.0;
        final double radius = 120.0;
        final int slotSize = 60;

        for (int i = 0; i < slots.size(); i++) {
            commandBuilder.append(sideSelector + " #Input", "Pages/Templates/RootboundSlot.ui");
            String entrySelector = sideSelector + " #Input[" + i + "]";

            double angle = (2 * Math.PI * i / slots.size()) - Math.PI / 2.0;

            int slotLeft = (int) Math.round(centerX + radius * Math.cos(angle) - slotSize / 2.0);
            int slotTop  = (int) Math.round(centerY + radius * Math.sin(angle) - slotSize / 2.0);

            Anchor anchor = new Anchor();
            anchor.setLeft(Value.of(slotLeft));
            anchor.setTop(Value.of(slotTop));
            anchor.setWidth(Value.of(slotSize));
            anchor.setHeight(Value.of(slotSize));
            commandBuilder.setObject(entrySelector + ".Anchor", anchor);

            MaterialQuantity entry = slots.get(i);

            if (entry.getItemId() != null) {
                commandBuilder.set(entrySelector + " #ItemIcon.ItemId", entry.getItemId());
            } else if (entry.getResourceTypeId() != null) {
                ResourceType resourceType = ResourceType.getAssetMap().getAsset(entry.getResourceTypeId());

                if (resourceType == null) continue;

                commandBuilder.set(entrySelector + " #ResourceTypeIcon.AssetPath", resourceType.getIcon());
                commandBuilder.set(entrySelector + " #ResourceTypeIcon.Visible", true);
                commandBuilder.set(entrySelector + " #ItemIcon.Visible", false);
            }
        }

        String mainInputSelector = sideSelector + " #MainInput";
        if (mainInput.getItemId() != null) {
            commandBuilder.set(mainInputSelector + " #ItemIcon.ItemId", mainInput.getItemId());
        } else if (mainInput.getResourceTypeId() != null) {
            ResourceType resourceType = ResourceType.getAssetMap().getAsset(mainInput.getResourceTypeId());

            commandBuilder.set(mainInputSelector + " #ResourceTypeIcon.AssetPath", resourceType.getIcon());
            commandBuilder.set(mainInputSelector + " #ResourceTypeIcon.Visible", true);
            commandBuilder.set(mainInputSelector + " #ItemIcon.Visible", false);
        }

        for (int i = 0; i < essenceInputs.length; i++) {
            commandBuilder.append(sideSelector + " #EssenceInputs", "Pages/Templates/EssenceSlot.ui");
            String entrySelector = sideSelector + " #EssenceInputs[" + i + "]";

            EssenceStorageComponent essenceComponent = essenceInputs[i];

            commandBuilder.set(entrySelector + " #EssenceIcon.ItemId", essenceComponent.getStoredEssenceType().getItemId());
            commandBuilder.set(entrySelector + " #Quantity.Text", String.valueOf(essenceComponent.getStoredEssenceAmount()));
        }
    }

    static {
        CODEC = BuilderCodec.builder(RootboundPage.class, RootboundPage::new)
                .append(new KeyedCodec<>("Title", Codec.STRING), (d, v) -> d.title = v, d -> d.title).add()
                .append(new KeyedCodec<>("RecipeId", Codec.STRING), (d, v) -> d.craftedItemId = v, d -> d.craftedItemId).add()
                .build();
    }
}
