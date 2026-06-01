package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemSpawnerRenderer implements BlockEntityRenderer<ItemSpawnerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ItemSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ItemSpawnerBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        ItemStack stack = be.getSpawnedStack();
        if (stack.isEmpty() || be.getLevel() == null) return;

        float spin = (be.getLevel().getGameTime() + partialTick) * 2.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.scale(0.75F, 0.75F, 0.75F);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, be.getLevel(), 0);
        poseStack.popPose();
    }
}
