package com.narezany.backpack;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

/** Окно рюкзака. Рисуется без текстур: серая панель и рамки слотов. */
public class GuiBackpack extends GuiContainer {

    private static final int PANEL = 0xFFC6C6C6;
    private static final int PANEL_LIGHT = 0xFFFFFFFF;
    private static final int PANEL_DARK = 0xFF555555;
    private static final int SLOT_EDGE = 0xFF373737;
    private static final int SLOT_IN = 0xFF8B8B8B;

    private final ContainerBackpack container;

    public GuiBackpack(ContainerBackpack container) {
        super(container);
        this.container = container;
        this.xSize = 176;
        this.ySize = 114 + 18 * container.getRows();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(container.getTitle(), 8, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"),
                8, ySize - 94, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, PANEL);
        drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + 1, PANEL_LIGHT);
        drawRect(guiLeft, guiTop, guiLeft + 1, guiTop + ySize, PANEL_LIGHT);
        drawRect(guiLeft + xSize - 1, guiTop, guiLeft + xSize, guiTop + ySize, PANEL_DARK);
        drawRect(guiLeft, guiTop + ySize - 1, guiLeft + xSize, guiTop + ySize, PANEL_DARK);

        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
            int x = guiLeft + slot.xDisplayPosition;
            int y = guiTop + slot.yDisplayPosition;
            drawRect(x - 1, y - 1, x + 17, y + 17, SLOT_EDGE);
            drawRect(x, y, x + 16, y + 16, SLOT_IN);
        }
    }
}
