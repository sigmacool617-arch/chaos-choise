package com.narezany.fastbuild;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.WorldServer;

/** Разбор кликов и порционное заполнение — в тиках сервера. */
public class ServerTicker {

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        SelectionStore.processQueue();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.world instanceof WorldServer) {
            FillJobs.tickWorld(event.world);
        }
    }
}
