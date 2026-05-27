package com.creativespawners.creativespawners;

import com.creativespawners.creativespawners.registry.ModBlocks;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.creativespawners.creativespawners.registry.ModItems;
import com.creativespawners.creativespawners.registry.ModRecipes;
import com.creativespawners.creativespawners.registry.ModCreativeTabs;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(CreativeSpawners.MODID)
public class CreativeSpawners {
    public static final String MODID = "creative_spawners";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreativeSpawners(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModRecipes.SERIALIZERS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        NeoForge.EVENT_BUS.register(new GameEvents());
    }
}
