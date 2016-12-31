package me.superhb.got.client.gui;

import me.superhb.got.tileentity.TileEntityBloomery;
import me.superhb.got.blocks.container.ContainerBloomery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiBloomery extends GuiContainer {
    private static final ResourceLocation texture = new ResourceLocation("got", "textures/gui/bloomery_gui.png");
    private TileEntityBloomery tile;

    public GuiBloomery (InventoryPlayer inventory, TileEntityBloomery tile) {
        super(new ContainerBloomery(inventory, tile));

        xSize = 176;
        ySize = 166;
        this.tile = tile;
    }

    final int COOK_BAR_XPOS = 58;
    final int COOK_BAR_YPOS = 35;
    final int COOK_BAR_ICON_U = 176;
    final int COOK_BAR_ICON_V = 14;
    final int COOK_BAR_WIDTH = 24;
    final int COOK_BAR_HEIGHT = 17;

    final int FLAME_XPOS = 37;
    final int FLAME_YPOS = 37;
    final int FLAME_ICON_U = 176;
    final int FLAME_ICON_V = 0;
    final int FLAME_WIDTH = 14;
    final int FLAME_HEIGHT = 14;

    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        double cookProgress = tile.fractionOfCookTimeComplete();
        double burnRemaining = tile.fractionOfFuelRemaining();
        int yOffset = (int)((1.0 - burnRemaining) * FLAME_HEIGHT);

        drawTexturedModalRect(guiLeft + COOK_BAR_XPOS, guiTop + COOK_BAR_YPOS, COOK_BAR_ICON_U, COOK_BAR_ICON_V, (int)(cookProgress * COOK_BAR_WIDTH), COOK_BAR_HEIGHT);
        drawTexturedModalRect(guiLeft + FLAME_XPOS, guiTop + FLAME_YPOS + yOffset, FLAME_ICON_U, FLAME_ICON_V + yOffset, FLAME_WIDTH, FLAME_HEIGHT - yOffset);
    }

    // TODO: Put in correct spot
    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY) {
        /*
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        final int LABEL_XPOS = 5;
        final int LABEL_YPOS = 5;

        fontRendererObj.drawString(tile.getDisplayName().getUnformattedText(), LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());

        List<String> hoverText = new ArrayList<String>();

        if (isInRect(guiLeft + COOK_BAR_XPOS, guiTop + COOK_BAR_YPOS, COOK_BAR_WIDTH, COOK_BAR_HEIGHT, mouseX, mouseY)) {
            hoverText.add("Progress:");

            int cookPercentage = (int)(tile.fractionOfCookTimeComplete() * 100);

            hoverText.add(cookPercentage + "%");
        }

        for (int i = 0; i < tile.FUEL_SLOTS_COUNT; i++) {
            if (isInRect(guiLeft + FLAME_YPOS + FLAME_X_SPACING * i, guiTop + FLAME_YPOS, FLAME_WIDTH, FLAME_HEIGHT, mouseX, mouseY)) {
                hoverText.add("Fuel Time:");
                hoverText.add(tile.secondsOfFuelRemaining(i) + "s");
            }
        }

        if (!hoverText.isEmpty()) drawHoveringText(hoverText, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        */
    }

    public static boolean isInRect (int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }
}
