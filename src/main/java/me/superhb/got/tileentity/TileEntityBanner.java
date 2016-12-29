package me.superhb.got.tileentity;

import me.superhb.got.EnumHouse;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import javax.annotation.Nullable;

public class TileEntityBanner extends TileEntity implements ITickable {
    private int house;

    public TileEntityBanner () {
        house = 0;
    }

    public int getID () {
        return house;
    }

    public void setID (int id) {
        house = id;
    }

    @Override
    public void update () {
        if (worldObj.isRemote) {
            IBlockState state = this.worldObj.getBlockState(pos);
            final int FLAGS = 3;
            worldObj.notifyBlockUpdate(pos, state, state, FLAGS);
        }
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("House", house);

        return compound;
    }

    @Override
    public void readFromNBT (NBTTagCompound compound) {
        super.readFromNBT(compound);

        house = compound.getInteger("House");
    }

    @Override
    public NBTTagCompound getUpdateTag () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return tag;
    }

    @Override
    public void handleUpdateTag (NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        handleUpdateTag(tag);
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket () {
        NBTTagCompound tag = getUpdateTag();
        final int meta = 0;

        return new SPacketUpdateTileEntity(this.pos, meta, tag);
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState old, IBlockState newState) {
        if (world.isAirBlock(pos)) return true;
        return false;
    }
}
