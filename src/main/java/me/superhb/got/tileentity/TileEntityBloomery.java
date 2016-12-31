package me.superhb.got.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.*;

import javax.annotation.Nullable;

/* TODO: Fix bugs:
    -putting item anywhere in bloomery puts a full stack
    -can't take anything out of bloomery (just disappears)
    -filling both inputs takes 2 out of the first input and returns two
*/
public class TileEntityBloomery extends TileEntity implements ITickable {
    public static final int FUEL_SLOTS_COUNT = 1;
    public static final int INPUT_SLOTS_COUNT = 2;
    public static final int OUTPUT_SLOTS_COUNT = 3;
    public static final int TOTAL_SLOT_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    public static final int FUEL_SLOT = 0;
    public static final int FIRST_INPUT_SLOT = FUEL_SLOT + FUEL_SLOTS_COUNT;
    public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;

    private int burnTimeRemaining;
    private int burnTimeInitialValue;
    private short cookTime;
    private static final short COOK_TIME_FOR_COMPLETION = 200;
    private int cachedNumberOfBurningSlots = -1;
    private static final ItemStack AIR = ItemStack.field_190927_a;

    public static ItemStackHandler handler;

    public TileEntityBloomery () {
        handler = new ItemStackHandler(TOTAL_SLOT_COUNT) {
            @Override
            protected void onContentsChanged(int slot) {
                TileEntityBloomery.this.markDirty();
            }
        };
    }

    public double fractionOfFuelRemaining () {
        if (burnTimeInitialValue <= 0) return 0;
        double fraction = burnTimeRemaining / (double)burnTimeInitialValue;
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    public int secondsOfFuelRemaining () {
        if (burnTimeRemaining <= 0) return 0;
        return burnTimeRemaining / 20;
    }

    public int numberOfBurningFuelSlots() {
        int burningCount = 0;
        if (burnTimeRemaining > 0) ++burningCount;
        return burningCount;
    }

    public double fractionOfCookTimeComplete () {
        double fraction = cookTime / (double)COOK_TIME_FOR_COMPLETION;
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        //return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        //return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)handler : super.getCapability(capability, facing);
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)handler : super.getCapability(capability, facing);
    }

    public boolean isUseableByPlayer (EntityPlayer player) {
        if (this.worldObj.getTileEntity(this.pos) != this) return false;

        final double X_CENTRE_OFFSET = 0.5D;
        final double Y_CENTRE_OFFSET = 0.5D;
        final double Z_CENTRE_OFFSET = 0.5D;
        final double MAXIMUM_DISTANCE_SQ = 8 * 8;

        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    @Override
    public void update () {
        if (canSmelt()) {
            if (burnFuel() > 0) cookTime += burnFuel();
            else cookTime -= 2;

            if (cookTime < 0) cookTime = 0;

            if (cookTime >= COOK_TIME_FOR_COMPLETION) {
                smeltItem();
                cookTime = 0;
            }
        } else {
            cookTime = 0;
        }

        if (cachedNumberOfBurningSlots != numberOfBurningFuelSlots()) {
            cachedNumberOfBurningSlots = numberOfBurningFuelSlots();

            if (worldObj.isRemote) {
                IBlockState state = this.worldObj.getBlockState(pos);
                worldObj.notifyBlockUpdate(pos, state, state, 3);
            }
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }

    private int burnFuel () {
        int burningCount = 0;
        boolean invChanged = false;
        ItemStack fuelStack = handler.getStackInSlot(FUEL_SLOT);

        if (burnTimeRemaining > 0) {
            --burnTimeRemaining;
            ++burningCount;
        }

        if (burnTimeRemaining == 0) {
            if (fuelStack != AIR && getItemBurnTime(fuelStack) > 0) {
                burnTimeRemaining = burnTimeInitialValue = getItemBurnTime(fuelStack);

                int stackSize = fuelStack.func_190916_E();
                fuelStack.func_190920_e(--stackSize);
                ++burningCount;
                invChanged = true;

                if (fuelStack.func_190916_E() == 0) fuelStack = fuelStack.getItem().getContainerItem(fuelStack);
            }
        }
        if (invChanged) markDirty();
        return burningCount;
    }

    private boolean canSmelt () {
        return smeltItem(false);
    }

    private void smeltItem () {
        smeltItem(true);
    }

    private boolean smeltItem (boolean performSmelt) {
        Integer firstSuitableInputSlot = null;
        Integer firstSuitableOutputSlot = null;
        ItemStack result = AIR;

        for (int i = FIRST_INPUT_SLOT; i < FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT; i++) {
            if (handler.getStackInSlot(i) != AIR) {
                result = getSmeltingResultForItem(handler.getStackInSlot(i));

                if (result != AIR) {
                    for (int o = FIRST_OUTPUT_SLOT; o < FIRST_OUTPUT_SLOT + OUTPUT_SLOTS_COUNT; o++) {
                        ItemStack outStack = handler.getStackInSlot(o);

                        if (outStack == AIR) {
                            firstSuitableInputSlot = i;
                            firstSuitableOutputSlot = o;
                            break;
                        }

                        if (outStack.getItem() == result.getItem() && (!outStack.getHasSubtypes() || outStack.getMetadata() == outStack.getMetadata()) && ItemStack.areItemStackTagsEqual(outStack, result)) {
                            int combinedSize = outStack.func_190916_E() + result.func_190916_E();

                            if (combinedSize <= 64 && combinedSize <= outStack.getMaxStackSize()) {
                                firstSuitableInputSlot = i;
                                firstSuitableOutputSlot = o;
                                break;
                            }
                        }
                    }
                    if (firstSuitableInputSlot != null) break;
                }
            }
        }
        if (firstSuitableInputSlot == null) return false;
        if (!performSmelt) return true;

        int stackSize = handler.getStackInSlot(firstSuitableInputSlot).func_190916_E();
        handler.getStackInSlot(firstSuitableInputSlot).func_190920_e(--stackSize);

        if (stackSize <= 0) handler.setStackInSlot(firstSuitableInputSlot, AIR); // TODO: Figure out difference between simulate = true and simulate = false
        if (handler.getStackInSlot(firstSuitableOutputSlot) == AIR) handler.setStackInSlot(firstSuitableOutputSlot, result.copy()); // TODO: Figure out difference between simulate = true and simulate = false
        else handler.getStackInSlot(firstSuitableOutputSlot).func_190920_e(handler.getStackInSlot(firstSuitableOutputSlot).func_190916_E() + result.func_190916_E());

        markDirty();

        return true;
    }

    // TODO Create own Recipes
    public static ItemStack getSmeltingResultForItem (ItemStack stack) {
        if (stack != AIR)
            return FurnaceRecipes.instance().getSmeltingResult(stack);
        return AIR;
    }

    public static short getItemBurnTime (ItemStack stack) {
        if (stack.func_190926_b()) return 0;
        else {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR) {
                Block block = Block.getBlockFromItem(item);
            }

            if (item == Items.LAVA_BUCKET) { // High grade
                // Store fuel
                return 20000;
            }
            if (item == Items.COAL && item.getMetadata(0) == 0) { // Regular
                // Store fuel
                return 1600;
            }
            if (item == Items.COAL && item.getMetadata(1) == 1) { // Low Grade
                // Store fuel
                return 1600;
            }
        }
        return 0;
    }

    /*
    @Override
    public ItemStack decrStackSize (int slotIndex, int count) {
        ItemStack stackInSlot = getStackInSlot(slotIndex);

        if (stackInSlot == AIR) return AIR;

        ItemStack stackRemoved;

        if (stackInSlot.func_190916_E() <= count) {
            stackRemoved = stackInSlot;
            setInventorySlotContents(slotIndex, AIR);
        } else {
            stackRemoved = stackInSlot.splitStack(count);

            if (stackInSlot.func_190916_E() == 0) setInventorySlotContents(slotIndex, AIR);
        }
        markDirty();

        return stackRemoved;
    }
    */

    static public boolean isItemValidForFuelSlot (ItemStack fuel) {
        return getItemBurnTime(fuel) > 0;
    }

    // TODO: Check input is iron + coal/charcoal (for steel)
    static public boolean isItemValidForInputSlot (ItemStack input) {
        return true;
    }

    static public boolean isItemValidForOutputSlot (ItemStack output) {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagCompound handlerCompound = handler.serializeNBT();

        compound.setTag("Handler", handlerCompound);
        compound.setShort("CookTime", cookTime);
        compound.setInteger("BurnTimeRemaining", burnTimeRemaining);
        compound.setInteger("BurnTimeInitial", burnTimeInitialValue);

        return compound;
    }

    @Override
    public void readFromNBT (NBTTagCompound compound) {
        super.readFromNBT(compound);

        cookTime = compound.getShort("CookTime");
        burnTimeRemaining = compound.getInteger("BurnTimeRemaining");
        burnTimeInitialValue = compound.getInteger("BurnTimeInitial");
        handler = new ItemStackHandler(TOTAL_SLOT_COUNT);
        handler.deserializeNBT(compound.getCompoundTag("Handler"));

        cachedNumberOfBurningSlots = -1;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket () {
        NBTTagCompound tag = getUpdateTag();
        final int meta = 0;

        return new SPacketUpdateTileEntity(this.pos, meta, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        handleUpdateTag(tag);
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

    /*
    @Override
    public String getName () {
        return "container.bloomery.name";
    }

    @Override
    public boolean hasCustomName () {
        return false;
    }
    */

    @Nullable
    @Override
    public ITextComponent getDisplayName () {
        //return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
        return new TextComponentString("Bloomery");
    }

    /*
    private static final byte COOK_FIELD_ID = 0;
    private static final byte FIRST_BURN_TIME_REMAINING_FIELD_ID = 1;
    private static final byte FIRST_BURN_TIME_INITIAL_FIELD_ID = FIRST_BURN_TIME_REMAINING_FIELD_ID + (byte)FUEL_SLOTS_COUNT;
    private static final byte NUMBER_OF_FIELDS = FIRST_BURN_TIME_INITIAL_FIELD_ID + (byte)FUEL_SLOTS_COUNT;

    @Override
    public int getField (int id) {
        if (id == COOK_FIELD_ID) return cookTime;
        //if (id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + FUEL_SLOTS_COUNT) return burnTimeRemaining[id - FIRST_BURN_TIME_INITIAL_FIELD_ID];
        if (id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + FUEL_SLOTS_COUNT) return burnTimeRemaining[0];

        System.err.println("Invalid field ID in TileEntityBloomery.getField: " + id);

        return 0;
    }

    @Override
    public void setField (int id, int value) {
        if (id == COOK_FIELD_ID) cookTime = (short)value;
        else if (id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + FUEL_SLOTS_COUNT) burnTimeRemaining[id - FIRST_BURN_TIME_REMAINING_FIELD_ID] = value;
        else if (id >= FIRST_BURN_TIME_INITIAL_FIELD_ID && id < FIRST_BURN_TIME_INITIAL_FIELD_ID + FUEL_SLOTS_COUNT) burnTimeRemaining[id - FIRST_BURN_TIME_INITIAL_FIELD_ID] = value;
        else System.err.println("Invalid field ID in TileEntityBloomery.setField: " + id);
    }

    @Override
    public int getFieldCount () {
        return NUMBER_OF_FIELDS;
    }
    */
}
