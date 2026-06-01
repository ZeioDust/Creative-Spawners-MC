package com.creativespawners.creativespawners.item;

import com.creativespawners.creativespawners.GameEvents;
import com.creativespawners.creativespawners.network.CreativeTimerPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class CreativeInventoryItem extends Item {
    public CreativeInventoryItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            GameType currentMode = serverPlayer.gameMode.getGameModeForPlayer();
            if (currentMode == GameType.CREATIVE) {
                return InteractionResultHolder.pass(held);
            }

            held.shrink(1);
            serverPlayer.setGameMode(GameType.CREATIVE);
            GameEvents.scheduleRevert(serverPlayer, currentMode, 100);
            ServerPlayNetworking.send(serverPlayer, new CreativeTimerPayload(100));
        }
        return InteractionResultHolder.success(held);
    }
}
