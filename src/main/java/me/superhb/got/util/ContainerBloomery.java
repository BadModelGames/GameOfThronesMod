package me.superhb.got.util;

import me.superhb.got.tileentity.TileEntityBloomery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;

import javax.imageio.metadata.IIOInvalidTreeException;

public class ContainerBloomery extends Container {
    private TileEntityBloomery tile;
    private int[] cachedFields;

    // Slot Count
    // Player Inventory
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COL_COUNT = 9;
    private final int PLAYER_INV_SLOT_COUNT = PLAYER_INV_COL_COUNT * PLAYER_INV_ROW_COUNT;
    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_SLOT_COUNT;

    // Bloomery
    public final int FUEL_SLOTS_COUNT = 1;
    public final int INPUT_SLOTS_COUNT = 6;
    public final int OUTPUT_SLOTS_COUNT = 1;
    public final int BLOOMERY_SLOT_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    // Slot Index
    private final int VANILLA_FIRST_SLOT_INDEX = 0;
    private final int FIRST_FUEL_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private final int FIRST_INPUT_SLOT_INDEX = FIRST_FUEL_SLOT_INDEX + FUEL_SLOTS_COUNT;
    private final int FIRST_OUTPUT_SLOT_INDEX = FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;

    // Slot Number
    private final int FIRST_FUEL_SLOT_NUMBER = 0;
    private final int FIRST_INPUT_SLOT_NUMBER = FIRST_FUEL_SLOT_NUMBER + FUEL_SLOTS_COUNT;
    private final int FIRST_OUTPUT_SLOT_NUMBER = FIRST_INPUT_SLOT_NUMBER + INPUT_SLOTS_COUNT;

    public ContainerBloomery (InventoryPlayer inventory, TileEntityBloomery tile) {
        this.tile = tile;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 183;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) addSlotToContainer(new Slot(inventory, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 125;

        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INV_COL_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COL_COUNT + x;
                int xPos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int yPos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;

                addSlotToContainer(new Slot(inventory, slotNumber, xPos, yPos));
            }
        }

        final int FUEL_SLOTS_XPOS = 53;
        final int FUEL_SLOTS_YPOS = 96;

        for (int x = 0; x < FUEL_SLOTS_COUNT; x++) {
            int slotNumber = x + FIRST_FUEL_SLOT_NUMBER;

            addSlotToContainer(new SlotFuel(tile, slotNumber, FUEL_SLOTS_XPOS + SLOT_X_SPACING * x, FUEL_SLOTS_YPOS));
        }

        final int INPUT_SLOTS_XPOS = 26;
        final int INPUT_SLOTS_YPOS = 24;

        for (int y = 0; y < INPUT_SLOTS_COUNT; y++) {
            int slotNumber = y + FIRST_INPUT_SLOT_NUMBER;

            addSlotToContainer(new SlotInput(tile, slotNumber, INPUT_SLOTS_XPOS, INPUT_SLOTS_YPOS + SLOT_Y_SPACING * y));
        }

        final int OUTPUT_SLOTS_XPOS = 134;
        final int OUTPUT_SLOTS_YPOS = 24;

        for (int y = 0; y < OUTPUT_SLOTS_COUNT; y++) {
            int slotNumber = y + FIRST_OUTPUT_SLOT_NUMBER;

            addSlotToContainer(new SlotOutput(tile, slotNumber, OUTPUT_SLOTS_XPOS, OUTPUT_SLOTS_YPOS + SLOT_Y_SPACING * y));
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int sourceSlotIndex) {
        Slot source = (Slot)inventorySlots.get(sourceSlotIndex);

        if (source == null || !source.getHasStack()) return null;

        ItemStack sourceStack = source.getStack();
        ItemStack copySourceStack = sourceStack.copy();

        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (TileEntityBloomery.getSmeltingResultForItem(sourceStack) != null) {
                if (!mergeItemStack(sourceStack, FIRST_INPUT_SLOT_INDEX, FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT, false)) return null;
            } else if (tile.getItemBurnTime(sourceStack) > 0) {
                if (!mergeItemStack(sourceStack, FIRST_FUEL_SLOT_INDEX, FIRST_FUEL_SLOT_INDEX + FUEL_SLOTS_COUNT, true)) return null;
            } else {
                return null;
            }
        } else if (sourceSlotIndex >= FIRST_FUEL_SLOT_INDEX && sourceSlotIndex < FIRST_FUEL_SLOT_INDEX + BLOOMERY_SLOT_COUNT) {
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) return null;
        } else {
            System.err.print("Invalid slotIndex: " + sourceSlotIndex);
            return null;
        }

        if (sourceStack.func_190916_E() == 0) source.putStack(null);
        else source.onSlotChanged();

        source.func_190901_a(player, sourceStack);

        return copySourceStack;
    }

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

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar (int id, int data) {
        tile.setField(id, data);
    }

    public class SlotFuel extends Slot {
        public SlotFuel (IInventory inventory, int index, int xPos, int yPos) {
            super(inventory, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid (ItemStack stack) {
            return TileEntityBloomery.isItemValidForFuelSlot(stack);
        }
    }

    public class SlotInput extends Slot {
        public SlotInput (IInventory inventory, int index, int xPos, int yPos) {
            super(inventory, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid (ItemStack stack) {
            return TileEntityBloomery.isItemValidForInputSlot(stack);
        }
    }

    public class SlotOutput extends Slot {
        public SlotOutput (IInventory inventory, int index, int xPos, int yPos) {
            super(inventory, index, xPos, yPos);
        }

        @Override
        public boolean isItemValid (ItemStack stack) {
            return TileEntityBloomery.isItemValidForOutputSlot(stack);
        }
    }
}
