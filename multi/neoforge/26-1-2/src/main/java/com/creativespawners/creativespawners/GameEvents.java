package com.creativespawners.creativespawners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameEvents {
    private static final Map<UUID, RevertEntry> pendingReverts = new ConcurrentHashMap<>();

    public static void scheduleRevert(ServerPlayer player, GameType previousMode, int ticks) {
        pendingReverts.put(player.getUUID(), new RevertEntry(previousMode, ticks));
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        Iterator<Map.Entry<UUID, RevertEntry>> it = pendingReverts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, RevertEntry> entry = it.next();
            RevertEntry revert = entry.getValue();
            revert.ticksLeft--;

            if (revert.ticksLeft <= 0) {
                it.remove();
                ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    player.setGameMode(revert.previousMode);
                }
            }
        }
    }

    private static class RevertEntry {
        final GameType previousMode;
        int ticksLeft;

        RevertEntry(GameType previousMode, int ticksLeft) {
            this.previousMode = previousMode;
            this.ticksLeft = ticksLeft;
        }
    }
}
