package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.block.EmptySpawnerBlock;
import com.creativespawners.creativespawners.block.ItemSpawnerBlock;
import com.creativespawners.creativespawners.block.CreativeSpawnerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreativeSpawners.MODID);

    private static BlockBehaviour.Properties spawnerProps() {
        return BlockBehaviour.Properties.of()
                .strength(5.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static final DeferredBlock<EmptySpawnerBlock> EMPTY_SPAWNER = BLOCKS.registerBlock("empty_spawner",
            EmptySpawnerBlock::new, spawnerProps());

    public static final DeferredBlock<ItemSpawnerBlock> ITEM_SPAWNER = BLOCKS.registerBlock("item_spawner",
            ItemSpawnerBlock::new, spawnerProps());

    public static final DeferredBlock<CreativeSpawnerBlock> CREATIVE_SPAWNER = BLOCKS.registerBlock("creative_spawner",
            CreativeSpawnerBlock::new, spawnerProps());
}
