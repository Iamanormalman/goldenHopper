package com.example.morehoppers;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
    public static final int IRON_HOPPER_GUI_ID = 0;
    public static final int GOLDEN_HOPPER_GUI_ID = 1;
    public static final int DIAMOND_HOPPER_GUI_ID = 2;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch(ID) {
            case GOLDEN_HOPPER_GUI_ID:
                if (tileEntity instanceof TileEntityGoldenHopper)
                    return new ContainerGoldenHopper(player.inventory, (TileEntityGoldenHopper)tileEntity);
                break;
            case DIAMOND_HOPPER_GUI_ID:
                if (tileEntity instanceof TileEntityDiamondHopper)
                    return new ContainerDiamondHopper(player.inventory, (TileEntityDiamondHopper)tileEntity);
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        switch(ID) {
            case GOLDEN_HOPPER_GUI_ID:
                if (tileEntity instanceof TileEntityGoldenHopper)
                    return new GuiGoldenHopper(player.inventory, (TileEntityGoldenHopper)tileEntity);
                break;
            case IRON_HOPPER_GUI_ID:
                break;
            case DIAMOND_HOPPER_GUI_ID:
                if (tileEntity instanceof TileEntityDiamondHopper)
                    return new GuiDiamondHopper(player.inventory, (TileEntityDiamondHopper)tileEntity);
                break;
        }
        return null;
    }

}