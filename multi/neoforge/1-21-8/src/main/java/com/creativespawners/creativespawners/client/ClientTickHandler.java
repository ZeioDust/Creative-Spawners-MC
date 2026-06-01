package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.CreativeSpawners;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CreativeSpawners.MODID, value = Dist.CLIENT)
public class ClientTickHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        CreativeTimerOverlay.tick();
    }
}
