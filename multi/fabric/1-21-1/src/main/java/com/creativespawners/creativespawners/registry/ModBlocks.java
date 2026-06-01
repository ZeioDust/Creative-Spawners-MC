package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.block.CreativeSpawnerBlock;
import com.creativespawners.creativespawners.block.EmptySpawnerBlock;
import com.creativespawners.creativespawners.block.ItemSpawnerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final EmptySpawnerBlock EMPTY_SPAWNER =
            register("empty_spawner", new EmptySpawnerBlock(spawnerProps()));
    public static final ItemSpawnerBlock ITEM_SPAWNER =
            register("item_spawner", new ItemSpawnerBlock(spawnerProps()));
    public static final CreativeSpawnerBlock CREATIVE_SPAWNER =
            register("creative_spawner", new CreativeSpawnerBlock(spawnerProps()));

    private static BlockBehaviour.Properties spawnerProps() {
        return BlockBehaviour.Properties.of()
                .strength(5.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, CreativeSpawners.id(name), block);
    }

    public static void init() {
        // Triggers static initialization.
    }
}
