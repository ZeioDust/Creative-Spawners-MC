package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = CreativeSpawners.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ITEM_SPAWNER.get(), ItemSpawnerRenderer::new);
    }

    @SubscribeEvent
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(CreativeSpawners.MODID, "item_spawner_inner"),
                ItemSpawnerSpecialRenderer.Unbaked.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.CROSSHAIR,
                Identifier.fromNamespaceAndPath(CreativeSpawners.MODID, "creative_timer"),
                new CreativeTimerOverlay()
        );
    }
}
