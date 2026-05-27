package com.creativespawners.creativespawners.item;

import com.creativespawners.creativespawners.GameEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            GameType currentMode = serverPlayer.gameMode.getGameModeForPlayer();
            if (currentMode == GameType.CREATIVE) {
                return InteractionResult.PASS;
            }

            serverPlayer.setGameMode(GameType.CREATIVE);
            GameEvents.scheduleRevert(serverPlayer, currentMode, 100); // 5 seconds

            ItemStack held = player.getItemInHand(hand);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
