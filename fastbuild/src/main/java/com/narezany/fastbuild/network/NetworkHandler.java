package com.narezany.fastbuild.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/** Канал мода: клиент сообщает серверу координаты клика. */
public final class NetworkHandler {

    public static SimpleNetworkWrapper NET;

    public static void init() {
        if (NET != null) {
            return;
        }
        NET = NetworkRegistry.INSTANCE.newSimpleChannel("fastbuild");
        NET.registerMessage(PacketSelect.Handler.class, PacketSelect.class, 0, Side.SERVER);
    }

    public static void sendSelect(int x, int y, int z) {
        if (NET != null) {
            NET.sendToServer(new PacketSelect(x, y, z));
        }
    }

    private NetworkHandler() {
    }
}
