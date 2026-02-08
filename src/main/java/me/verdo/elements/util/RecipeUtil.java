package me.verdo.elements.util;

import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.verdo.elements.display.ItemDisplayManager;
import me.verdo.elements.interaction.StoreEssenceInteraction;
import me.verdo.elements.component.EssenceStorageComponent;
import me.verdo.elements.recipe.RootboundCraftingRecipe;

import java.util.List;

public class RecipeUtil {
    public static Vector3d getSearchRadius(World world, BlockState blockState) {
        double horizontalRadius = world.getGameplayConfig().getCraftingConfig().getBenchMaterialHorizontalChestSearchRadius();
        double verticalRadius = world.getGameplayConfig().getCraftingConfig().getBenchMaterialVerticalChestSearchRadius();
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

                    ParticleUtil.spawnParticleEffect("GreenOrbImpact", container.pos().toVector3d().add(0.5, 1.25, 0.5), world.getEntityStore().getStore());
                }
            }
        }
    }
}
