package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.recipe.ItemSpawnerRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ModRecipes {
    public static final RecipeSerializer<ItemSpawnerRecipe> ITEM_SPAWNER =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CreativeSpawners.id("item_spawner"),
                    new SimpleCraftingRecipeSerializer<>(ItemSpawnerRecipe::new));

    public static void init() {
    }
}
