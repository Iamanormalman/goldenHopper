package com.example.morehoppers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * 所有自訂漏斗的基礎類別
 */
public abstract class BlockBaseHopper extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    protected IIcon topIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon insideIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon outsideIcon;

    private final Random hopperRandom = new Random();

    public BlockBaseHopper()
    {
        super(Material.iron);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setStepSound(soundTypeMetal);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * 子類必須實現此方法以提供適當的材質前綴
     */
    protected abstract String getTexturePrefix();

    /**
     * 子類必須實現此方法以提供適當的 GUI ID
     */
    protected abstract int getGuiId();

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        String prefix = getTexturePrefix();
        this.topIcon = iconRegister.registerIcon(MoreHoppersMod.MODID + ":" + prefix + "_top");
        this.insideIcon = iconRegister.registerIcon(MoreHoppersMod.MODID + ":" + prefix + "_inside");
        this.outsideIcon = iconRegister.registerIcon(MoreHoppersMod.MODID + ":" + prefix + "_outside");
        this.blockIcon = iconRegister.registerIcon(MoreHoppersMod.MODID + ":" + prefix);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 1 ? this.topIcon : this.outsideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        return this.getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    public IIcon getHopperInsideIcon()
    {
        return this.insideIcon;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        return side;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        if (placer instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) placer;
            int placementSide = world.getBlockMetadata(x, y, z);

            if (player.isSneaking() && isValidSideForDirectionalPlacement(placementSide))
            {
                int hopperMetadata = getOppositeDirection(placementSide);
                world.setBlockMetadataWithNotify(x, y, z, hopperMetadata, 2);
            }
            else
            {
                world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            }

            if (stack.hasDisplayName())
            {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof IInventory)
                {
                    // 假設所有漏斗 TileEntity 都有 setCustomName 方法
                    if (tileEntity instanceof TileEntityGoldenHopper)
                    {
                        ((TileEntityGoldenHopper)tileEntity).setCustomName(stack.getDisplayName());
                    }
                    else if (tileEntity instanceof TileEntityDiamondHopper)
                    {
                        ((TileEntityDiamondHopper)tileEntity).setCustomName(stack.getDisplayName());
                    }
                }
            }
        }
        else
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }
    }

    private boolean isValidSideForDirectionalPlacement(int side)
    {
        return side >= 2 && side <= 5;
    }

    private int getOppositeDirection(int side)
    {
        switch (side)
        {
            case 2: return 3;
            case 3: return 2;
            case 4: return 5;
            case 5: return 4;
            default: return 0;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof IInventory) {
                player.openGui(MoreHoppersMod.instance, getGuiId(), world, x, y, z);
            }
        }
        return true;
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int metadata)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof IInventory)
        {
            IInventory hopperTileEntity = (IInventory)tileEntity;

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
                        EntityItem entityitem = new EntityItem(world, (float)x + f, (float)y + f1, (float)z + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (float)this.hopperRandom.nextGaussian() * f3;
                        entityitem.motionY = (float)this.hopperRandom.nextGaussian() * f3 + 0.2F;
                        entityitem.motionZ = (float)this.hopperRandom.nextGaussian() * f3;
                        world.spawnEntityInWorld(entityitem);
                    }
                }
            }

            world.func_147453_f(x, y, z, this);
        }

        super.onBlockPreDestroy(world, x, y, z, metadata);
    }

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
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    @Override
    public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world, int x, int y, int z)
    {
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
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, net.minecraftforge.common.util.ForgeDirection side)
    {
        return side == net.minecraftforge.common.util.ForgeDirection.UP;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public int getRenderType()
    {
        return UniversalHopperRenderer.renderID;
    }
}