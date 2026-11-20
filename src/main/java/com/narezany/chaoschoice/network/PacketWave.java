package com.narezany.chaoschoice.network;

import java.nio.charset.Charset;

import com.narezany.chaoschoice.ChaosChoice;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** Сервер → клиент: состояние волны для полоски внизу. */
public class PacketWave implements IMessage {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    /** 0 — до волны N секунд, 1 — идёт выбор. */
    public int state;
    public int seconds;
    public String shortA = "";
    public String shortB = "";

    public PacketWave() {
    }

    public PacketWave(int state, int seconds, String shortA, String shortB) {
        this.state = state;
        this.seconds = seconds;
        this.shortA = shortA == null ? "" : shortA;
        this.shortB = shortB == null ? "" : shortB;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        state = buf.readByte();
        seconds = buf.readInt();
        shortA = readString(buf);
        shortB = readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(state);
        buf.writeInt(seconds);
        writeString(buf, shortA);
        writeString(buf, shortB);
    }

    static String readString(ByteBuf buf) {
        int len = buf.readInt();
        if (len < 0) {
            len = 0;
        }
        if (len > 2048) {
            len = 2048;
        }
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, UTF8);
    }

    static void writeString(ByteBuf buf, String text) {
        byte[] bytes = (text == null ? "" : text).getBytes(UTF8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static class Handler implements IMessageHandler<PacketWave, IMessage> {

        @Override
        public IMessage onMessage(PacketWave message, MessageContext ctx) {
            ChaosChoice.proxy.onWave(message.state, message.seconds, message.shortA, message.shortB);
            return null;
        }
    }
}
