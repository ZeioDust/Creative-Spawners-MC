package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import com.creativespawners.creativespawners.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ItemSpawnerSpecialRenderer implements SpecialModelRenderer<ItemStack> {

    @Override
    public @Nullable ItemStack extractArgument(ItemStack stack) {
        var level = Minecraft.getInstance().level;
        if (level == null) return ItemStack.EMPTY;
        return ItemSpawnerBlockItem.getStoredStack(stack, level.registryAccess());
    }

    @Override
    public void render(@Nullable ItemStack argument, ItemDisplayContext displayContext, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay, boolean hasFoil) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer ir = mc.getItemRenderer();
        var level = mc.level;

        // Global offset that moves the whole assembly together.
        poseStack.pushPose();
        poseStack.translate(0.25F, 0.25F, 0.25F);

        // Cage
        poseStack.pushPose();
        poseStack.translate(0.25F, 0.25F, 0.25F);
        ir.renderStatic(new ItemStack(ModItems.EMPTY_SPAWNER.get()), ItemDisplayContext.NONE,
                light, overlay, poseStack, buffers, level, 0);
        poseStack.popPose();

        // Stored item, centered and scaled down inside the cage
        if (argument != null && !argument.isEmpty()) {
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.5F, 0.5F, 0.5F);
            ir.renderStatic(argument, ItemDisplayContext.NONE, light, overlay, poseStack, buffers, level, 0);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> output) {
        output.add(new Vector3f(0.0F, 0.0F, 0.0F));
        output.add(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new ItemSpawnerSpecialRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
