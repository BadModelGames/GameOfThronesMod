package me.superhb.got.blocks;

import me.superhb.got.Main;
import me.superhb.got.Reference;
import me.superhb.got.items.ItemBlockBanner;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GoTBlocks {
    // Misc.
    public static Block banner, valyrian, steel;

    // Ore
    public static Block valyrianOre;

    // Machine
    public static Block bloomery;

    public static void init () {
        // Misc.
        banner = new BlockBanner(Material.CLOTH).setRegistryName("banner").setCreativeTab(Main.tab);
        valyrian = new Block(Material.IRON).setRegistryName("valyrian_block").setCreativeTab(Main.tab);
        steel = new Block(Material.IRON).setRegistryName("steel_block").setCreativeTab(Main.tab);

        // Ore
        valyrianOre = new Block(Material.ROCK).setRegistryName("valyrian_ore").setCreativeTab(Main.tab);

        // Machine
        bloomery = new BlockBloomery(Material.ROCK).setRegistryName("bloomery").setCreativeTab(Main.tab);
    }

    public static void register () {
        // Misc.
        registerBlock(valyrian);
        registerBlock(steel);

        // Banner
        //registerBlock(banner);
        GameRegistry.register(banner.setUnlocalizedName(banner.getRegistryName().toString()));
        ItemBlockBanner itemBanner = new ItemBlockBanner(banner);
        itemBanner.setRegistryName(banner.getRegistryName());
        GameRegistry.register(itemBanner);

        // Ore
        registerBlock(valyrianOre);

        // Machine
        registerBlock(bloomery);
    }

    public static void registerBlock (Block b) {
        GameRegistry.register(b.setUnlocalizedName(b.getRegistryName().toString()));
        ItemBlock i = new ItemBlock(b);
        i.setRegistryName(b.getRegistryName());
        GameRegistry.register(i);
    }

    public static void registerRenders () {
        // Misc.
        //for (int i = 0; i < 14; i++) registerRender(banner, 0, "banner", "house=" + hName[i] + ",hanging=false");
        //for (int i = 0; i < 14; i++) registerRender(banner, 0, "banner", "house=" + hName[i] + ",hanging=true");
        registerRender(banner);

        // Ore
        registerRender(valyrianOre);

        // Machine
        registerRender(bloomery);
    }

    public static void registerRender (Block b) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory"));
    }

    public static void registerRender (Block b, int metadata, String model) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), metadata, new ModelResourceLocation(Reference.MODID + ":" + model, "inventory"));
    }

    public static void registerRender (Block b, int metadata, String model, String variant) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), metadata, new ModelResourceLocation(Reference.MODID + ":" + model, variant));
    }
}
