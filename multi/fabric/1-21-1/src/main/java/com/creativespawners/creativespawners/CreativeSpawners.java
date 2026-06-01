package com.creativespawners.creativespawners;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreativeSpawners {
    public static final String MODID = "creative_spawners";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
