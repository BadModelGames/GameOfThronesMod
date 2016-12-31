package me.superhb.got.blocks.container;

import me.superhb.got.tileentity.TileEntityBloomery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBloomery extends Container {
    private TileEntityBloomery tile;
    private int[] cachedFields;

    // Slot Count
    // Player Inventory
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COL_COUNT = 9;
    private final int PLAYER_INV_SLOT_COUNT = PLAYER_INV_COL_COUNT * PLAYER_INV_ROW_COUNT; // 27
    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_SLOT_COUNT; // 36

    // Bloomery
    public final int FUEL_SLOTS_COUNT = 1;
    public final int INPUT_SLOTS_COUNT = 2;
    public final int OUTPUT_SLOTS_COUNT = 3;
    public final int BLOOMERY_SLOT_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT; // 6

    // Slot Index
    private final int VANILLA_FIRST_SLOT_INDEX = 0;
    private final int FUEL_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT; // 36
    private final int FIRST_INPUT_SLOT_INDEX = FUEL_SLOT_INDEX + FUEL_SLOTS_COUNT;
    private final int FIRST_OUTPUT_SLOT_INDEX = FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;

    // Slot Number
    private final int FUEL_SLOT_NUMBER = 0;
    private final int FIRST_INPUT_SLOT_NUMBER = FUEL_SLOT_NUMBER + FUEL_SLOTS_COUNT;
    private final int FIRST_OUTPUT_SLOT_NUMBER = FIRST_INPUT_SLOT_NUMBER + INPUT_SLOTS_COUNT;

    public ContainerBloomery () {}

    public ContainerBloomery (InventoryPlayer inventory, TileEntityBloomery tileEntity) {
        tile = tileEntity;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 142;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) addSlotToContainer(new Slot(inventory, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 84;

        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INV_COL_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COL_COUNT + x;
                int xPos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int yPos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;

                addSlotToContainer(new Slot(inventory, slotNumber, xPos, yPos));
            }
        }

        final int FUEL_XPOS = 35;
        final int FUEL_YPOS = 53;

        addSlotToContainer(new SlotFuel(tile.handler, FUEL_SLOT_NUMBER, FUEL_XPOS, FUEL_YPOS));

        final int INPUT_XPOS = 25;
        final int INPUT_YPOS = 17;
        final int INPUT_X_SPACING = 20;

        for (int i = 0; i < INPUT_SLOTS_COUNT; i++) {
            int xPos = INPUT_XPOS + (i * INPUT_X_SPACING);
            int slotNumber = i + FIRST_INPUT_SLOT_NUMBER;

            addSlotToContainer(new SlotInput(tile.handler, slotNumber, xPos, INPUT_YPOS));
        }

        final int OUTPUT_XPOS = 91;
        final int OUTPUT_YPOS = 35;
        final int OUTPUT_X_SPACING = 20;

        for (int i = 0; i < OUTPUT_SLOTS_COUNT; i++) {
            int xPos = OUTPUT_XPOS + (i * OUTPUT_X_SPACING);
            int slotNumber = i + FIRST_OUTPUT_SLOT_NUMBER;

            addSlotToContainer(new SlotOutput(tile.handler, slotNumber, xPos, OUTPUT_YPOS));
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int sourceSlotIndex) {
/*
        ItemStack stack = tile.handler.getStackInSlot(sourceSlotIndex);
        ItemStack copy = stack.copy();

        if (tile.handler.getStackInSlot(sourceSlotIndex) == ItemStack.field_190927_a) return ItemStack.field_190927_a;

        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_SLOT_COUNT) {
            if (TileEntityBloomery.getSmeltingResultForItem(stack) != ItemStack.field_190927_a) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, copy)) return ItemStack.field_190927_a;
            }
        }
*/
        Slot source = inventorySlots.get(sourceSlotIndex);

        if (source == null || !source.getHasStack() || source.getStack() == ItemStack.field_190927_a) return ItemStack.field_190927_a;

        ItemStack sourceStack = source.getStack();
        ItemStack copySourceStack = sourceStack.copy();

        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (TileEntityBloomery.getSmeltingResultForItem(sourceStack) != ItemStack.field_190927_a) { // TODO: FIRST Check Item is valid for slot
                if (!mergeItemStack(sourceStack, FIRST_INPUT_SLOT_INDEX, FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT, false)) return ItemStack.field_190927_a;
            } else if (TileEntityBloomery.getItemBurnTime(sourceStack) > 0) {
                if (!mergeItemStack(sourceStack, FUEL_SLOT_INDEX, FUEL_SLOT_INDEX, true)) return ItemStack.field_190927_a;
            } else { return ItemStack.field_190927_a; }
        } else if (sourceSlotIndex >= FUEL_SLOT_INDEX && sourceSlotIndex < FUEL_SLOT_INDEX + BLOOMERY_SLOT_COUNT) {
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) return ItemStack.field_190927_a;
        } else {
            System.err.println("Invalid slotIndex:" + sourceSlotIndex);
            return ItemStack.field_190927_a;
        }

        if (sourceStack.func_190916_E() == 0) source.putStack(ItemStack.field_190927_a);
        else source.onSlotChanged();

        // Calls onSlotChanged and returns given stack
        source.func_190901_a(player, sourceStack);

        return copySourceStack;
    }

    // TODO: Add fields back?
    /*
    @Override
    public void detectAndSendChanges () {
        super.detectAndSendChanges();

        boolean allFieldsHaveChanged = false;
        boolean fieldHasChanged[] = new boolean[tile.getFieldCount()];

        if (cachedFields == null) {
            cachedFields = new int[tile.getFieldCount()];
            allFieldsHaveChanged = true;
        }

        for (int i = 0; i < cachedFields.length; i++) {
            if (allFieldsHaveChanged || cachedFields[i] != tile.getField(i)) {
                cachedFields[i] = tile.getField(i);

                fieldHasChanged[i] = true;
            }
        }

        for (IContainerListener listener : this.listeners) {
            for (int i = 0; i < tile.getFieldCount(); i++) {
                if (fieldHasChanged[i]) listener.sendProgressBarUpdate(this, i, cachedFields[i]);
            }
        }
    }
    */

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar (int id, int data) {
        //tile.setField(id, data);
    }

    public class SlotFuel extends SlotItemHandler {
        public SlotFuel (IItemHandler handler, int index, int xPos, int yPos) {
            super(handler, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid (ItemStack stack) {
            return tile.isItemValidForFuelSlot(stack) || isBucket(stack);
        }

        @Override
        public int getItemStackLimit (ItemStack stack) {
            return isBucket(stack) ? 1 : super.getItemStackLimit(stack);
        }

        public boolean isBucket (ItemStack stack) {
            return stack.getItem() == Items.BUCKET;
        }
    }

    public class SlotInput extends SlotItemHandler {
        public SlotInput (IItemHandler handler, int index, int xPos, int yPos) {
            super(handler, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid (ItemStack stack) {
            return tile.isItemValidForInputSlot(stack);
        }
    }

    public class SlotOutput extends SlotItemHandler {
        public SlotOutput (IItemHandler handler, int index, int xPos, int yPos) {
            super(handler, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid (ItemStack stack) {
            return tile.isItemValidForOutputSlot(stack);
        }
    }
}
