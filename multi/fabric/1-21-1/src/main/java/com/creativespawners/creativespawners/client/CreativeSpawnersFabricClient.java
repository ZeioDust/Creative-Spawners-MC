package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.network.CreativeTimerPayload;
import com.creativespawners.creativespawners.registry.ModBlockEntities;
import com.creativespawners.creativespawners.registry.ModBlocks;
import com.creativespawners.creativespawners.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.RenderType;

public class CreativeSpawnersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.ITEM_SPAWNER, ItemSpawnerRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EMPTY_SPAWNER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ITEM_SPAWNER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CREATIVE_SPAWNER, RenderType.cutout());

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ITEM_SPAWNER, new ItemSpawnerDynamicRenderer());

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> CreativeTimerOverlay.render(graphics, tickCounter));
        ClientTickEvents.END_CLIENT_TICK.register(client -> CreativeTimerOverlay.tick());

        ClientPlayNetworking.registerGlobalReceiver(CreativeTimerPayload.TYPE, (payload, context) ->
                context.client().execute(() -> CreativeTimerOverlay.start(payload.ticks())));
    }
}
