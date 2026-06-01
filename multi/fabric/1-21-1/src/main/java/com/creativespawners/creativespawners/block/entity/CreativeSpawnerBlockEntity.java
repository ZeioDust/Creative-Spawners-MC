package com.creativespawners.creativespawners.block.entity;

import com.creativespawners.creativespawners.block.CreativeSpawnerBlock;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.creativespawners.creativespawners.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeSpawnerBlockEntity extends BlockEntity {
    private int tickCounter = 0;
    private static final int SPAWN_INTERVAL = 300;
    private static final int SPAWN_RANGE = 2;

    public CreativeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_SPAWNER, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tickCounter = tag.getInt("TickCounter");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeSpawnerBlockEntity be) {
        be.tickCounter++;
        boolean upgraded = state.hasProperty(CreativeSpawnerBlock.UPGRADED) && state.getValue(CreativeSpawnerBlock.UPGRADED);
        int interval = upgraded ? SPAWN_INTERVAL / 2 : SPAWN_INTERVAL;
        if (be.tickCounter >= interval) {
            be.tickCounter = 0;
            spawnCreativeInventory(level, pos);
        }
    }

    private static void spawnCreativeInventory(Level level, BlockPos center) {
        BlockPos dropPos = findDropPosition(level, center);
        if (dropPos == null) return;

        ItemStack stack = new ItemStack(ModItems.CREATIVE_INVENTORY);
        double x = dropPos.getX() + 0.5;
        double y = dropPos.getY() + 0.5;
        double z = dropPos.getZ() + 0.5;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
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
