package com.creativespawners.creativespawners.client;

import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import com.creativespawners.creativespawners.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class ItemSpawnerSpecialRenderer implements SpecialModelRenderer<ItemStack> {

    @Override
    public @Nullable ItemStack extractArgument(ItemStack stack) {
        var level = Minecraft.getInstance().level;
        if (level == null) return ItemStack.EMPTY;
        return ItemSpawnerBlockItem.getStoredStack(stack, level.registryAccess());
    }

    @Override
    public void submit(@Nullable ItemStack argument, PoseStack poseStack, SubmitNodeCollector collector,
                       int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();

        // Global offset that moves the WHOLE assembly (cage + inner) together.
        // The inner stays centered in the cage because both share this offset.
        float gx = 0.25F, gy = 0.25F, gz = 0.25F;
        poseStack.pushPose();
        poseStack.translate(gx, gy, gz);

        // Cage - model renders in [0,1]
        ItemStackRenderState cageState = new ItemStackRenderState();
        resolver.updateForTopItem(cageState, new ItemStack(ModItems.EMPTY_SPAWNER.get()),
                ItemDisplayContext.NONE, null, null, 0);
        poseStack.pushPose();
        poseStack.translate(0.25F, 0.25F, 0.25F);
        cageState.submit(poseStack, collector, lightCoords, overlayCoords, outlineColor);
        poseStack.popPose();

        // Stored item - half size, centered inside the cage
        if (argument != null && !argument.isEmpty()) {
            ItemStackRenderState innerState = new ItemStackRenderState();
            resolver.updateForTopItem(innerState, argument, ItemDisplayContext.NONE, null, null, 0);
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0.5F, 0.5F, 0.5F);
            innerState.submit(poseStack, collector, lightCoords, overlayCoords, outlineColor);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.5F, -0.5F, -0.5F));
        output.accept(new Vector3f(0.5F, 0.5F, 0.5F));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<ItemStack> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @Nullable SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context) {
            return new ItemSpawnerSpecialRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
