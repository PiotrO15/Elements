package me.verdo.elements.recipe;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
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
    public static boolean craft(BlockState blockState, CommandBuffer<EntityStore> commandBuffer) {
        List<EssenceJarData> essenceContainers = new ArrayList<>();
        List<PedestalData> pedestalData = new ArrayList<>();

        World world = blockState.getChunk().getWorld();
        Vector3d searchRadius = RecipeUtil.getSearchRadius(world, blockState);

        world.getBlockType(blockState.getBlockPosition());

        int limit = world.getGameplayConfig().getCraftingConfig().getBenchMaterialChestLimit();

        Store<ChunkStore> store = world.getChunkStore().getStore();
        Vector3d blockPos = blockState.getBlockPosition().toVector3d();

        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = store.getResource(BlockStateModule.get().getItemContainerSpatialResourceType());
        ObjectList<Ref<ChunkStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(blockPos, searchRadius.x, searchRadius.y, searchRadius.z, results);

        if (!results.isEmpty()) {
            ObjectListIterator<Ref<ChunkStore>> iterator = results.iterator();

            while (iterator.hasNext()) {
                Ref<ChunkStore> ref = iterator.next();
                BlockState state = BlockState.getBlockState(ref, ref.getStore());
                if (state.getBlockType().getId().equals("Rootbound_Pedestal")) {
                    StoredItemComponent component = ref.getStore().ensureAndGetComponent(ref, ElementsPlugin.get().storedItem);
                    pedestalData.add(new PedestalData(ref, component, state.getBlockPosition()));
                    if (pedestalData.size() >= limit) {
                        break;
                    }
                } else if (state.getBlockType().getId().contains("Essence_Jar")) {
                    EssenceStorageComponent component = ref.getStore().ensureAndGetComponent(ref, ElementsPlugin.get().essenceStorage);
                    essenceContainers.add(new EssenceJarData(ref, component, state.getBlockPosition()));
                    if (essenceContainers.size() >= limit) {
                        break;
                    }
                }
            }
        }

        if (blockState.getBlockType().getId().equals("Rootbound_Nexus")) {
            Ref<ChunkStore> blockRef = ModChunkUtil.getBlockComponentEntity(world, blockState.getBlockPosition());

            if (blockRef == null)
                return false;

            StoredItemComponent mainInput = blockRef.getStore().ensureAndGetComponent(blockRef, ElementsPlugin.get().storedItem);

            if (mainInput.getStoredItem() == null || mainInput.getStoredItem().isEmpty())
                return false;

            essenceContainers.stream().filter(s -> s.component.getStoredEssenceType() != null).forEach(c -> System.out.println(c.component.getStoredEssenceType() + " " + c.component.getStoredEssenceAmount()));
            pedestalData.stream().filter(s -> !s.component.getStoredItem().isEmpty()).forEach(c -> System.out.println(c.component.getStoredItem()));

            for (EssenceCraftingRecipe recipe : EssenceCraftingRecipe.getAssetMap().getAssetMap().values()) {
                if (!recipe.getMainInput().getItemId().equals(mainInput.getStoredItem().getItemId())) {
                    System.out.println("Failed main check!" + recipe.getMainInput().getItemId() + " != " + mainInput.getStoredItem().getItemId());
                    continue;
                }

                if (!RecipeUtil.hasRequiredEssence(essenceContainers, recipe.getEssenceInputs())) {
                    System.out.println("Failed essence check");
                    continue;
                }

                if (!RecipeUtil.hasRequiredMaterials(pedestalData, recipe.getPedestalInputs())) {
                    System.out.println("Failed pedestal check");
                    continue;
                }

                ParticleUtil.spawnParticleEffect("Beam_Lightning2", blockPos, world.getEntityStore().getStore());

                mainInput.setStoredItem(ItemStack.EMPTY);

                RecipeUtil.consumeEssence(essenceContainers, recipe.getEssenceInputs(), world);

                RecipeUtil.consumeMaterials(pedestalData, recipe.getPedestalInputs(), world, commandBuffer);

                ItemStack output = recipe.getOutput().toItemStack();
                if (output != null)
                    mainInput.setStoredItem(output);

                commandBuffer.run(_ -> ItemDisplayManager.createOrUpdateDisplay(mainInput, world, blockPos.x, blockPos.y, blockPos.z, blockRef));
                break;
            }
        }

        return false;
    }

    public record PedestalData(Ref<ChunkStore> ref, StoredItemComponent component, Vector3i pos) {}
    public record EssenceJarData(Ref<ChunkStore> ref, EssenceStorageComponent component, Vector3i pos) {}
}
