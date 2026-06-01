package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ItemSpawnerRenderer implements BlockEntityRenderer<ItemSpawnerBlockEntity, ItemSpawnerRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ItemSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ItemSpawnerRenderState createRenderState() {
        return new ItemSpawnerRenderState();
    }

    @Override
    public void extractRenderState(
            ItemSpawnerBlockEntity blockEntity,
            ItemSpawnerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        ItemStack stack = blockEntity.getSpawnedStack();
        state.hasItem = !stack.isEmpty();
        if (state.hasItem) {
            long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
            state.spin = (gameTime + partialTicks) * 2.0F;
            itemModelResolver.updateForTopItem(
                    state.itemState,
                    stack,
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(),
                    null,
                    0
            );
        }
    }

    @Override
    public void submit(ItemSpawnerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.hasItem) return;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
        poseStack.scale(0.75F, 0.75F, 0.75F);
        state.itemState.submit(poseStack, collector, state.lightCoords, 0xA0000, 0);
        poseStack.popPose();
    }
}
