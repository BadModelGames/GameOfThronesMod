package me.superhb.got.proxy;

import me.superhb.got.blocks.GoTBlocks;
import me.superhb.got.items.GoTItems;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit () {
        super.preInit();

        GoTBlocks.registerRenders();
        GoTItems.registerRenders();
    }

    @Override
    public void init () {
        super.init();
    }

    @Override
    public void postInit () {
        super.postInit();
    }
}
