package me.superhb.got.util;

import me.superhb.got.gui.GuiBloomery;
import me.superhb.got.tileentity.TileEntityBloomery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class BloomeryGUIHandler implements IGuiHandler {
    private static final int bloomery = 31;

    public static int getGUIID () {
        return bloomery;
    }

    @Override
    public Object getServerGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id != getGUIID()) {
            System.err.println("Invalid ID. expected " + getGUIID() + ", received " + id);
        }

        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityBloomery) {
            TileEntityBloomery bloomery = (TileEntityBloomery)tile;

            return new ContainerBloomery(player.inventory, bloomery);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id != getGUIID()) System.err.println("Invalid ID! Expected " + getGUIID() + ", received " + id);

        BlockPos pos = new BlockPos(x ,y, z);
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityBloomery) {
            TileEntityBloomery bloomery = (TileEntityBloomery)tile;

            return new GuiBloomery(player.inventory, bloomery);
        }
        return null;
    }
}
