package me.superhb.got.blocks;

import me.superhb.got.EnumHouse;
import me.superhb.got.tileentity.TileEntityBanner;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockBanner extends Block {//ISpecialBlock, ITileEntityProvider {
    // TODO: MAKE facing data to metadata
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum HOUSE = PropertyEnum.create("house", EnumHouse.class);
    // TODO: MOVE hanging to TileEntity
    public static final PropertyBool HANGING = PropertyBool.create("hanging");

    public BlockBanner (Material material) {
        super(material);

        //setDefaultState(blockState.getBaseState().withProperty(HOUSE, EnumHouse.ARRYN));//.withProperty(HANGING, false));

        //isBlockContainer = true;
    }

    @Override
    public boolean hasTileEntity (IBlockState state) {
        return true;
    }

    /*
    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityBanner();
    }
    */

    @Override
    public TileEntity createTileEntity (World world, IBlockState state) {
        return new TileEntityBanner();
    }

    @Override
    public boolean isFullCube (IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube (IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable (IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSpawnInBlock() {
        return true;
    }

    // TODO: Different bounding box for different variants
    @Override
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox (IBlockState state, World world, BlockPos pos) {
        return state.getBoundingBox(world, pos).offset(pos);
    }

    /*
    // TODO: GET Special Name by TileEntity Data
    @Override
    public String getSpecialName (ItemStack stack) {
        return EnumHouse.getNid(stack.getItemDamage());
    }
    */

    /*
    @Override
    public ItemStack getPickBlock (IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(world.getBlockState(pos)));
    }
    */

    /*
    @Override
    public IBlockState withRotation (IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror (IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation((EnumFacing)state.getValue(FACING)));
    }
    */

    @Override
    public IBlockState getActualState (IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityBanner) {
            TileEntityBanner banner = (TileEntityBanner)tile;
            int id = banner.getID();
            //return state.withProperty(HOUSE, EnumHouse.getValue(id));
            return getDefaultState().withProperty(HOUSE, EnumHouse.STARK);
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        //return getDefaultState().withProperty(HANGING, meta == 0 ? false : true);
        return getDefaultState();
    }

    // TODO: GET Meta from FACING
    @Override
    public int getMetaFromState (IBlockState state) {
        //return state.getValue(HANGING).booleanValue() == false ? 0 : 1;
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new BlockStateContainer(this, new IProperty[] { HOUSE });
    }

    /*
    @Override
    public int damageDropped (IBlockState state) {
        return getMetaFromState(state);
    }
    */

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    /* TODO: ADD to CreativeTab via NBT data
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 14; i++) {
            ItemStack stack = new ItemStack(item, 1);
            NBTTagCompound compound = stack.getTagCompound();
            compound.setTag("banner", new NBTTagList());
            NBTTagCompound banner = compound.getCompoundTag("banner");
            banner.setInteger("house", i);

            list.add(stack);
        }
    }
    */

    /*
    @Override
    public IBlockState onBlockPlaced (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        //return getStateFromMeta(meta);
        return getStateFromMeta(meta).withProperty(HOUSE, EnumHouse.STARK);
    }
    */

    @Override
    public boolean eventReceived (IBlockState state, World world, BlockPos pos, int eventID, int eventParam) {
        super.eventReceived(state, world, pos, eventID, eventParam);

        TileEntity tile = world.getTileEntity(pos);

        return tile == null ? false : tile.receiveClientEvent(eventID, eventParam);
    }

    @Override
    public EnumBlockRenderType getRenderType (IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
