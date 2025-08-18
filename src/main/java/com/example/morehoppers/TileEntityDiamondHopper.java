package com.example.morehoppers;

public class TileEntityDiamondHopper extends TileEntityBaseHopper
{
    private static final int TRANSFER_COOLDOWN = 1;

    @Override
    protected int getTransferCooldown()
    {
        return TRANSFER_COOLDOWN;
    }

    @Override
    protected String getContainerName()
    {
        return "container.diamondhopper";
    }
}