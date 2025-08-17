package com.example.goldenhopper;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class TileEntityGoldenHopper extends TileEntity implements IInventory, ISidedInventory
{
    private ItemStack[] hopperItemStacks = new ItemStack[5];
    private String customName;
    private int transferCooldown = 0; // 初始化為0而不是-1

    // 黃金漏斗每tick轉移一個物品（比原版快5倍）
    private static final int TRANSFER_COOLDOWN = 1;

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        this.hopperItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.hopperItemStacks.length)
            {
                this.hopperItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.transferCooldown = nbt.getInteger("TransferCooldown");

        if (nbt.hasKey("CustomName", 8))
        {
            this.customName = nbt.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.hopperItemStacks.length; ++i)
        {
            if (this.hopperItemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.hopperItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbt.setTag("Items", nbttaglist);
        nbt.setInteger("TransferCooldown", this.transferCooldown);

        if (this.hasCustomInventoryName())
        {
            nbt.setString("CustomName", this.customName);
        }
    }

    @Override
    public void updateEntity()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            --this.transferCooldown;

            if (!this.isCoolingDown())
            {
                this.setTransferCooldown(0);
                this.updateHopper();
            }
        }
    }

    private boolean updateHopper()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            if (!this.isCoolingDown())
            {
                boolean flag = false;

                if (!this.isEmpty())
                {
                    flag = this.transferItemsOut();
                }

                if (!this.isFull())
                {
                    flag = suckItemsIntoHopper(this) || flag;
                }

                if (flag)
                {
                    this.setTransferCooldown(TRANSFER_COOLDOWN);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean isEmpty()
    {
        for (ItemStack itemstack : this.hopperItemStacks)
        {
            if (itemstack != null)
            {
                return false;
            }
        }
        return true;
    }

    private boolean isFull()
    {
        for (ItemStack itemstack : this.hopperItemStacks)
        {
            if (itemstack == null || itemstack.stackSize < itemstack.getMaxStackSize())
            {
                return false;
            }
        }
        return true;
    }

    private boolean transferItemsOut()
    {
        IInventory targetInventory = this.getInventoryForHopperTransfer();

        if (targetInventory == null)
        {
            return false;
        }
        else
        {
            int facing = this.getBlockMetadata() & 7;
            // 修正面向邏輯
            int insertSide = -1;
            if (facing == 0) // 向下
            {
                insertSide = 1; // 底面
            }
            else if (facing >= 2 && facing <= 5) // 水平方向
            {
                insertSide = facing; // 對應的側面
            }

            if (this.isInventoryFull(targetInventory, insertSide))
            {
                return false;
            }
            else
            {
                for (int i = 0; i < this.getSizeInventory(); ++i)
                {
                    if (this.getStackInSlot(i) != null)
                    {
                        ItemStack originalStack = this.getStackInSlot(i).copy();
                        ItemStack stackToTransfer = this.decrStackSize(i, 1);
                        ItemStack remainingStack = insertStack(targetInventory, stackToTransfer, insertSide);

                        if (remainingStack == null || remainingStack.stackSize == 0)
                        {
                            targetInventory.markDirty();
                            return true;
                        }

                        // 如果無法插入，恢復原始物品
                        this.setInventorySlotContents(i, originalStack);
                    }
                }

                return false;
            }
        }
    }

    private static boolean suckItemsIntoHopper(TileEntityGoldenHopper hopper)
    {
        IInventory sourceInventory = getSourceInventory(hopper);

        if (sourceInventory != null)
        {
            byte inputSide = 0; // 從底面（side 0）抽取物品

            if (sourceInventory instanceof ISidedInventory)
            {
                ISidedInventory sidedInventory = (ISidedInventory)sourceInventory;
                int[] accessibleSlots = sidedInventory.getAccessibleSlotsFromSide(inputSide);

                for (int slot : accessibleSlots)
                {
                    if (pullItemFromSlot(hopper, sourceInventory, slot, inputSide))
                    {
                        return true;
                    }
                }
            }
            else
            {
                int inventorySize = sourceInventory.getSizeInventory();

                for (int slot = 0; slot < inventorySize; ++slot)
                {
                    if (pullItemFromSlot(hopper, sourceInventory, slot, inputSide))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            // 嘗試吸取地面物品
            List<EntityItem> entityItems = getItemsAboveHopper(hopper);

            if (!entityItems.isEmpty())
            {
                return captureDroppedItems(hopper, entityItems.get(0));
            }
        }

        return false;
    }

    private static boolean pullItemFromSlot(TileEntityGoldenHopper hopper, IInventory inventory, int slot, int side)
    {
        ItemStack itemstack = inventory.getStackInSlot(slot);

        if (itemstack != null && canExtractItemFromSlot(inventory, itemstack, slot, side))
        {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = insertStack(hopper, inventory.decrStackSize(slot, 1), -1);

            if (itemstack2 == null || itemstack2.stackSize == 0)
            {
                inventory.markDirty();
                return true;
            }

            inventory.setInventorySlotContents(slot, itemstack1);
        }

        return false;
    }

    public static boolean captureDroppedItems(TileEntityGoldenHopper hopper, EntityItem entityItem)
    {
        boolean flag = false;

        if (entityItem == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = entityItem.getEntityItem().copy();
            ItemStack itemstack1 = insertStack(hopper, itemstack, -1);

            if (itemstack1 != null && itemstack1.stackSize != 0)
            {
                entityItem.setEntityItemStack(itemstack1);
            }
            else
            {
                flag = true;
                entityItem.setDead();
            }

            return flag;
        }
    }

    public static ItemStack insertStack(IInventory inventory, ItemStack stack, int side)
    {
        if (inventory instanceof ISidedInventory && side > -1)
        {
            ISidedInventory isidedinventory = (ISidedInventory)inventory;
            int[] aint = isidedinventory.getAccessibleSlotsFromSide(side);

            for (int l = 0; l < aint.length && stack != null && stack.stackSize > 0; ++l)
            {
                stack = insertStack(inventory, stack, aint[l], side);
            }
        }
        else
        {
            int i = inventory.getSizeInventory();

            for (int j = 0; j < i && stack != null && stack.stackSize > 0; ++j)
            {
                stack = insertStack(inventory, stack, j, side);
            }
        }

        if (stack != null && stack.stackSize == 0)
        {
            stack = null;
        }

        return stack;
    }

    private static boolean canInsertItemInSlot(IInventory inventory, ItemStack stack, int slot, int side)
    {
        if (!inventory.isItemValidForSlot(slot, stack))
        {
            return false;
        }

        // 特殊處理：熔爐的槽位規則
        if (inventory instanceof net.minecraft.tileentity.TileEntityFurnace)
        {
            // slot 0: 輸入槽（可燒製的物品）
            // slot 1: 燃料槽（燃料物品）
            // slot 2: 輸出槽（只能取出，不能放入）

            if (slot == 2) // 輸出槽不允許放入
            {
                return false;
            }
            else if (slot == 1) // 燃料槽：檢查是否為燃料
            {
                return net.minecraft.tileentity.TileEntityFurnace.isItemFuel(stack);
            }
            else if (slot == 0) // 輸入槽：檢查是否可燒製
            {
                // 檢查是否有對應的熔煉配方
                return net.minecraft.item.crafting.FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
            }
        }

        // 特殊處理：釀造台的槽位規則
        if (inventory instanceof net.minecraft.tileentity.TileEntityBrewingStand)
        {
            // slot 0,1,2: 藥水槽（放置藥水瓶）
            // slot 3: 材料槽（放置釀造材料）

            if (slot >= 0 && slot <= 2) // 藥水槽
            {
                // 只允許放入藥水瓶相關物品
                return stack.getItem() == net.minecraft.init.Items.potionitem ||
                        stack.getItem() == net.minecraft.init.Items.glass_bottle;
            }
            else if (slot == 3) // 材料槽
            {
                // 檢查是否為有效的釀造材料
                return isValidBrewingIngredient(stack);
            }
        }

        // ISidedInventory 檢查
        if (inventory instanceof ISidedInventory)
        {
            return ((ISidedInventory)inventory).canInsertItem(slot, stack, side);
        }

        return true;
    }

    /**
     * 檢查物品是否為有效的釀造材料
     * 在 Minecraft 1.7.10 中的常見釀造材料
     */
    private static boolean isValidBrewingIngredient(ItemStack stack)
    {
        if (stack == null) return false;

        // 常見的釀造材料
        return stack.getItem() == net.minecraft.init.Items.nether_wart ||
                stack.getItem() == net.minecraft.init.Items.redstone ||
                stack.getItem() == net.minecraft.init.Items.glowstone_dust ||
                stack.getItem() == net.minecraft.init.Items.gunpowder ||
                stack.getItem() == net.minecraft.init.Items.golden_carrot ||
                stack.getItem() == net.minecraft.init.Items.magma_cream ||
                stack.getItem() == net.minecraft.init.Items.blaze_powder ||
                stack.getItem() == net.minecraft.init.Items.ghast_tear ||
                stack.getItem() == net.minecraft.init.Items.spider_eye ||
                stack.getItem() == net.minecraft.init.Items.fermented_spider_eye ||
                stack.getItem() == net.minecraft.init.Items.sugar ||
                stack.getItem() == Items.speckled_melon ||
                stack.getItem() == net.minecraft.init.Items.speckled_melon;
    }

    private static boolean canExtractItemFromSlot(IInventory inventory, ItemStack stack, int slot, int side)
    {
        // ISidedInventory 檢查 - 但要先處理我們的特殊邏輯
        if (inventory instanceof ISidedInventory)
        {
            ISidedInventory sidedInv = (ISidedInventory)inventory;

            // 首先檢查該槽位是否可從該面存取
            int[] accessibleSlots = sidedInv.getAccessibleSlotsFromSide(side);
            boolean slotAccessible = false;
            for (int accessibleSlot : accessibleSlots)
            {
                if (accessibleSlot == slot)
                {
                    slotAccessible = true;
                    break;
                }
            }

            if (!slotAccessible)
            {
                return false;
            }

            // 然後檢查是否可以提取
            return sidedInv.canExtractItem(slot, stack, side);
        }

        return true;
    }

    private static ItemStack insertStack(IInventory inventory, ItemStack stack, int slot, int side)
    {
        ItemStack itemstack1 = inventory.getStackInSlot(slot);

        if (canInsertItemInSlot(inventory, stack, slot, side))
        {
            boolean flag = false;

            if (itemstack1 == null)
            {
                inventory.setInventorySlotContents(slot, stack);
                stack = null;
                flag = true;
            }
            else if (canCombine(itemstack1, stack))
            {
                int i = stack.getMaxStackSize() - itemstack1.stackSize;
                int j = Math.min(stack.stackSize, i);
                stack.stackSize -= j;
                itemstack1.stackSize += j;
                flag = j > 0;
            }

            if (flag)
            {
                if (inventory instanceof TileEntityGoldenHopper)
                {
                    TileEntityGoldenHopper tileentityhopper1 = (TileEntityGoldenHopper)inventory;

                    if (tileentityhopper1.mayTransfer())
                    {
                        tileentityhopper1.setTransferCooldown(TRANSFER_COOLDOWN);
                    }

                    inventory.markDirty();
                }

                inventory.markDirty();
            }
        }

        return stack;
    }

    private IInventory getInventoryForHopperTransfer()
    {
        int i = this.xCoord;
        int j = this.yCoord;
        int k = this.zCoord;
        int metadata = this.getBlockMetadata();

        switch (metadata & 7)
        {
            case 0: // 向下
                --j;
                break;
            case 2: // 向北
                --k;
                break;
            case 3: // 向南
                ++k;
                break;
            case 4: // 向西
                --i;
                break;
            case 5: // 向東
                ++i;
                break;
        }

        return getInventoryAtLocation(this.worldObj, i, j, k);
    }

    public static IInventory getSourceInventory(TileEntityGoldenHopper hopper)
    {
        return getInventoryAtLocation(hopper.getWorldObj(), hopper.xCoord, hopper.yCoord + 1.0D, hopper.zCoord);
    }

    /**
     * 獲取指定位置的容器（替代 TileEntityHopper.getInventoryAtLocation）
     */
    public static IInventory getInventoryAtLocation(World world, double x, double y, double z)
    {
        IInventory inventory = null;
        int blockX = MathHelper.floor_double(x);
        int blockY = MathHelper.floor_double(y);
        int blockZ = MathHelper.floor_double(z);
        TileEntity tileEntity = world.getTileEntity(blockX, blockY, blockZ);

        if (tileEntity != null && tileEntity instanceof IInventory)
        {
            inventory = (IInventory)tileEntity;

            // 處理雙箱子的情況
            if (inventory instanceof net.minecraft.tileentity.TileEntityChest)
            {
                net.minecraft.block.Block block = world.getBlock(blockX, blockY, blockZ);

                if (block instanceof net.minecraft.block.BlockChest)
                {
                    inventory = ((net.minecraft.block.BlockChest)block).func_149951_m(world, blockX, blockY, blockZ);
                }
            }
        }

        // 檢查是否有實體容器（如礦車箱子）
        if (inventory == null)
        {
            List entityList = world.getEntitiesWithinAABBExcludingEntity(null,
                    AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));

            if (entityList != null && entityList.size() > 0)
            {
                for (Object entity : entityList)
                {
                    if (entity instanceof IInventory)
                    {
                        inventory = (IInventory)entity;
                        break;
                    }
                }
            }
        }

        return inventory;
    }

    public static List<EntityItem> getItemsAboveHopper(TileEntityGoldenHopper hopper)
    {
        return hopper.worldObj.getEntitiesWithinAABB(EntityItem.class,
                AxisAlignedBB.getBoundingBox(hopper.xCoord, hopper.yCoord + 1.0D, hopper.zCoord,
                        hopper.xCoord + 1.0D, hopper.yCoord + 2.0D, hopper.zCoord + 1.0D));
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && stack1.stackSize <= stack1.getMaxStackSize() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    private boolean isInventoryFull(IInventory inventory, int side)
    {
        if (inventory instanceof ISidedInventory)
        {
            ISidedInventory isidedinventory = (ISidedInventory)inventory;
            int[] aint = isidedinventory.getAccessibleSlotsFromSide(side);

            for (int l = 0; l < aint.length; ++l)
            {
                ItemStack itemstack1 = isidedinventory.getStackInSlot(aint[l]);

                if (itemstack1 == null || itemstack1.stackSize != itemstack1.getMaxStackSize())
                {
                    return false;
                }
            }
        }
        else
        {
            int i = inventory.getSizeInventory();

            for (int j = 0; j < i; ++j)
            {
                ItemStack itemstack = inventory.getStackInSlot(j);

                if (itemstack == null || itemstack.stackSize != itemstack.getMaxStackSize())
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean mayTransfer()
    {
        return this.transferCooldown <= 0;
    }

    public boolean isCoolingDown()
    {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int cooldown)
    {
        this.transferCooldown = cooldown;
    }

    // IInventory 實現
    @Override
    public int getSizeInventory()
    {
        return this.hopperItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.hopperItemStacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (this.hopperItemStacks[slot] != null)
        {
            ItemStack itemstack;

            if (this.hopperItemStacks[slot].stackSize <= amount)
            {
                itemstack = this.hopperItemStacks[slot];
                this.hopperItemStacks[slot] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.hopperItemStacks[slot].splitStack(amount);

                if (this.hopperItemStacks[slot].stackSize == 0)
                {
                    this.hopperItemStacks[slot] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.hopperItemStacks[slot] != null)
        {
            ItemStack itemstack = this.hopperItemStacks[slot];
            this.hopperItemStacks[slot] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.hopperItemStacks[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.customName : "container.goldenhopper";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String customName)
    {
        this.customName = customName;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
                player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    // ISidedInventory 實現
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        // 所有槽位都可以從任何方向存取
        return new int[] {0, 1, 2, 3, 4};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return true;
    }
}