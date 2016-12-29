package me.superhb.got.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;

import javax.annotation.Nullable;
import java.util.Arrays;

public class TileEntityBloomery extends TileEntity implements IInventory, ITickable {
    public static final int FUEL_SLOTS_COUNT = 1;
    public static final int INPUT_SLOTS_COUNT = 6;
    public static final int OUTPUT_SLOTS_COUNT = 1;
    public static final int TOTAL_SLOT_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    public static final int FIRST_FUEL_SLOT = 0;
    public static final int FIRST_INPUT_SLOT = FIRST_FUEL_SLOT + FUEL_SLOTS_COUNT;
    public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;

    private ItemStack[] stacks = new ItemStack[TOTAL_SLOT_COUNT];
    private int[] burnTimeRemaining = new int[FUEL_SLOTS_COUNT];
    private int[] burnTimeInitialValue = new int[FUEL_SLOTS_COUNT];
    private short cookTime;
    private static final short COOK_TIME_FOR_COMPLETION = 200;
    private int cachedNumberOfBurningSlots = -1;

    public double fractionOfFuelRemaining (int slot) {
        if (burnTimeInitialValue[slot] <= 0) return 0;
        double fraction = burnTimeRemaining[slot] / (double)burnTimeInitialValue[slot];
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    public int secondsOfFuelRemaining (int slot) {
        if (burnTimeRemaining[slot] <= 0) return 0;
        return burnTimeRemaining[slot] / 20;
    }

    public int numberOfBurningFuelSlots () {
        int burningCount = 0;

        for (int burnTime : burnTimeRemaining) {
            if (burnTime > 0) ++burningCount;
        }
        return burningCount;
    }

    public double fractionOfCookTimeComplete () {
        double fraction = cookTime / (double)COOK_TIME_FOR_COMPLETION;
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    @Override
    public void update () {
        if (canSmelt()) {
            int numberOfFuelBurning = burnFuel();

            if (numberOfFuelBurning > 0) cookTime += numberOfFuelBurning;
            else cookTime -= 2;

            if (cookTime < 0) cookTime = 0;

            if (cookTime >= COOK_TIME_FOR_COMPLETION) {
                smeltItem();
                cookTime = 0;
            }
        } else {
            cookTime = 0;
        }

        int numberBurning = numberOfBurningFuelSlots();

        if (cachedNumberOfBurningSlots != numberBurning) {
            cachedNumberOfBurningSlots = numberBurning;

            if (worldObj.isRemote) {
                IBlockState state = this.worldObj.getBlockState(pos);
                final  int FLAGS = 3;
                worldObj.notifyBlockUpdate(pos, state, state, FLAGS);
            }
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }

    private int burnFuel () {
        int burningCount = 0;
        boolean invChanged = false;

        for (int i = 0; i < FUEL_SLOTS_COUNT; i++) {
            int fuelSlotNumber = i + FIRST_FUEL_SLOT;

            if (burnTimeRemaining[i] > 0) {
                --burnTimeRemaining[i];
                ++burningCount;
            }

            if (burnTimeRemaining[i] == 0) {
                if (stacks[fuelSlotNumber] != null && getItemBurnTime(stacks[fuelSlotNumber]) > 0) {
                    burnTimeRemaining[i] = burnTimeInitialValue[i] = getItemBurnTime(stacks[fuelSlotNumber]);

                    int stackSize = stacks[fuelSlotNumber].func_190916_E();
                    stacks[fuelSlotNumber].func_190920_e(--stackSize);
                    ++burningCount;
                    invChanged = true;

                    if (stacks[fuelSlotNumber].func_190916_E() == 0) stacks[fuelSlotNumber] = stacks[fuelSlotNumber].getItem().getContainerItem(stacks[fuelSlotNumber]);
                }
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
        ItemStack result = null;

        for (int i = FIRST_INPUT_SLOT; i < FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT; i++) {
            if (stacks[i] != null) {
                result = getSmeltingResultForItem(stacks[i]);

                if (result != null) {
                    for (int o = FIRST_OUTPUT_SLOT; o < FIRST_OUTPUT_SLOT + OUTPUT_SLOTS_COUNT; o++) {
                        ItemStack outStack = stacks[o];

                        if (outStack == null) {
                            firstSuitableInputSlot = i;
                            firstSuitableOutputSlot = o;
                            break;
                        }

                        if (outStack.getItem() == result.getItem() && (!outStack.getHasSubtypes() || outStack.getMetadata() == outStack.getMetadata()) && ItemStack.areItemStackTagsEqual(outStack, result)) {
                            int combinedSize = stacks[o].func_190916_E() + result.func_190916_E();

                            if (combinedSize <= getInventoryStackLimit() && combinedSize <= stacks[o].getMaxStackSize()) {
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

        int stackSize = stacks[firstSuitableInputSlot].func_190916_E();

        stacks[firstSuitableInputSlot].func_190920_e(--stackSize);

        if (stackSize <= 0) stacks[firstSuitableInputSlot] = null;
        if (stacks[firstSuitableOutputSlot] == null) stacks[firstSuitableOutputSlot] = result.copy();
        else stacks[firstSuitableOutputSlot].func_190920_e(stacks[firstSuitableOutputSlot].func_190916_E() + result.func_190916_E());

        markDirty();

        return true;
    }

    public static ItemStack getSmeltingResultForItem (ItemStack stack) {
        return FurnaceRecipes.instance().getSmeltingResult(stack);
    }

    public static short getItemBurnTime (ItemStack stack) {
        int burnTime = TileEntityBloomery.getItemBurnTime(stack);

        return (short)MathHelper.clamp_int(burnTime, 0, Short.MAX_VALUE);
    }

    @Override
    public int getSizeInventory () {
        return stacks.length;
    }

    @Override
    public ItemStack getStackInSlot (int i) {
        return stacks[i];
    }

    @Override
    public ItemStack decrStackSize (int slotIndex, int count) {
        ItemStack stackInSlot = getStackInSlot(slotIndex);

        if (stackInSlot == null) return null;

        ItemStack stackRemoved;

        if (stackInSlot.func_190916_E() <= count) {
            stackRemoved = stackInSlot;
            setInventorySlotContents(slotIndex, null);
        } else {
            stackRemoved = stackInSlot.splitStack(count);

            if (stackInSlot.func_190916_E() == 0) setInventorySlotContents(slotIndex, null);
        }
        markDirty();

        return stackRemoved;
    }

    @Override
    public void setInventorySlotContents (int slotIndex, ItemStack stack) {
        stacks[slotIndex] = stack;

        if (stack != null && stack.func_190916_E() > getInventoryStackLimit()) stack.func_190920_e(getInventoryStackLimit());
        markDirty();
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        if (this.worldObj.getTileEntity(this.pos) != this) return false;

        final double X_CENTRE_OFFSET = 0.5D;
        final double Y_CENTRE_OFFSET = 0.5D;
        final double Z_CENTRE_OFFSET = 0.5D;
        final double MAXIMUM_DISTANCE_SQ = 8 * 8;

        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    // TODO: Check fuel is lava, coal, charcoal, etc.
    static public boolean isItemValidForFuelSlot (ItemStack fuel) {
        return true;
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

        NBTTagList slotsData = new NBTTagList();

        for (int i = 0; i < this.stacks.length; ++i) {
            if (this.stacks[i] != null) {
                NBTTagCompound slotData = new NBTTagCompound();
                slotData.setByte("Slot", (byte)i);
                this.stacks[i].writeToNBT(slotData);
                slotsData.appendTag(slotData);
            }
        }
        compound.setTag("Items", slotsData);
        compound.setShort("CookTime", cookTime);
        compound.setTag("burnTimeRemaining", new NBTTagIntArray(burnTimeRemaining));
        compound.setTag("burnTimeInitial", new NBTTagIntArray(burnTimeInitialValue));

        return compound;
    }

    @Override
    public void readFromNBT (NBTTagCompound compound) {
        super.readFromNBT(compound);

        final byte NBT_TYPE_COMPOUND = 10;

        NBTTagList slotsData = compound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(stacks, null);

        for (int i = 0; i < slotsData.tagCount(); i++) {
            NBTTagCompound slotData = slotsData.getCompoundTagAt(i);
            byte slotNumber = slotData.getByte("Slot");

            if (slotNumber >= 0 && slotNumber < this.stacks.length) {
                this.stacks[slotNumber] = new ItemStack(slotData);
            }
        }
        cookTime = compound.getShort("CookTime");
        burnTimeRemaining = Arrays.copyOf(compound.getIntArray("burnTimeRemaining"), FUEL_SLOTS_COUNT);
        burnTimeInitialValue = Arrays.copyOf(compound.getIntArray("burnTimeInitial"), FUEL_SLOTS_COUNT);
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

    @Override
    public void clear () {
        Arrays.fill(stacks, null);
    }

    @Override
    public String getName () {
        return "container.bloomery.name";
    }

    @Override
    public boolean hasCustomName () {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName () {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    private static final byte COOK_FIELD_ID = 0;
    private static final byte FIRST_BURN_TIME_REMAINING_FIELD_ID = 1;
    private static final byte FIRST_BURN_TIME_INITIAL_FIELD_ID = FIRST_BURN_TIME_REMAINING_FIELD_ID + (byte)FUEL_SLOTS_COUNT;
    private static final byte NUMBER_OF_FIELDS = FIRST_BURN_TIME_INITIAL_FIELD_ID + (byte)FUEL_SLOTS_COUNT;

    @Override
    public int getField (int id) {
        if (id == COOK_FIELD_ID) return cookTime;
        if (id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + FUEL_SLOTS_COUNT) return burnTimeRemaining[id - FIRST_BURN_TIME_INITIAL_FIELD_ID];

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

    @Override
    public boolean isItemValidForSlot (int slotIndex, ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot (int slotIndex) {
        ItemStack stack = getStackInSlot(slotIndex);

        if (stack != null) setInventorySlotContents(slotIndex, null);

        return stack;
    }

    @Override
    public void openInventory (EntityPlayer player) {}

    @Override
    public void closeInventory (EntityPlayer player) {}

    @Override
    public boolean func_191420_l () {
        return false;
    }
}
