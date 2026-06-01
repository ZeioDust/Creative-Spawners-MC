package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import com.creativespawners.creativespawners.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemSpawnerDynamicRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer ir = mc.getItemRenderer();
        var level = mc.level;
        var registries = level != null ? level.registryAccess() : null;

        poseStack.pushPose();
        poseStack.translate(0.25F, 0.25F, 0.25F);

        // Cage
        poseStack.pushPose();
        poseStack.translate(0.25F, 0.25F, 0.25F);
        ir.renderStatic(new ItemStack(ModItems.EMPTY_SPAWNER), ItemDisplayContext.NONE,
                light, overlay, poseStack, buffers, level, 0);
        poseStack.popPose();

        // Stored item, centered and scaled down inside the cage
        if (registries != null) {
            ItemStack inner = ItemSpawnerBlockItem.getStoredStack(stack, registries);
            if (!inner.isEmpty()) {
                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.5F, 0.5F, 0.5F);
                ir.renderStatic(inner, ItemDisplayContext.NONE, light, overlay, poseStack, buffers, level, 0);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}
