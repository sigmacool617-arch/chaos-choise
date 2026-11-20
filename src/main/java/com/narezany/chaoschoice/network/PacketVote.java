package com.narezany.chaoschoice.network;

import com.narezany.chaoschoice.ChoiceManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/** Клиент → сервер: игрок нажал A (0) или B (1). */
public class PacketVote implements IMessage {

    public int index;

    public PacketVote() {
    }

    public PacketVote(int index) {
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        index = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(index);
    }

    public static class Handler implements IMessageHandler<PacketVote, IMessage> {

        @Override
        public IMessage onMessage(PacketVote message, MessageContext ctx) {
            try {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                if (player != null) {
                    ChoiceManager.setPendingVote(player.getCommandSenderName(), message.index);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }
}
