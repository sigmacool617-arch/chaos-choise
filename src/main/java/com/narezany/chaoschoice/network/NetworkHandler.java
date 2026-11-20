package com.narezany.chaoschoice.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

/** Канал связи: один на весь мод. */
public final class NetworkHandler {

    public static SimpleNetworkWrapper NET;

    public static void init() {
        if (NET != null) {
            return;
        }
        NET = NetworkRegistry.INSTANCE.newSimpleChannel("chaoschoice");
        NET.registerMessage(PacketVote.Handler.class, PacketVote.class, 0, Side.SERVER);
        NET.registerMessage(PacketWave.Handler.class, PacketWave.class, 1, Side.CLIENT);
        NET.registerMessage(PacketOpen.Handler.class, PacketOpen.class, 2, Side.CLIENT);
    }

    public static void sendWave(EntityPlayerMP player, int state, int seconds, String a, String b) {
        if (NET != null && player != null) {
            NET.sendTo(new PacketWave(state, seconds, a, b), player);
        }
    }

    public static void sendOpen(EntityPlayerMP player, String nameA, String nameB, int seconds) {
        if (NET != null && player != null) {
            NET.sendTo(new PacketOpen(nameA, nameB, seconds), player);
        }
    }

    public static void sendVote(int index) {
        if (NET != null) {
            NET.sendToServer(new PacketVote(index));
        }
    }

    private NetworkHandler() {
    }
}
