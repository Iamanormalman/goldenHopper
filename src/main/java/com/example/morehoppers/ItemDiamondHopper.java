package com.example.morehoppers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

public class ItemDiamondHopper extends ItemBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon itemIcon;

    public ItemDiamondHopper(Block block)
    {
        super(block);
        // 設置物品的註冊名稱
        this.setUnlocalizedName("diamond_hopper");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon(MoreHoppersMod.MODID + ":diamond_hopper");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return this.itemIcon;
    }

    @Override
    public int getSpriteNumber()
    {
        return 1;
    }
}