package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.item.CreativeInventoryItem;
import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreativeSpawners.MODID);

    public static final DeferredItem<BlockItem> EMPTY_SPAWNER = ITEMS.registerSimpleBlockItem(ModBlocks.EMPTY_SPAWNER);

    public static final DeferredItem<ItemSpawnerBlockItem> ITEM_SPAWNER = ITEMS.registerItem("item_spawner",
            props -> new ItemSpawnerBlockItem(ModBlocks.ITEM_SPAWNER.get(), props));

    public static final DeferredItem<BlockItem> CREATIVE_SPAWNER = ITEMS.registerSimpleBlockItem(ModBlocks.CREATIVE_SPAWNER);

    public static final DeferredItem<CreativeInventoryItem> CREATIVE_INVENTORY = ITEMS.registerItem("creative_inventory",
            props -> new CreativeInventoryItem(props.stacksTo(1)));
}
