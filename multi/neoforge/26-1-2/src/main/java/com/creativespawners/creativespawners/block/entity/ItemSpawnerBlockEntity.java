package com.creativespawners.creativespawners.block.entity;

import com.creativespawners.creativespawners.block.ItemSpawnerBlock;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ItemSpawnerBlockEntity extends BlockEntity {
    private ItemStack spawnedStack = ItemStack.EMPTY;
    private int tickCounter = 0;

    private static final int BASE_INTERVAL = 200;
    private static final int UPGRADED_INTERVAL = 100;
    private static final int SPAWN_RANGE = 2;

    public ItemSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_SPAWNER.get(), pos, state);
    }

    public ItemStack getSpawnedStack() {
        return spawnedStack;
    }

    public void setSpawnedStack(ItemStack stack) {
        this.spawnedStack = stack.copyWithCount(1);
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!spawnedStack.isEmpty()) {
            output.store("SpawnedStack", ItemStack.CODEC, spawnedStack);
        }
        output.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        spawnedStack = input.read("SpawnedStack", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        tickCounter = input.getIntOr("TickCounter", 0);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ItemSpawnerBlockEntity be) {
        if (be.spawnedStack.isEmpty()) return;

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

        ItemStack toDrop = be.spawnedStack.copy();
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
