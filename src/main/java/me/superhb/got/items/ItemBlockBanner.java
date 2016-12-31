package me.superhb.got.items;

import me.superhb.got.properties.EnumHouse;
import me.superhb.got.tileentity.TileEntityBanner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockBanner extends ItemBlock {
    public ItemBlockBanner (Block block) {
        super(block);

        //if (!(block instanceof ISpecialBlock)) throw new IllegalArgumentException(String.format("The given block, %s, is not an instance of ISpecialBlock", block.getUnlocalizedName()));

        //setHasSubtypes(true);
    }

    public int getMetadata (int damage) {
        return damage;
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());

            NBTTagCompound compound = stack.getTagCompound();
            compound.setInteger("House", 0);
        }

        NBTTagCompound compound = stack.getTagCompound();
        int houseVal = compound.getInteger("House");

        if (world.getTileEntity(pos) instanceof TileEntityBanner) {
            TileEntityBanner tile = (TileEntityBanner)world.getTileEntity(pos);
            tile.setID(houseVal);
        }
        return true;
    }

    @Override
    public String getItemStackDisplayName (ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());

            NBTTagCompound compound = stack.getTagCompound();
            compound.setInteger("House", 0);
        }

        NBTTagCompound compound = stack.getTagCompound();
        int houseVal = compound.getInteger("House");

        return I18n.format("tile.got:banner." + EnumHouse.getNid(houseVal) + ".name");
    }

    /*
    @Override
    public void onUpdate (ItemStack stack, World world, Entity entity, int metadata, boolean par4) {
        if (stack.getTagCompound() == null) stack.getTagCompound().setInteger("house", 0);
    }
    */

    /*
    @Override
    public String getUnlocalizedName (ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ((ISpecialBlock)block).getSpecialName(stack);
    }
    */

    /*
    @Override
    public void addInformation (ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)  {
        super.addInformation(stack, playerIn, tooltip, advanced);

        NBTTagCompound compound = stack.getTagCompound();

        if (compound.getCompoundTag("house") != null) {
            tooltip.add("House ID:" + compound.getInteger("house"));
        } else {
            compound.setInteger("house", 0);
            tooltip.add("House ID: " + compound.getInteger("house"));
        }
    }
    */
}
