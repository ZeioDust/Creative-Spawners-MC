package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.item.CreativeInventoryItem;
import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final BlockItem EMPTY_SPAWNER =
            register("empty_spawner", new BlockItem(ModBlocks.EMPTY_SPAWNER, new Item.Properties()));
    public static final ItemSpawnerBlockItem ITEM_SPAWNER =
            register("item_spawner", new ItemSpawnerBlockItem(ModBlocks.ITEM_SPAWNER, new Item.Properties()));
    public static final BlockItem CREATIVE_SPAWNER =
            register("creative_spawner", new BlockItem(ModBlocks.CREATIVE_SPAWNER, new Item.Properties()));
    public static final CreativeInventoryItem CREATIVE_INVENTORY =
            register("creative_inventory", new CreativeInventoryItem(new Item.Properties().stacksTo(1)));

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, CreativeSpawners.id(name), item);
    }

    public static void init() {
    }
}
