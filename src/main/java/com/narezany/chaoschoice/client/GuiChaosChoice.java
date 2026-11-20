package com.narezany.chaoschoice.client;

import com.narezany.chaoschoice.network.NetworkHandler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/** Меню выбора: две кнопки и таймер. */
public class GuiChaosChoice extends GuiScreen {

    private final String nameA;
    private final String nameB;

    public GuiChaosChoice(String nameA, String nameB) {
        this.nameA = nameA == null ? "" : nameA;
        this.nameB = nameB == null ? "" : nameB;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int cx = width / 2;
        int cy = height / 2;
        buttonList.add(new GuiButton(0, cx - 150, cy - 24, 300, 20, "A) " + trim(nameA)));
        buttonList.add(new GuiButton(1, cx - 150, cy + 2, 300, 20, "B) " + trim(nameB)));
        buttonList.add(new GuiButton(2, cx - 70, cy + 36, 140, 20, "Позже (Esc)"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0 || button.id == 1) {
            ClientState.menuOpen = false;
            NetworkHandler.sendVote(button.id);
            mc.displayGuiScreen(null);
        } else if (button.id == 2) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int cx = width / 2;
        drawCenteredString(fontRendererObj, "§6Что выбираешь?", cx, height / 2 - 72, 0xFFAA00);
        drawCenteredString(fontRendererObj, "§fОсталось " + ClientState.seconds + "с", cx,
                height / 2 - 56, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, "§8Кто первый нажал — тот и решил. Не успеешь — решит судьба.",
                cx, height / 2 + 64, 0x909090);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String trim(String text) {
        if (text.length() <= 58) {
            return text;
        }
        return text.substring(0, 55) + "...";
    }
}
