package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.Set;
import java.util.stream.IntStream;

public class ModCreativeTabs {
    // Items that are one registered item but have many component-driven variants;
    // handled explicitly below so we get a spawner per variant.
    private static final Set<Item> VARIANT_ITEMS = Set.of(
            Items.ENCHANTED_BOOK, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION,
            Items.TIPPED_ARROW, Items.GOAT_HORN, Items.OMINOUS_BOTTLE);

    public static final CreativeModeTab SPAWNERS_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            CreativeSpawners.id("spawners"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.creative_spawners"))
                    .icon(() -> new ItemStack(ModItems.EMPTY_SPAWNER))
                    .displayItems((params, output) -> {
                        HolderLookup.Provider registries = params.holders();

                        output.accept(new ItemStack(ModItems.EMPTY_SPAWNER));
                        output.accept(new ItemStack(ModItems.CREATIVE_SPAWNER));
                        output.accept(new ItemStack(ModItems.CREATIVE_INVENTORY));

                        // Plain items (one spawner per item id).
                        for (Item item : BuiltInRegistries.ITEM) {
                            if (VARIANT_ITEMS.contains(item)) continue;
                            output.accept(ItemSpawnerBlockItem.createSpawnerFor(item));
                        }

                        // Enchanted books: one spawner per enchantment + level.
                        registries.lookup(Registries.ENCHANTMENT).ifPresent(enchantments ->
                                enchantments.listElements().forEach(enchantment ->
                                        enchantment.unwrapKey().ifPresent(key ->
                                                IntStream.rangeClosed(enchantment.value().getMinLevel(), enchantment.value().getMaxLevel())
                                                        .forEach(level -> output.accept(
                                                                ItemSpawnerBlockItem.createBookSpawner(enchantment, key.location(), level))))));

                        // Potions, splash, lingering, tipped arrows: one spawner per potion type.
                        registries.lookup(Registries.POTION).ifPresent(potions ->
                                potions.listElements().forEach(potion -> {
                                    addVariant(output, registries, PotionContents.createItemStack(Items.POTION, potion));
                                    addVariant(output, registries, PotionContents.createItemStack(Items.SPLASH_POTION, potion));
                                    addVariant(output, registries, PotionContents.createItemStack(Items.LINGERING_POTION, potion));
                                    addVariant(output, registries, PotionContents.createItemStack(Items.TIPPED_ARROW, potion));
                                }));

                        // Goat horns: one spawner per instrument.
                        registries.lookup(Registries.INSTRUMENT).ifPresent(instruments ->
                                instruments.get(InstrumentTags.GOAT_HORNS).ifPresent(tagged ->
                                        tagged.forEach(instrument -> {
                                            ItemStack horn = new ItemStack(Items.GOAT_HORN);
                                            horn.set(DataComponents.INSTRUMENT, instrument);
                                            addVariant(output, registries, horn);
                                        })));

                        // Ominous bottles: one spawner per amplifier (0-4).
                        for (int amplifier = 0; amplifier <= 4; amplifier++) {
                            ItemStack bottle = new ItemStack(Items.OMINOUS_BOTTLE);
                            bottle.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, amplifier);
                            addVariant(output, registries, bottle);
                        }
                    })
                    .build());

    private static void addVariant(CreativeModeTab.Output output, HolderLookup.Provider registries, ItemStack variant) {
        String name = variant.getHoverName().getString();
        output.accept(ItemSpawnerBlockItem.createVariantSpawner(variant, name, registries));
    }

    public static void init() {
    }
}
