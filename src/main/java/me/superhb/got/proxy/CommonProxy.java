package me.superhb.got.proxy;

import me.superhb.got.Main;
import me.superhb.got.network.GoTMessage;
import me.superhb.got.network.GoTMessageHandler;
import me.superhb.got.network.GoTPacketHandler;
import me.superhb.got.util.BloomeryGUIHandler;
import me.superhb.got.util.GuiHandlerRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
    public void preInit () {
        NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, GuiHandlerRegistry.getInstance());
        GuiHandlerRegistry.getInstance().registerGuiHandler(new BloomeryGUIHandler(), BloomeryGUIHandler.getGUIID());
    }

    public void init () {}

    public void postInit () {}
}
