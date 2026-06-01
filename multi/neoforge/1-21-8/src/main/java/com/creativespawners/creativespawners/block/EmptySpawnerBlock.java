package com.creativespawners.creativespawners.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EmptySpawnerBlock extends Block {
    public static final MapCodec<EmptySpawnerBlock> CODEC = simpleCodec(EmptySpawnerBlock::new);

    public EmptySpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
