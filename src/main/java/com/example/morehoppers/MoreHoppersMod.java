package com.example.morehoppers;

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

@Mod(modid = MoreHoppersMod.MODID, version = MoreHoppersMod.VERSION)
public class MoreHoppersMod
{
    public static final String MODID = "morehoppers";
    public static final String VERSION = "1.0";

    @Instance(MoreHoppersMod.MODID)
    public static MoreHoppersMod instance;

    public static Block goldenHopperBlock;
    public static Block diamondHopperBlock;
    public static CreativeTabs moreHoppersTab = new CreativeTabs("morehoppers") {
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
        diamondHopperBlock = new BlockDiamondHopper();
        GameRegistry.registerBlock(diamondHopperBlock, ItemDiamondHopper.class, "diamond_hopper");

        // 註冊TileEntity
        GameRegistry.registerTileEntity(TileEntityGoldenHopper.class, "GoldenHopper");
        GameRegistry.registerTileEntity(TileEntityDiamondHopper.class, "DiamondHopper"); // 新增
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // 註冊GUI處理器
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        // 只在客戶端初始化渲染器
        if (event.getSide() == Side.CLIENT)
        {
            // 使用通用渲染器替代原來的 GoldenHopperRenderer
            new UniversalHopperRenderer();
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
        GameRegistry.addRecipe(new ItemStack(diamondHopperBlock, 1),
                "D D",
                "DHD",
                " D ",
                'D', Items.diamond,
                'H', Blocks.hopper
        );

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}