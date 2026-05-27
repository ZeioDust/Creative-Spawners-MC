package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import com.creativespawners.creativespawners.block.entity.CreativeSpawnerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreativeSpawners.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemSpawnerBlockEntity>> ITEM_SPAWNER =
            BLOCK_ENTITIES.register("item_spawner",
                    () -> new BlockEntityType<>(ItemSpawnerBlockEntity::new, Set.of(ModBlocks.ITEM_SPAWNER.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeSpawnerBlockEntity>> CREATIVE_SPAWNER =
            BLOCK_ENTITIES.register("creative_spawner",
                    () -> new BlockEntityType<>(CreativeSpawnerBlockEntity::new, Set.of(ModBlocks.CREATIVE_SPAWNER.get())));
}
