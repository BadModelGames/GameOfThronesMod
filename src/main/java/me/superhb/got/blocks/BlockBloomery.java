package me.superhb.got.blocks;

import me.superhb.got.Main;
import me.superhb.got.tileentity.TileEntityBanner;
import me.superhb.got.tileentity.TileEntityBloomery;
import me.superhb.got.util.BloomeryGUIHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class BlockBloomery extends BlockContainer {
    public BlockBloomery (Material material) {
        super(material);
    }

    @Override
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {
        double y = 22.0D / 16.0D;
        double d1 = 2.0D / 16.0D;
        double d2 = 14.0D / 16.0D;

        return new AxisAlignedBB(d1, 0.0D, d1, d2, y, d2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox (IBlockState state, World world, BlockPos pos) {
        return state.getBoundingBox(world, pos).offset(pos);
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityBloomery();
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if (world.isRemote) return true;

        player.openGui(Main.instance, BloomeryGUIHandler.getGUIID(), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);

        // TODO: Drop Items on Break (Cannot cast ItemStackHandler to IInventory)
        if (tile instanceof TileEntityBloomery) {
            TileEntityBloomery te = (TileEntityBloomery) tile;

            //InventoryHelper.dropInventoryItems(world, pos, (IInventory)te.handler);
        }

        super.breakBlock(world, pos, state);
    }

    /*
    @Override
    public IBlockState getActualState (IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityBloomery) {
            TileEntityBloomery bloomery = (TileEntityBloomery)tile;

            int burningSlots = bloomery.numberOfBurningFuelSlots();
            burningSlots = MathHelper.clamp_int(burningSlots, 0, 4);
            return getDefaultState().withProperty(BURNING_SIDES_COUNT, burningSlots);
        }
        return state;
    }
    */

    @Override
    public IBlockState getStateFromMeta (int meta) {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return 0;
    }

    /*
    @Override
    protected BlockStateContainer createBlockState () {
        return new BlockStateContainer(this, new IProperty[] {BURNING_SIDES_COUNT} );
    }
    */

    //public static final PropertyInteger BURNING_SIDES_COUNT = PropertyInteger.create("burning_sides_count", 0, 4);

    //private static final int FOUR_SIDE_LIGHT_VALUE = 15;
    //private static final int ONE_SIDE_LIGHT_VALUE = 8;

    /*
    @Override
    public int getLightValue (IBlockState state, IBlockAccess world, BlockPos pos) {
        int lightValue = 0;
        IBlockState s = getActualState(getDefaultState(), world, pos);
        int burningSides = (Integer)s.getValue(BURNING_SIDES_COUNT);

        if (burningSides == 0) lightValue = 0;
        else lightValue = ONE_SIDE_LIGHT_VALUE + (int)((FOUR_SIDE_LIGHT_VALUE - ONE_SIDE_LIGHT_VALUE) / (4 - 1) * burningSides);

        return lightValue;
    }
    */

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer () {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isOpaqueCube (IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube (IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType (IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
