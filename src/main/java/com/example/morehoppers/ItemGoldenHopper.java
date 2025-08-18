package com.example.morehoppers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

public class ItemGoldenHopper extends ItemBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon itemIcon;

    public ItemGoldenHopper(Block block)
    {
        super(block);
        // 設置物品的註冊名稱
        this.setUnlocalizedName("golden_hopper");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        // 註冊物品專用的材質，它會去尋找 "assets/goldenhopper/textures/items/golden_hopper.png"
        this.itemIcon = iconRegister.registerIcon(MoreHoppersMod.MODID + ":golden_hopper");
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