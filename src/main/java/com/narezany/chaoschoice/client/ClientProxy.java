package com.narezany.chaoschoice.client;

import com.narezany.chaoschoice.CommonProxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

/**
 * Клиентская половина: полоска внизу и меню выбора.
 * Пакеты только записывают состояние, а экран открывается в клиентском тике —
 * из сетевого потока открывать GUI нельзя.
 */
public class ClientProxy extends CommonProxy {

    private final ChaosHud hud = new ChaosHud();

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(hud);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void onWave(int state, int seconds, String shortA, String shortB) {
        ClientState.state = state;
        ClientState.seconds = seconds;
        ClientState.shortA = shortA == null ? "" : shortA;
        ClientState.shortB = shortB == null ? "" : shortB;

        if (state == 0) {
            ClientState.menuOpen = false;
            ClientState.closeRequested = true;
            return;
        }
        ClientState.menuOpen = true;
        if (ClientState.reopen > 20) {
            ClientState.reopen = 20;
        }
    }

    @Override
    public void onOpen(String nameA, String nameB, int seconds) {
        ClientState.nameA = nameA == null ? "" : nameA;
        ClientState.nameB = nameB == null ? "" : nameB;
        ClientState.seconds = seconds;
        ClientState.menuOpen = true;
        ClientState.closeRequested = false;
        ClientState.reopen = 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }

        if (ClientState.closeRequested) {
            ClientState.closeRequested = false;
            if (mc.currentScreen instanceof GuiChaosChoice) {
                mc.displayGuiScreen(null);
            }
        }

        if (ClientState.state != 1 || !ClientState.menuOpen || ClientState.seconds <= 0) {
            return;
        }
        if (mc.currentScreen != null) {
            ClientState.reopen = 100;
            return;
        }
        if (ClientState.reopen > 0) {
            ClientState.reopen--;
        }
        if (ClientState.reopen <= 0) {
            ClientState.reopen = 160;
            mc.displayGuiScreen(new GuiChaosChoice(ClientState.nameA, ClientState.nameB));
        }
    }
}
