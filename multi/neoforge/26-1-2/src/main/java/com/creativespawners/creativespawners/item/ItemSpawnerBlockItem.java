package com.creativespawners.creativespawners.item;

import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import com.creativespawners.creativespawners.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class ItemSpawnerBlockItem extends BlockItem {
    public ItemSpawnerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ItemSpawnerBlockEntity spawner) {
                ItemStack stored = getStoredStack(stack, level.registryAccess());
                if (!stored.isEmpty()) {
                    spawner.setSpawnedStack(stored);
                }
            }
        }
        return result;
    }

    /** Reads the stored stack from the spawner item, supporting both the full-stack and item-id formats. */
    public static ItemStack getStoredStack(ItemStack spawnerStack, HolderLookup.Provider registries) {
        CustomData customData = spawnerStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return ItemStack.EMPTY;
        CompoundTag tag = customData.copyTag();

        // Enchanted book stored as enchantment id + level -> reconstruct the book
        if (tag.contains("BookEnch")) {
            Identifier enchId = Identifier.tryParse(tag.getStringOr("BookEnch", ""));
            int level = tag.getIntOr("BookLvl", 1);
            if (enchId != null) {
                var lookup = registries.lookup(net.minecraft.core.registries.Registries.ENCHANTMENT);
                if (lookup.isPresent()) {
                    var holder = lookup.get().get(net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.ENCHANTMENT, enchId));
                    if (holder.isPresent()) {
                        return net.minecraft.world.item.enchantment.EnchantmentHelper.createBook(
                                new net.minecraft.world.item.enchantment.EnchantmentInstance(holder.get(), level));
                    }
                }
            }
        }

        if (tag.contains("SpawnedStack")) {
            RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
            Tag stackTag = tag.get("SpawnedStack");
            if (stackTag != null) {
                return ItemStack.CODEC.parse(ops, stackTag).result().orElse(ItemStack.EMPTY);
            }
        }
        if (tag.contains("SpawnedItemId")) {
            Identifier id = Identifier.tryParse(tag.getStringOr("SpawnedItemId", ""));
            if (id != null) {
                Item found = BuiltInRegistries.ITEM.getValue(id);
                if (found != null && found != Items.AIR) {
                    return new ItemStack(found);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /** Builds a spawner item that stores a plain item by id (no extra components). */
    public static ItemStack createSpawnerFor(Item item) {
        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER.get());
        var key = BuiltInRegistries.ITEM.getKey(item);
        if (key != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("SpawnedItemId", key.toString());
            result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return result;
    }

    /** Builds a spawner item that stores a full stack (with components, e.g. enchanted books). */
    public static ItemStack createSpawnerForStack(ItemStack stack, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER.get());
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        Tag encoded = ItemStack.CODEC.encodeStart(ops, stack.copyWithCount(1)).getOrThrow();
        CompoundTag tag = new CompoundTag();
        tag.put("SpawnedStack", encoded);
        result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }

    /** Builds a spawner for a specific enchanted book, tagged so it can show a name like "Unbreaking II Book". */
    public static ItemStack createBookSpawner(ItemStack book, Identifier enchantmentId, int level, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER.get());
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        Tag encoded = ItemStack.CODEC.encodeStart(ops, book.copyWithCount(1)).getOrThrow();
        CompoundTag tag = new CompoundTag();
        tag.put("SpawnedStack", encoded);
        tag.putString("BookEnch", enchantmentId.toString());
        tag.putInt("BookLvl", level);
        result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }

    @Override
    public Component getName(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();

            // Enchanted book spawner -> e.g. "Unbreaking II Book"
            if (tag.contains("BookEnch")) {
                Identifier id = Identifier.tryParse(tag.getStringOr("BookEnch", ""));
                int level = tag.getIntOr("BookLvl", 1);
                if (id != null) {
                    MutableComponent enchantment = Component.translatable("enchantment." + id.getNamespace() + "." + id.getPath());
                    enchantment.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
                    return Component.translatable("name.creative_spawners.spawner", enchantment);
                }
            }

            // Plain item spawner -> e.g. "Grass Block Spawner"
            if (tag.contains("SpawnedItemId")) {
                Identifier id = Identifier.tryParse(tag.getStringOr("SpawnedItemId", ""));
                if (id != null) {
                    Item found = BuiltInRegistries.ITEM.getValue(id);
                    if (found != null && found != Items.AIR) {
                        return Component.translatable("name.creative_spawners.spawner", new ItemStack(found).getHoverName());
                    }
                }
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        HolderLookup.Provider registries = context.registries();
        if (registries != null) {
            ItemStack stored = getStoredStack(stack, registries);
            if (!stored.isEmpty()) {
                tooltip.accept(Component.translatable("tooltip.creative_spawners.spawns")
                        .append(stored.getHoverName()));
            }
        }
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
