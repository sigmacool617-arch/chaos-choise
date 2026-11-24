package com.narezany.drill;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.WorldServer;

/** Отсчёт бурения и сам 3×3 — в тиках сервера, а не в сетевом потоке. */
public class ServerTicker {

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.world instanceof WorldServer) {
            DrillJobs.tickWorld(event.world);
        }
    }
}
