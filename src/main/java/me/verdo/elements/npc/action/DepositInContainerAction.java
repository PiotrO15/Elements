package me.verdo.elements.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DepositInContainerAction extends ActionBase {
    public DepositInContainerAction(@NonNullDecl BuilderActionBase builderActionBase) {
        super(builderActionBase);
    }

    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);

        CombinedItemContainer inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.HOTBAR_FIRST);

        IPositionProvider positionProvider = sensorInfo.getPositionProvider();
        if (positionProvider == null) return false;

        Vector3i target = new Vector3i((int) Math.floor(positionProvider.getX()), (int) Math.floor(positionProvider.getY()), (int) Math.floor(positionProvider.getZ()));
        World world = store.getExternalData().getWorld();

        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(target.x, target.z));

        if (chunk == null) return false;

        int localX = ChunkUtil.localCoordinate(target.x);
        int localZ = ChunkUtil.localCoordinate(target.z);
        Ref<ChunkStore> chunkStoreRef = chunk.getBlockComponentEntity(localX, target.y, localZ);

        if (chunkStoreRef == null) return false;

        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
        ItemContainerBlock container = chunkStore.getComponent(chunkStoreRef, ItemContainerBlock.getComponentType());

        if (container == null) return false;

        inventory.moveAllItemStacksTo(container.getItemContainer());
        return true;
    }
}
