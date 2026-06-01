package com.creativespawners.creativespawners;

import com.creativespawners.creativespawners.network.CreativeTimerPayload;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.creativespawners.creativespawners.registry.ModBlocks;
import com.creativespawners.creativespawners.registry.ModCreativeTabs;
import com.creativespawners.creativespawners.registry.ModItems;
import com.creativespawners.creativespawners.registry.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class CreativeSpawnersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModRecipes.init();
        ModCreativeTabs.init();

        PayloadTypeRegistry.playS2C().register(CreativeTimerPayload.TYPE, CreativeTimerPayload.STREAM_CODEC);

        GameEvents.register();

        CreativeSpawners.LOGGER.info("Creative Spawners (Fabric) initialized");
    }
}
