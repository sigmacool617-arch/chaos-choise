package com.narezany.chaoschoice;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.WorldServer;

/** Два тика: серверный таймер выбора и порционная правка мира по мирам. */
public class ServerTicker {

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        ChoiceManager.serverTick();
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.world instanceof WorldServer) {
            BlockJobs.tickWorld(event.world);
        }
    }
}
