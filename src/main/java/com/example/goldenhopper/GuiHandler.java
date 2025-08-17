package com.example.goldenhopper;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
    public static final int GOLDEN_HOPPER_GUI_ID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == GOLDEN_HOPPER_GUI_ID)
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityGoldenHopper)
            {
                return new ContainerGoldenHopper(player.inventory, (TileEntityGoldenHopper)tileEntity);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == GOLDEN_HOPPER_GUI_ID)
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityGoldenHopper)
            {
                return new GuiGoldenHopper(player.inventory, (TileEntityGoldenHopper)tileEntity);
            }
        }
        return null;
    }
}