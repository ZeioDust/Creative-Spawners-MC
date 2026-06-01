package com.creativespawners.creativespawners.recipe;

import com.creativespawners.creativespawners.item.ItemSpawnerBlockItem;
import com.creativespawners.creativespawners.registry.ModItems;
import com.creativespawners.creativespawners.registry.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class ItemSpawnerRecipe extends CustomRecipe {
    public ItemSpawnerRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 2) return false;

        boolean hasEmptySpawner = false;
        boolean hasOtherItem = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.EMPTY_SPAWNER)) {
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack ingredient = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && !stack.is(ModItems.EMPTY_SPAWNER)) {
                ingredient = stack;
                break;
            }
        }

        if (ingredient.isEmpty()) return ItemStack.EMPTY;

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(ingredient.getItem());
        if (key == null) return new ItemStack(ModItems.ITEM_SPAWNER);

        // Enchanted books -> store enchantment id + level (dynamic localized name).
        ItemEnchantments enchantments = ingredient.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments != null && !enchantments.isEmpty()) {
            Holder<Enchantment> enchantment = enchantments.keySet().iterator().next();
            ResourceLocation id = enchantment.unwrapKey().map(k -> k.location()).orElse(null);
            if (id != null) {
                return ItemSpawnerBlockItem.createBookSpawner(enchantment, id, enchantments.getLevel(enchantment));
            }
        }

        // Other component-variant items (potions, tipped arrows, goat horns, ...) -> store the full stack.
        if (!ingredient.getComponentsPatch().isEmpty()) {
            return ItemSpawnerBlockItem.createVariantSpawner(ingredient, ingredient.getHoverName().getString(), registries);
        }

        // Plain item.
        return ItemSpawnerBlockItem.createSpawnerFor(ingredient.getItem());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ITEM_SPAWNER;
    }
}
