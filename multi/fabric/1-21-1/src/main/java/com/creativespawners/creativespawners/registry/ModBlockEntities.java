package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.block.entity.CreativeSpawnerBlockEntity;
import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<ItemSpawnerBlockEntity> ITEM_SPAWNER =
            register("item_spawner",
                    BlockEntityType.Builder.of(ItemSpawnerBlockEntity::new, ModBlocks.ITEM_SPAWNER).build(null));

    public static final BlockEntityType<CreativeSpawnerBlockEntity> CREATIVE_SPAWNER =
            register("creative_spawner",
                    BlockEntityType.Builder.of(CreativeSpawnerBlockEntity::new, ModBlocks.CREATIVE_SPAWNER).build(null));

    private static <T extends BlockEntityType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreativeSpawners.id(name), type);
    }

    public static void init() {
    }
}
