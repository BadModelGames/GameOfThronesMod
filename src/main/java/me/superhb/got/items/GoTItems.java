package me.superhb.got.items;

import me.superhb.got.Main;
import me.superhb.got.Reference;
import me.superhb.got.items.material.GoTMaterial;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GoTItems {
    // Weapons
    public static Item needle;

    // Armor
    public static Item starkArmor;

    // Misc.
    public static Item sigil, steel, highSteel, lowSteel, valyrian;

    public static void init () {
        // Weapons
        needle = new ItemSword(GoTMaterial.HIGHSTEEL).setRegistryName("needle").setCreativeTab(Main.tab);

        // Armor

        // Misc.
        // steel must be mixed with iron and coal in a bloomery
        // [Bloomery] coal + iron ore (burned with coal) = steel
        // [Bloomery] coal + iron ingot (burned with coal) = low steel
        // [Bloomery] coal + iron ingot (burned with lava) = steel
        // [Bloomery] coal + iron ore (burned with lava) = high steel
        // [Furnace] steel (burned with lava) = high steel (chance of purifying?)
        // [Furnace] low steel (burned with lava) = steel (change of purifying?)
        steel = new Item().setRegistryName("steel").setCreativeTab(Main.tab);
        highSteel = new Item().setRegistryName("high_steel").setCreativeTab(Main.tab);
        lowSteel = new Item().setRegistryName("low_steel").setCreativeTab(Main.tab);
        valyrian = new Item().setRegistryName("valyrian").setCreativeTab(Main.tab);
    }

    public static void register () {
        // Weapons
        registerItem(needle);

        // Armor

        // Misc.
        registerItem(steel);
        registerItem(highSteel);
        registerItem(lowSteel);
        registerItem(valyrian);
    }

    public static void registerItem (Item i) {
        GameRegistry.register(i.setUnlocalizedName(i.getRegistryName().toString()));
    }

    public static void registerRenders () {
        // Weapons
        registerRender(needle);

        // Armor

        // Misc.
        registerRender(steel);
        registerRender(highSteel);
        registerRender(lowSteel);
        registerRender(valyrian);
    }

    public static void registerRender (Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
    }

    public static void registerRender (Item i, int metadata, String model) {
        ModelLoader.setCustomModelResourceLocation(i, metadata, new ModelResourceLocation(Reference.MODID + ":" + model, "inventory"));
    }
}
