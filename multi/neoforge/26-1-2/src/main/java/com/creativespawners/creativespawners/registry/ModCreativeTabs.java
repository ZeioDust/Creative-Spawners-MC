package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreativeSpawners.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPAWNERS_TAB = TABS.register("spawners",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.creative_spawners"))
                    .icon(() -> new ItemStack(ModItems.EMPTY_SPAWNER.get()))
                    .displayItems((params, output) -> {
                        output.accept(new ItemStack(ModItems.EMPTY_SPAWNER.get()));
                        output.accept(new ItemStack(ModItems.CREATIVE_SPAWNER.get()));
                        output.accept(new ItemStack(ModItems.CREATIVE_INVENTORY.get()));

                        for (Item item : BuiltInRegistries.ITEM) {
                            output.accept(ItemSpawnerBlockItem.createSpawnerFor(item));
                        }
                    })
                    .build());
}
