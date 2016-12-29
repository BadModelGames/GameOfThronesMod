package me.superhb.got;

import me.superhb.got.blocks.GoTBlocks;
import me.superhb.got.items.GoTItems;
import me.superhb.got.proxy.CommonProxy;
import me.superhb.got.tileentity.TileEntityBanner;
import me.superhb.got.tileentity.TileEntityBloomery;
import me.superhb.got.util.BloomeryGUIHandler;
import me.superhb.got.util.GenerationHandler;
import me.superhb.got.util.GuiHandlerRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy proxy;

    @Instance(Reference.MODID)
    public static Main instance;

    public static CreativeTabs tab;

    @EventHandler
    public static void preInit (FMLPreInitializationEvent event) {
        tab = new CreativeTabs("Game of Thrones") {
            public ItemStack getTabIconItem () {
                return new ItemStack(Items.ARROW);
            }

            public String getTranslatedTabLabel () {
                return I18n.format("mod.got:name.locale");
            }
        };

        // Register Blocks
        GoTBlocks.init();
        GoTBlocks.register();

        // Register Items
        GoTItems.init();
        GoTItems.register();

        // Register TileEntities
        GameRegistry.registerTileEntity(TileEntityBanner.class, Reference.MODID + ":tile_banner");
        GameRegistry.registerTileEntity(TileEntityBloomery.class, Reference.MODID + ":tile_bloomery");

        // Register Ores
        OreDictionary.registerOre(Reference.MODID + ":ore_valyrian", GoTBlocks.valyrianOre);

        // Register World Generator
        GameRegistry.registerWorldGenerator(new GenerationHandler(), 2);

        // Register GUI
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, GuiHandlerRegistry.getInstance());
        GuiHandlerRegistry.getInstance().registerGuiHandler(new BloomeryGUIHandler(), BloomeryGUIHandler.getGUIID());

        proxy.preInit();
    }

    @EventHandler
    public static void init (FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public static void postInit (FMLPostInitializationEvent event) {

    }
}
