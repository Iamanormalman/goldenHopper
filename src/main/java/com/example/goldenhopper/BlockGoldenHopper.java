package com.example.goldenhopper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockGoldenHopper extends BlockContainer
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
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityGoldenHopper();
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
        // 將點擊的面信息存儲在metadata中，稍後在onBlockPlacedBy中使用
        // 我們使用metadata的高位來臨時存儲side信息
        return side;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        if (placer instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) placer;

            // 獲取從onBlockPlaced傳遞來的面信息
            int placementSide = world.getBlockMetadata(x, y, z);

            if (player.isSneaking() && isValidSideForDirectionalPlacement(placementSide))
            {
                // 潛行且對著側面放置：漏嘴朝向該側面的相對面
                int hopperMetadata = getOppositeDirection(placementSide);
                world.setBlockMetadataWithNotify(x, y, z, hopperMetadata, 2);
            }
            else
            {
                // 不潛行或其他情況：默認向下
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            }

            // 設置自定義名稱（如果物品有自定義名稱）
            if (stack.hasDisplayName())
            {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityGoldenHopper)
                {
                    ((TileEntityGoldenHopper)tileEntity).setCustomName(stack.getDisplayName());
                }
            }
        }
        else
        {
            // 非玩家放置，默認向下
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }
    }

    /**
     * 檢查是否為有效的側面（不是頂面或底面）
     */
    private boolean isValidSideForDirectionalPlacement(int side)
    {
        return side >= 2 && side <= 5; // 2,3,4,5 分別是北、南、西、東面
    }

    /**
     * 獲取相對面的方向
     * 對著北面放置 → 漏嘴朝南 (2 → 3)
     * 對著南面放置 → 漏嘴朝北 (3 → 2)
     * 對著西面放置 → 漏嘴朝東 (4 → 5)
     * 對著東面放置 → 漏嘴朝西 (5 → 4)
     */
    private int getOppositeDirection(int side)
    {
        switch (side)
        {
            case 2: // 北面 → 漏嘴朝南
                return 3;
            case 3: // 南面 → 漏嘴朝北
                return 2;
            case 4: // 西面 → 漏嘴朝東
                return 5;
            case 5: // 東面 → 漏嘴朝西
                return 4;
            default:
                return 0; // 默認向下
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityGoldenHopper)
            {
                player.openGui(GoldenHopper.instance, GuiHandler.GOLDEN_HOPPER_GUI_ID, world, x, y, z);
            }
            return true;
        }
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int metadata)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityGoldenHopper)
        {
            TileEntityGoldenHopper hopperTileEntity = (TileEntityGoldenHopper)tileEntity;

            for (int i = 0; i < hopperTileEntity.getSizeInventory(); ++i)
            {
                ItemStack itemstack = hopperTileEntity.getStackInSlot(i);

                if (itemstack != null)
                {
                    float f = this.hopperRandom.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.hopperRandom.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.hopperRandom.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int j = this.hopperRandom.nextInt(21) + 10;

                        if (j > itemstack.stackSize)
                        {
                            j = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j;
                        EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.hopperRandom.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.hopperRandom.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.hopperRandom.nextGaussian() * f3);
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }

            world.func_147453_f(x, y, z, this);
        }

        super.onBlockPreDestroy(world, x, y, z, metadata);
    }

    private Random hopperRandom = new Random();

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
        return Container.calcRedstoneFromInventory((IInventory)world.getTileEntity(x, y, z));
    }

    @Override
    public void addCollisionBoxesToList(net.minecraft.world.World world, int x, int y, int z, net.minecraft.util.AxisAlignedBB aabb, java.util.List list, net.minecraft.entity.Entity entity)
    {
        // 設置為完整一格高的碰撞箱
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    @Override
    public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world, int x, int y, int z)
    {
        // 設置選擇邊界為完整一格
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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