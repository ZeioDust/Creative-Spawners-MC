package com.creativespawners.creativespawners.item;

import com.creativespawners.creativespawners.block.entity.ItemSpawnerBlockEntity;
import com.creativespawners.creativespawners.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
                Item item = getStoredItem(stack);
                if (item != Items.AIR) {
                    spawner.setSpawnedItem(item);
                }
            }
        }
        return result;
    }

    public static Item getStoredItem(ItemStack spawnerStack) {
        CustomData customData = spawnerStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("SpawnedItemId")) {
                var id = Identifier.tryParse(tag.getStringOr("SpawnedItemId", ""));
                if (id != null) {
                    Item found = BuiltInRegistries.ITEM.getValue(id);
                    if (found != null) return found;
                }
            }
        }
        return Items.AIR;
    }

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

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Item stored = getStoredItem(stack);
        if (stored != Items.AIR) {
            tooltip.accept(Component.translatable("tooltip.creative_spawners.spawns")
                    .append(new ItemStack(stored).getHoverName()));
        }
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
