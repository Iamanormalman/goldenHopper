package com.example.goldenhopper;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = GoldenHopper.MODID, version = GoldenHopper.VERSION)
public class GoldenHopper
{
    public static final String MODID = "goldenhopper";
    public static final String VERSION = "1.0";

    @Instance(GoldenHopper.MODID)
    public static GoldenHopper instance;

    public static Block goldenHopperBlock;
    public static CreativeTabs goldenHopperTab = new CreativeTabs("goldenhopper") {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(goldenHopperBlock);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // 註冊方塊
        goldenHopperBlock = new BlockGoldenHopper();
        GameRegistry.registerBlock(goldenHopperBlock, ItemGoldenHopper.class, "golden_hopper");

        // 註冊TileEntity
        GameRegistry.registerTileEntity(TileEntityGoldenHopper.class, "GoldenHopper");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // 註冊GUI處理器
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        // 只在客戶端初始化渲染器
        if (event.getSide() == Side.CLIENT)
        {
            new GoldenHopperRenderer();
        }
        addRecipes();

    }

    private void addRecipes() {
        GameRegistry.addRecipe(new ItemStack(goldenHopperBlock, 1),
                "G G",
                "GHG",
                " G ",
                'G', Items.gold_ingot,
                'H', Blocks.hopper
        );
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}