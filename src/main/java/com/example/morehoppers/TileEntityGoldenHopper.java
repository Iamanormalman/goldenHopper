package com.example.morehoppers;

public class TileEntityGoldenHopper extends TileEntityBaseHopper
{
    private static final int TRANSFER_COOLDOWN = 2;

    @Override
    protected int getTransferCooldown()
    {
        return TRANSFER_COOLDOWN;
    }

    @Override
    protected String getContainerName()
    {
        return "container.goldenhopper";
    }
}