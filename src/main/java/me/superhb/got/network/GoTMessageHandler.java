package me.superhb.got.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GoTMessageHandler implements IMessageHandler<GoTMessage, IMessage> {
    @Override
    public IMessage onMessage (GoTMessage message, MessageContext context) {
        EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;
        int sent = message.intSend;
        //serverPlayer.inventory.addItemStackToInventory()
        // Do Something with the message
        return null;
    }
}
