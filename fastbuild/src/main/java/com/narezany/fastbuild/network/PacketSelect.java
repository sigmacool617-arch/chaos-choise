package com.narezany.fastbuild.network;

import com.narezany.fastbuild.SelectionStore;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/** Клиент → сервер: левый клик по блоку (x, y, z). */
public class PacketSelect implements IMessage {

    public int x;
    public int y;
    public int z;

    public PacketSelect() {
    }

    public PacketSelect(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<PacketSelect, IMessage> {

        @Override
        public IMessage onMessage(PacketSelect message, MessageContext ctx) {
            try {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                if (player != null) {
                    SelectionStore.queueSelect(player.getCommandSenderName(),
                            message.x, message.y, message.z);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }
}
