package com.creativespawners.creativespawners.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class CreativeTimerOverlay {
    private static int ticksRemaining = 0;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int RED = 0xFFFF5555;

    public static void start(int ticks) {
        ticksRemaining = ticks;
    }

    public static void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (ticksRemaining <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int seconds = (ticksRemaining + 19) / 20;

        int numberColor = switch (seconds) {
            case 5, 4 -> WHITE;
            case 3, 2 -> YELLOW;
            default -> RED;
        };

        String label = "Creative: ";
        String number = Integer.toString(seconds);
        int labelWidth = font.width(label);
        int totalWidth = labelWidth + font.width(number);

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int startX = (screenWidth - totalWidth) / 2;
        int y = screenHeight - 39 - 5 - 9;

        graphics.drawString(font, label, startX, y, WHITE, true);
        graphics.drawString(font, number, startX + labelWidth, y, numberColor, true);
    }
}
