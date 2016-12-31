package me.superhb.got.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GoTMessage implements IMessage {
    public int intSend;

    public GoTMessage () {}

    public GoTMessage (int send) {
        intSend = send;
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(intSend);
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        intSend = buf.readInt();
    }
}
