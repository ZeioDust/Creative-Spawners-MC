package com.creativespawners.creativespawners.block.entity;

import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.creativespawners.creativespawners.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CreativeSpawnerBlockEntity extends BlockEntity {
    private int tickCounter = 0;
    private static final int SPAWN_INTERVAL = 300;
    private static final int SPAWN_RANGE = 2;

    public CreativeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_SPAWNER.get(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tickCounter = input.getIntOr("TickCounter", 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeSpawnerBlockEntity be) {
        be.tickCounter++;
        boolean upgraded = state.hasProperty(com.creativespawners.creativespawners.block.CreativeSpawnerBlock.UPGRADED)
                && state.getValue(com.creativespawners.creativespawners.block.CreativeSpawnerBlock.UPGRADED);
        int interval = upgraded ? SPAWN_INTERVAL / 2 : SPAWN_INTERVAL;
        if (be.tickCounter >= interval) {
            be.tickCounter = 0;
            spawnCreativeInventory(level, pos);
        }
    }

    private static void spawnCreativeInventory(Level level, BlockPos center) {
        BlockPos dropPos = findDropPosition(level, center);
        if (dropPos == null) return;

        ItemStack stack = new ItemStack(ModItems.CREATIVE_INVENTORY.get());
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
