package com.creativespawners.creativespawners.recipe;

import com.creativespawners.creativespawners.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class ItemSpawnerRecipe extends CustomRecipe {
    public static final MapCodec<ItemSpawnerRecipe> MAP_CODEC = MapCodec.unit(ItemSpawnerRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSpawnerRecipe> STREAM_CODEC =
            StreamCodec.unit(new ItemSpawnerRecipe());
    public static final RecipeSerializer<ItemSpawnerRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public ItemSpawnerRecipe() {
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 2) return false;

        boolean hasEmptySpawner = false;
        boolean hasOtherItem = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.EMPTY_SPAWNER.get())) {
                if (hasEmptySpawner) return false;
                hasEmptySpawner = true;
            } else {
                if (hasOtherItem) return false;
                hasOtherItem = true;
            }
        }

        return hasEmptySpawner && hasOtherItem;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack ingredient = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && !stack.is(ModItems.EMPTY_SPAWNER.get())) {
                ingredient = stack;
                break;
            }
        }

        if (ingredient.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER.get());
        var key = BuiltInRegistries.ITEM.getKey(ingredient.getItem());
        if (key == null) return result;

        CompoundTag tag = new CompoundTag();
        tag.putString("SpawnedItemId", key.toString());

        // If it's an enchanted book, tag the stored enchantment so the spawner can
        // reconstruct it and show a name like "Impaling V Spawner".
        ItemEnchantments enchantments = ingredient.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments != null && !enchantments.isEmpty()) {
            Holder<Enchantment> enchantment = enchantments.keySet().iterator().next();
            Identifier id = enchantment.unwrapKey().map(k -> k.identifier()).orElse(null);
            if (id != null) {
                tag.putString("BookEnch", id.toString());
                tag.putInt("BookLvl", enchantments.getLevel(enchantment));
            }
        }

        result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }

    @Override
    public RecipeSerializer<ItemSpawnerRecipe> getSerializer() {
        return SERIALIZER;
    }
}
