package me.superhb.got.items.material;

import me.superhb.got.items.GoTItems;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class GoTMaterial {
    /*
        WOOD(0, 59, 2.0F, 0.0F, 15),
        STONE(1, 131, 4.0F, 1.0F, 5),
        IRON(2, 250, 6.0F, 2.0F, 14),
        DIAMOND(3, 1561, 8.0F, 3.0F, 10),
        GOLD(0, 32, 12.0F, 0.0F, 22);
    */
    public static final ToolMaterial LOWSTEEL = EnumHelper.addToolMaterial("LowSteel", 0, 200, 4.0F, 1F, 10).setRepairItem(new ItemStack(GoTItems.lowSteel));
    public static final ToolMaterial STEEL = EnumHelper.addToolMaterial("Steel", 0, 300, 6.0F, 1.8F, 10).setRepairItem(new ItemStack(GoTItems.steel));
    public static final ToolMaterial HIGHSTEEL = EnumHelper.addToolMaterial("HighSteel", 0, 400, 8.0F, 2.7F, 10).setRepairItem(new ItemStack(GoTItems.highSteel));
    public static final ToolMaterial VALYRIAN = EnumHelper.addToolMaterial("Valyrian", 0, 3000, 13.0F, 3.5F, 10).setRepairItem(new ItemStack(GoTItems.valyrian));
}
