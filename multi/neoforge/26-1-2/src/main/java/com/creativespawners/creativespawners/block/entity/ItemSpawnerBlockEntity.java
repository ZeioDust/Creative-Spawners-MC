package com.creativespawners.creativespawners.block.entity;

import com.creativespawners.creativespawners.block.ItemSpawnerBlock;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ItemSpawnerBlockEntity extends BlockEntity {
    private Item spawnedItem = Items.AIR;
    private int tickCounter = 0;

    private static final int BASE_INTERVAL = 200;
    private static final int UPGRADED_INTERVAL = 100;
    private static final int SPAWN_RANGE = 2;

    public ItemSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_SPAWNER.get(), pos, state);
    }

    public Item getSpawnedItem() {
        return spawnedItem;
    }

    public void setSpawnedItem(Item item) {
        this.spawnedItem = item;
        setChanged();
    }

    public String getSpawnedItemId() {
        var key = BuiltInRegistries.ITEM.getKey(spawnedItem);
        return key != null ? key.toString() : "";
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        var key = BuiltInRegistries.ITEM.getKey(spawnedItem);
        if (key != null) {
            output.putString("SpawnedItemId", key.toString());
        }
        output.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getString("SpawnedItemId").ifPresent(idStr -> {
            var id = Identifier.tryParse(idStr);
            if (id != null) {
                Item found = BuiltInRegistries.ITEM.getValue(id);
                if (found != null) spawnedItem = found;
            }
        });
        tickCounter = input.getIntOr("TickCounter", 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ItemSpawnerBlockEntity be) {
        if (be.spawnedItem == Items.AIR) return;

        be.tickCounter++;
        boolean upgraded = state.getValue(ItemSpawnerBlock.UPGRADED);
        int interval = upgraded ? UPGRADED_INTERVAL : BASE_INTERVAL;

        if (be.tickCounter >= interval) {
            be.tickCounter = 0;
            spawnItem(level, pos, be);
        }
    }

    private static void spawnItem(Level level, BlockPos spawnerPos, ItemSpawnerBlockEntity be) {
        BlockPos dropPos = findDropPosition(level, spawnerPos);
        if (dropPos == null) return;

        ItemStack toDrop = new ItemStack(be.spawnedItem);
        double x = dropPos.getX() + 0.5;
        double y = dropPos.getY() + 0.5;
        double z = dropPos.getZ() + 0.5;
        ItemEntity entity = new ItemEntity(level, x, y, z, toDrop);
        entity.setDeltaMovement(0, 0, 0);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    private static BlockPos findDropPosition(Level level, BlockPos center) {
        var random = level.getRandom();
        for (int attempts = 0; attempts < 10; attempts++) {
            int dx = random.nextInt(SPAWN_RANGE * 2 + 1) - SPAWN_RANGE;
            int dz = random.nextInt(SPAWN_RANGE * 2 + 1) - SPAWN_RANGE;
            if (dx == 0 && dz == 0) continue;

            BlockPos candidate = center.offset(dx, 0, dz);
            if (level.getBlockState(candidate).isAir()) {
                return candidate;
            }
        }
        return null;
    }
}
