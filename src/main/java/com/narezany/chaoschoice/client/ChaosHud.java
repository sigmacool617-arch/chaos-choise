package com.narezany.chaoschoice.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Отсчёт и подсказки прямо над шкалой опыта. */
public class ChaosHud extends Gui {

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.gameSettings.hideGUI) {
            return;
        }
        if (mc.currentScreen != null) {
            return;
        }

        ScaledResolution res = event.resolution;
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        int y = height - 58;

        if (ClientState.state == 1) {
            String line = "§6ВЫБОР §f" + ClientState.seconds + "с §8| §aA §7" + ClientState.shortA
                    + " §8| §bB §7" + ClientState.shortB;
            drawLine(mc, line, width, y, 0xFFFFFF);
        } else if (ClientState.seconds > 0 && ClientState.seconds <= 10) {
            String line = "§8Следующая волна: §7" + ClientState.seconds + "с";
            drawLine(mc, line, width, y, 0xAAAAAA);
        }
    }

    private void drawLine(Minecraft mc, String line, int width, int y, int color) {
        int textWidth = mc.fontRenderer.getStringWidth(line);
        int x = (width - textWidth) / 2;
        drawRect(x - 4, y - 3, x + textWidth + 4, y + 10, 0x90000000);
        mc.fontRenderer.drawStringWithShadow(line, x, y, color);
    }
}
