package com.example.morehoppers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGoldenHopper extends BlockBaseHopper
{
    public BlockGoldenHopper()
    {
        super();
        this.setBlockName("golden_hopper");
        this.setCreativeTab(MoreHoppersMod.moreHoppersTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityGoldenHopper();
    }

    @Override
    protected String getTexturePrefix()
    {
        return "golden_hopper";
    }

    @Override
    protected int getGuiId()
    {
        return GuiHandler.GOLDEN_HOPPER_GUI_ID;
    }
}