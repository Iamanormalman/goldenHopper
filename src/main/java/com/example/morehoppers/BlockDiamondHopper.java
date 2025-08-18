package com.example.morehoppers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDiamondHopper extends BlockBaseHopper
{
    public BlockDiamondHopper()
    {
        super();
        this.setBlockName("diamond_hopper");
        this.setCreativeTab(MoreHoppersMod.moreHoppersTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityDiamondHopper();
    }

    @Override
    protected String getTexturePrefix()
    {
        return "diamond_hopper";
    }

    @Override
    protected int getGuiId()
    {
        return GuiHandler.DIAMOND_HOPPER_GUI_ID;
    }
}