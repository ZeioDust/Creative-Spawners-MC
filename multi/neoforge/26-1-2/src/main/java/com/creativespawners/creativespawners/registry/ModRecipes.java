package com.creativespawners.creativespawners.registry;

import com.creativespawners.creativespawners.CreativeSpawners;
import com.creativespawners.creativespawners.recipe.ItemSpawnerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreativeSpawners.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ItemSpawnerRecipe>> ITEM_SPAWNER_RECIPE =
            SERIALIZERS.register("item_spawner", () -> ItemSpawnerRecipe.SERIALIZER);
}
