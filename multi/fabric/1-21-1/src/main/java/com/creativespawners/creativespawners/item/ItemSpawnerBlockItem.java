package com.creativespawners.creativespawners.item;

import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import com.creativespawners.creativespawners.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

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

    public static ItemStack getStoredStack(ItemStack spawnerStack, HolderLookup.Provider registries) {
        CustomData customData = spawnerStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return ItemStack.EMPTY;
        CompoundTag tag = customData.copyTag();

        // Enchanted book stored as enchantment id + level -> reconstruct the book
        if (tag.contains("BookEnch")) {
            ResourceLocation enchId = ResourceLocation.tryParse(tag.getString("BookEnch"));
            int level = tag.getInt("BookLvl");
            if (enchId != null) {
                var lookup = registries.lookup(Registries.ENCHANTMENT);
                if (lookup.isPresent()) {
                    var holder = lookup.get().get(ResourceKey.create(Registries.ENCHANTMENT, enchId));
                    if (holder.isPresent()) {
                        return makeBook(holder.get(), level);
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
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("SpawnedItemId"));
            if (id != null) {
                Item found = BuiltInRegistries.ITEM.get(id);
                if (found != null && found != Items.AIR) {
                    return new ItemStack(found);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack makeBook(Holder<Enchantment> enchantment, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(enchantment, level);
        book.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return book;
    }

    /** Items whose display name is shared across many ids (variant only shown in lore). */
    public static boolean hasSharedDisplayName(ResourceLocation id) {
        String p = id.getPath();
        return p.endsWith("_smithing_template") || p.startsWith("music_disc") || p.endsWith("_banner_pattern");
    }

    /** Title-cases a registry path, e.g. "netherite_upgrade_smithing_template" -> "Netherite Upgrade Smithing Template". */
    public static String prettify(String path) {
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    public static ItemStack createSpawnerFor(Item item) {
        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER);
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        if (key != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("SpawnedItemId", key.toString());
            // Items that all share one display name get a unique name derived from their id.
            if (hasSharedDisplayName(key)) {
                tag.putString("SpawnedName", prettify(key.getPath()));
            }
            result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return result;
    }

    /** A spawner for a full component-variant stack (potion, tipped arrow, goat horn, ...). */
    public static ItemStack createVariantSpawner(ItemStack variant, String displayName, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER);
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        Tag encoded = ItemStack.CODEC.encodeStart(ops, variant.copyWithCount(1)).getOrThrow();
        CompoundTag tag = new CompoundTag();
        tag.put("SpawnedStack", encoded);
        tag.putString("SpawnedName", displayName);
        result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }

    public static ItemStack createBookSpawner(Holder<Enchantment> enchantment, ResourceLocation enchantmentId, int level) {
        ItemStack result = new ItemStack(ModItems.ITEM_SPAWNER);
        CompoundTag tag = new CompoundTag();
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
            if (tag.contains("BookEnch")) {
                ResourceLocation id = ResourceLocation.tryParse(tag.getString("BookEnch"));
                int level = tag.getInt("BookLvl");
                if (id != null) {
                    MutableComponent ench = Component.translatable("enchantment." + id.getNamespace() + "." + id.getPath());
                    ench.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
                    return Component.translatable("name.creative_spawners.spawner", ench);
                }
            }
            if (tag.contains("SpawnedName")) {
                return Component.translatable("name.creative_spawners.spawner", Component.literal(tag.getString("SpawnedName")));
            }
            if (tag.contains("SpawnedItemId")) {
                ResourceLocation id = ResourceLocation.tryParse(tag.getString("SpawnedItemId"));
                if (id != null) {
                    Item found = BuiltInRegistries.ITEM.get(id);
                    if (found != null && found != Items.AIR) {
                        return Component.translatable("name.creative_spawners.spawner", new ItemStack(found).getHoverName());
                    }
                }
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (context.registries() != null) {
            ItemStack stored = getStoredStack(stack, context.registries());
            if (!stored.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.creative_spawners.spawns").append(stored.getHoverName()));
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
