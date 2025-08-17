package com.example.goldenhopper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockGoldenHopper extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon insideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon outsideIcon;

    public BlockGoldenHopper()
    {
        super(Material.iron);
        this.setBlockName("golden_hopper");
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setStepSound(soundTypeMetal);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        // 使用與原版漏斗相同的貼圖命名規則
        this.topIcon = iconRegister.registerIcon(GoldenHopper.MODID + ":golden_hopper_top");
        this.insideIcon = iconRegister.registerIcon(GoldenHopper.MODID + ":golden_hopper_inside");
        this.outsideIcon = iconRegister.registerIcon(GoldenHopper.MODID + ":golden_hopper_outside");

        // 設置blockIcon為outside材質（用於掉落物品等）
        this.blockIcon = this.outsideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        // 完全模擬原版漏斗的材質邏輯
        // side 1 = 頂部使用top材質
        // 其他所有側面使用outside材質
        return side == 1 ? this.topIcon : this.outsideIcon;
    }

    // 如果你想要漏斗內部使用不同貼圖，可以覆蓋這個方法
    @SideOnly(Side.CLIENT)
    public IIcon getHopperInsideIcon()
    {
        return this.insideIcon;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        // 完全模擬原版漏斗的放置邏輯
        // 默認朝下
        int hopperMetadata = 0;

        // 檢查放置的面，如果在側面且符合條件，漏嘴朝向該面
        if (side != 0 && side != 1) // 不是頂面或底面
        {
            // 將面ID轉換為漏斗的方向metadata
            switch (side)
            {
                case 2: // 北面 (Z-)
                    hopperMetadata = 2;
                    break;
                case 3: // 南面 (Z+)
                    hopperMetadata = 3;
                    break;
                case 4: // 西面 (X-)
                    hopperMetadata = 4;
                    break;
                case 5: // 東面 (X+)
                    hopperMetadata = 5;
                    break;
                default:
                    hopperMetadata = 0; // 默認向下
                    break;
            }
        }

        return hopperMetadata;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        // 如果玩家沒有潛行，強制設置為向下
        if (placer instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) placer;
            if (!player.isSneaking())
            {
                // 不潛行時，強制向下
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            }
            // 如果潛行，使用 onBlockPlaced 中設置的朝向
        }
        else
        {
            // 非玩家放置，默認向下
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        // 移除右鍵切換朝向的功能，保持原版行為
        // 原版漏斗右鍵是打開GUI，我們這裡暫時不做任何事
        return false;
    }

    @Override
    public void addCollisionBoxesToList(net.minecraft.world.World world, int x, int y, int z, net.minecraft.util.AxisAlignedBB aabb, java.util.List list, net.minecraft.entity.Entity entity)
    {
        // 完全模擬原版漏斗的碰撞箱，根據朝向調整漏嘴位置
        int metadata = world.getBlockMetadata(x, y, z);
        int direction = BlockHopper.getDirectionFromMetadata(metadata);

        // 主漏斗體的碰撞箱 (10/16高度)
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);

        // 根據方向設置漏嘴的碰撞箱
        double spoutSize = 0.25; // 4/16
        double spoutLength = 0.25; // 4/16

        switch (direction)
        {
            case 0: // 向下
                this.setBlockBounds(0.375F, -0.25F, 0.375F, 0.625F, 0.0F, 0.625F);
                break;
            case 2: // 向北 (Z-)
                this.setBlockBounds(0.375F, 0.25F, 0.0F, 0.625F, 0.5F, 0.25F);
                break;
            case 3: // 向南 (Z+)
                this.setBlockBounds(0.375F, 0.25F, 0.75F, 0.625F, 0.5F, 1.0F);
                break;
            case 4: // 向西 (X-)
                this.setBlockBounds(0.0F, 0.25F, 0.375F, 0.25F, 0.5F, 0.625F);
                break;
            case 5: // 向東 (X+)
                this.setBlockBounds(0.75F, 0.25F, 0.375F, 1.0F, 0.5F, 0.625F);
                break;
        }

        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);

        // 重置邊界為主體
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world, int x, int y, int z)
    {
        // 設置選擇邊界包含整個漏斗結構
        int metadata = world.getBlockMetadata(x, y, z);
        int direction = BlockHopper.getDirectionFromMetadata(metadata);

        switch (direction)
        {
            case 0: // 向下
                this.setBlockBounds(0.0F, -0.25F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
            case 2: // 向北
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
            case 3: // 向南
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
            case 4: // 向西
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
            case 5: // 向東
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
            default:
                this.setBlockBounds(0.0F, -0.25F, 0.0F, 1.0F, 0.625F, 1.0F);
                break;
        }
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return GoldenHopperRenderer.renderID;
    }
}