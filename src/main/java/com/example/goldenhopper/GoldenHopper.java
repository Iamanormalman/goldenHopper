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
import net.minecraft.item.ItemBlock;

@Mod(modid = GoldenHopper.MODID, version = GoldenHopper.VERSION)
public class GoldenHopper
{
    public static final String MODID = "goldenhopper";
    public static final String VERSION = "1.0";

    @Instance(GoldenHopper.MODID)
    public static GoldenHopper instance;

    public static Block goldenHopperBlock;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // 註冊方塊
        goldenHopperBlock = new BlockGoldenHopper();
        GameRegistry.registerBlock(goldenHopperBlock, ItemBlock.class, "golden_hopper");

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
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}