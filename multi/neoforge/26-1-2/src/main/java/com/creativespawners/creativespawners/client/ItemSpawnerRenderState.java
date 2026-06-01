package com.creativespawners.creativespawners.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
public class ItemSpawnerRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState itemState = new ItemStackRenderState();
    public boolean hasItem = false;
    public float spin = 0;
}
