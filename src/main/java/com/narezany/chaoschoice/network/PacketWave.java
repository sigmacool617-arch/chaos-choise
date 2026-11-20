package com.narezany.chaoschoice.network;

import java.io.IOException;

import com.narezany.chaoschoice.ChaosChoice;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** Сервер → клиент: состояние волны для полоски внизу. */
public class PacketWave implements IMessage {

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
        try {
            state = buf.readByte();
            seconds = buf.readInt();
            shortA = readString(buf);
            shortB = readString(buf);
        } catch (IOException e) {
            shortA = "";
            shortB = "";
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(state);
        buf.writeInt(seconds);
        writeString(buf, shortA);
        writeString(buf, shortB);
    }

    static String readString(ByteBuf buf) throws IOException {
        int len = buf.readInt();
        if (len < 0 || len > 4096) {
            throw new IOException("bad string length");
        }
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = buf.readChar();
        }
        return new String(chars);
    }

    static void writeString(ByteBuf buf, String text) {
        String value = text == null ? "" : text;
        buf.writeInt(value.length());
        buf.writeChars(value);
    }

    public static class Handler implements IMessageHandler<PacketWave, IMessage> {

        @Override
        public IMessage onMessage(PacketWave message, MessageContext ctx) {
            ChaosChoice.proxy.onWave(message.state, message.seconds, message.shortA, message.shortB);
            return null;
        }
    }
}
