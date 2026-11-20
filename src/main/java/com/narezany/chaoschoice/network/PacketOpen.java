package com.narezany.chaoschoice.network;

import java.io.IOException;

import com.narezany.chaoschoice.ChaosChoice;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** Сервер → клиент: открой меню с двумя вариантами. */
public class PacketOpen implements IMessage {

    public String nameA = "";
    public String nameB = "";
    public int seconds = 60;

    public PacketOpen() {
    }

    public PacketOpen(String nameA, String nameB, int seconds) {
        this.nameA = nameA == null ? "" : nameA;
        this.nameB = nameB == null ? "" : nameB;
        this.seconds = seconds;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            nameA = PacketWave.readString(buf);
            nameB = PacketWave.readString(buf);
            seconds = buf.readInt();
        } catch (IOException e) {
            nameA = "";
            nameB = "";
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketWave.writeString(buf, nameA);
        PacketWave.writeString(buf, nameB);
        buf.writeInt(seconds);
    }

    public static class Handler implements IMessageHandler<PacketOpen, IMessage> {

        @Override
        public IMessage onMessage(PacketOpen message, MessageContext ctx) {
            ChaosChoice.proxy.onOpen(message.nameA, message.nameB, message.seconds);
            return null;
        }
    }
}
