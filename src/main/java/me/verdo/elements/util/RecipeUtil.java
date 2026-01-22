package me.verdo.elements.util;

import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import me.verdo.elements.component.StoredItemComponent;
import me.verdo.elements.display.ItemDisplayManager;
import me.verdo.elements.interaction.StoreEssenceInteraction;
import me.verdo.elements.item.ItemPedestalState;
import me.verdo.elements.item.PedestalItemContainer;
import me.verdo.elements.component.EssenceStorageComponent;
import me.verdo.elements.recipe.RootboundCraftingRecipe;

import javax.annotation.Nonnull;
import java.util.List;

public class RecipeUtil {
    protected static List<ItemContainer> getContainersAroundBench(@Nonnull BlockState blockState, List<String> searchedBlockTypes) {
        List<ItemContainer> containers = new ObjectArrayList();
        World world = blockState.getChunk().getWorld();
        Store<ChunkStore> store = world.getChunkStore().getStore();
        int limit = world.getGameplayConfig().getCraftingConfig().getBenchMaterialChestLimit();
        Vector3d blockPos = blockState.getBlockPosition().toVector3d();
        Vector3d searchRadius = getSearchRadius(world, blockState);
        SpatialResource<Ref<ChunkStore>, ChunkStore> blockStateSpatialStructure = store.getResource(BlockStateModule.get().getItemContainerSpatialResourceType());
        ObjectList<Ref<ChunkStore>> results = SpatialResource.getThreadLocalReferenceList();
        blockStateSpatialStructure.getSpatialStructure().ordered3DAxis(blockPos, searchRadius.x, searchRadius.y, searchRadius.z, results);
        if (!results.isEmpty()) {
            ObjectListIterator var35 = results.iterator();

            while(var35.hasNext()) {
                Ref<ChunkStore> ref = (Ref)var35.next();
                BlockState state = BlockState.getBlockState(ref, ref.getStore());
                if (state instanceof ItemPedestalState chest) {
                    if (searchedBlockTypes.contains(state.getBlockType().getId())) {
                        containers.add(chest.getItemContainer());
                        if (containers.size() >= limit) {
                            break;
                        }
                    }
                }
            }
        }

        return containers;
    }

    public static Vector3d getSearchRadius(World world, BlockState blockState) {
        double horizontalRadius = world.getGameplayConfig().getCraftingConfig().getBenchMaterialHorizontalChestSearchRadius();
        double verticalRadius = world.getGameplayConfig().getCraftingConfig().getBenchMaterialVerticalChestSearchRadius();
        Vector3d blockPos = blockState.getBlockPosition().toVector3d();
        BlockBoundingBoxes hitboxAsset = BlockBoundingBoxes.getAssetMap().getAsset(blockState.getBlockType().getHitboxTypeIndex());
        BlockBoundingBoxes.RotatedVariantBoxes rotatedHitbox = hitboxAsset.get(blockState.getRotationIndex());
        Box boundingBox = rotatedHitbox.getBoundingBox();

        double benchWidth = boundingBox.width();
        double benchHeight = boundingBox.height();
        double benchDepth = boundingBox.depth();
        double extraSearchRadius = Math.max(benchWidth, Math.max(benchDepth, benchHeight)) - (double)1.0F;
        return new Vector3d(horizontalRadius + extraSearchRadius, verticalRadius + extraSearchRadius, horizontalRadius + extraSearchRadius);
    }

    public static boolean hasRequiredEssence(List<RootboundCraftingRecipe.EssenceJarData> essenceContainers, EssenceStorageComponent[] requiredEssence) {
        if (requiredEssence == null) {
            return true;
        }

        for (EssenceStorageComponent required : requiredEssence) {
            int totalAmount = essenceContainers.stream()
                    .filter(container -> container.component().getStoredEssenceType() != null &&
                            container.component().getStoredEssenceType().getItemId().equals(required.getStoredEssenceType().getItemId()))
                    .mapToInt(container -> container.component().getStoredEssenceAmount())
                    .sum();

            System.out.println("Required: " + required.getStoredEssenceType() + " Amount: " + required.getStoredEssenceAmount() + " | Total Available: " + totalAmount);

            if (totalAmount < required.getStoredEssenceAmount()) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasRequiredMaterials(List<RootboundCraftingRecipe.PedestalData> containers, MaterialQuantity[] requiredMaterials) {
        if (requiredMaterials == null) {
            return true;
        }

        for (MaterialQuantity required : requiredMaterials) {
            int totalAmount = containers.stream()
                    .filter(container -> !container.component().getStoredItem().isEmpty() && container.component().getStoredItem().getItemId().equals(required.getItemId()))
                    .mapToInt(container -> container.component().getStoredItem().getQuantity())
                    .sum();

            System.out.println("Required: " + required.getItemId() + " Amount: " + required.getQuantity() + " | Total Available: " + totalAmount);

            if (totalAmount < required.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    public static void consumeEssence(List<RootboundCraftingRecipe.EssenceJarData> essenceContainers, EssenceStorageComponent[] requiredEssence, World world) {
        if (requiredEssence == null) {
            return;
        }

        for (EssenceStorageComponent required : requiredEssence) {
            int remaining = required.getStoredEssenceAmount();

            for (RootboundCraftingRecipe.EssenceJarData container : essenceContainers) {
                if (remaining <= 0) break;

                if (container.component().getStoredEssenceType() != null &&
                        container.component().getStoredEssenceType().equals(required.getStoredEssenceType())) {
                    int available = container.component().getStoredEssenceAmount();
                    int toConsume = Math.min(available, remaining);

                    container.component().setStoredEssenceAmount(available - toConsume);
                    if (container.component().getStoredEssenceAmount() == 0) {
                        container.component().setStoredEssenceType(null);
                    }

                    WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(container.pos().x, container.pos().z));
                    StoreEssenceInteraction.displayEssence(chunk, container.pos(), container.component());
                    remaining -= toConsume;
                }
            }
        }
    }

    public static void consumeMaterials(List<RootboundCraftingRecipe.PedestalData> containers, MaterialQuantity[] requiredMaterials, World world, CommandBuffer<EntityStore> commandBuffer) {
        if (requiredMaterials == null) {
            return;
        }

        for (MaterialQuantity required : requiredMaterials) {
            int remaining = required.getQuantity();

            for (RootboundCraftingRecipe.PedestalData container : containers) {
                if (remaining <= 0) break;

                if (container.component().getStoredItem().isEmpty()) continue;

                int available = CraftingManager.matches(required, container.component().getStoredItem()) ? container.component().getStoredItem().getQuantity() : 0;
                if (available > 0) {
                    int toConsume = Math.min(available, remaining);
                    container.component().setStoredItem(ItemStack.EMPTY);
                    WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(container.pos().x, container.pos().z));
                    commandBuffer.run(_ -> ItemDisplayManager.removeDisplayEntity(world, container.ref(), chunk));
                    remaining -= toConsume;
                }
            }
        }
    }
}
