package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.stream.IntStream;

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

                        // A spawner for every item, except the generic enchanted book
                        // (handled below as one spawner per enchantment + level).
                        for (Item item : BuiltInRegistries.ITEM) {
                            if (item == Items.ENCHANTED_BOOK) continue;
                            output.accept(ItemSpawnerBlockItem.createSpawnerFor(item));
                        }

                        // One spawner per enchanted book variant (Unbreaking I, Mending, Fortune III, ...)
                        params.holders().lookup(Registries.ENCHANTMENT).ifPresent(enchantments ->
                                enchantments.listElements().forEach(enchantment ->
                                        enchantment.unwrapKey().ifPresent(key ->
                                                IntStream.rangeClosed(enchantment.value().getMinLevel(), enchantment.value().getMaxLevel())
                                                        .forEach(level -> {
                                                            ItemStack book = EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, level));
                                                            output.accept(ItemSpawnerBlockItem.createBookSpawner(book, key.location(), level, params.holders()));
                                                        }))));
                    })
                    .build());
}
