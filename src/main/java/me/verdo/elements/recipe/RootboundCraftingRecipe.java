package me.verdo.elements.recipe;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.ElementsPlugin;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.display.ItemDisplayManager;
import me.verdo.elements.asset.EssenceCraftingRecipe;
import me.verdo.elements.component.EssenceStorageComponent;
import me.verdo.elements.util.ModChunkUtil;
import me.verdo.elements.util.RecipeUtil;

import java.util.ArrayList;
import java.util.List;

public class RootboundCraftingRecipe extends CraftingRecipe {
    public static boolean craft(BlockModule.BlockStateInfo blockStateInfo, Vector3i blockPos, CommandBuffer<EntityStore> commandBuffer) {
        List<EssenceJarData> essenceContainers = new ArrayList<>();
        List<PedestalData> pedestalData = new ArrayList<>();

        World world = blockStateInfo.getChunkRef().getStore().getExternalData().getWorld();
        Vector3d searchRadius = RecipeUtil.getSearchRadius(world, world.getBlockType(blockPos), world.getBlockRotationIndex(blockPos.x, blockPos.y, blockPos.z));
        int limit = world.getGameplayConfig().getCraftingConfig().getBenchMaterialChestLimit();

        Store<ChunkStore> store = world.getChunkStore().getStore();

        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = store.getResource(ElementsPlugin.get().essenceStorageSpatialResourceType);
        List<Ref<ChunkStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(blockPos.toVector3d(), searchRadius.x, searchRadius.y, searchRadius.z, results);

        if (!results.isEmpty()) {
            for (Ref<ChunkStore> ref : results) {
                BlockModule.BlockStateInfo blockStateInfoI = world.getChunkStore().getStore().getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
                Vector3i blockPosI = ModChunkUtil.getBlockPosFromIndex(blockStateInfoI);

                if (world.getBlockType(blockPosI).getId().equals("Rootbound_Pedestal")) {
                    StoredItemComponent component = ref.getStore().ensureAndGetComponent(ref, ElementsPlugin.get().storedItem);
                    pedestalData.add(new PedestalData(ref, component, blockPosI));
                    if (pedestalData.size() >= limit) {
                        break;
                    }
                } else if (world.getBlockType(blockPosI).getId().contains("Essence_Jar")) {
                    EssenceStorageComponent component = ref.getStore().ensureAndGetComponent(ref, ElementsPlugin.get().essenceStorage);
                    essenceContainers.add(new EssenceJarData(component, blockPosI));
                    if (essenceContainers.size() >= limit) {
                        break;
                    }
                }
            }
        }

        if (world.getBlockType(blockPos).getId().equals("Rootbound_Nexus")) {
            Ref<ChunkStore> blockRef = ModChunkUtil.getBlockComponentEntity(world, blockPos);

            if (blockRef == null)
                return false;

            StoredItemComponent mainInput = blockRef.getStore().ensureAndGetComponent(blockRef, ElementsPlugin.get().storedItem);

            if (mainInput.getStoredItem() == null || mainInput.getStoredItem().isEmpty())
                return false;

            for (EssenceCraftingRecipe recipe : EssenceCraftingRecipe.getAssetMap().getAssetMap().values()) {
                if (!recipe.getMainInput().getItemId().equals(mainInput.getStoredItem().getItemId())) {
                    continue;
                }

                if (!RecipeUtil.hasRequiredEssence(essenceContainers, recipe.getEssenceInputs())) {
                    continue;
                }

                if (!RecipeUtil.hasRequiredMaterials(pedestalData, recipe.getPedestalInputs())) {
                    continue;
                }

                Vector3d center = blockPos.toVector3d().add(1.5, 1.25, 1.5);

                mainInput.setStoredItem(ItemStack.EMPTY);

                RecipeUtil.consumeEssence(essenceContainers, recipe.getEssenceInputs(), world);

                RecipeUtil.consumeMaterials(pedestalData, recipe.getPedestalInputs(), world, commandBuffer, center);

                ItemStack output = recipe.getOutput().toItemStack();
                if (output != null)
                    mainInput.setStoredItem(output);

                ParticleUtil.spawnParticleEffect("GreenOrbImpact", center, world.getEntityStore().getStore());

                commandBuffer.run(_ -> ItemDisplayManager.createOrUpdateDisplay(mainInput, world, blockPos.x, blockPos.y, blockPos.z, blockRef));
                break;
            }
        }

        return false;
    }

    public record PedestalData(Ref<ChunkStore> ref, StoredItemComponent component, Vector3i pos) {}
    public record EssenceJarData(EssenceStorageComponent component, Vector3i pos) {}
}
