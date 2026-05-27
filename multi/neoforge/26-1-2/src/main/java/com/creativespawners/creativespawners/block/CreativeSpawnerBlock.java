package com.creativespawners.creativespawners.block;

import com.creativespawners.creativespawners.block.entity.CreativeSpawnerBlockEntity;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CreativeSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<CreativeSpawnerBlock> CODEC = simpleCodec(CreativeSpawnerBlock::new);

    public CreativeSpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeSpawnerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.CREATIVE_SPAWNER.get(), CreativeSpawnerBlockEntity::serverTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.7;
        double y = pos.getY() + 0.6;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.7;
        level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.03, 0);
    }
}
