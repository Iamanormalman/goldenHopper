package com.example.morehoppers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDiamondHopper extends Container
{
    private final TileEntityDiamondHopper hopperInventory;

    public ContainerDiamondHopper(InventoryPlayer playerInventory, TileEntityDiamondHopper hopperInventory)
    {
        this.hopperInventory = hopperInventory;
        hopperInventory.openInventory();

        // 添加黃金漏斗的5個槽位（橫排）
        for (int i = 0; i < 5; ++i)
        {
            this.addSlotToContainer(new Slot(hopperInventory, i, 44 + i * 18, 20));
        }

        // 添加玩家背包槽位
        for (int l = 0; l < 3; ++l)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
            }
        }

        // 添加玩家快捷欄槽位
        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 109));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.hopperInventory.isUseableByPlayer(player);
    }

    /**
     * 處理 Shift+點擊 轉移物品的邏輯
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex < this.hopperInventory.getSizeInventory())
            {
                // 從漏斗槽位轉移到玩家背包
                if (!this.mergeItemStack(itemstack1, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.hopperInventory.getSizeInventory(), false))
            {
                // 從玩家背包轉移到漏斗槽位
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * 當容器關閉時調用
     */
    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        this.hopperInventory.closeInventory();
    }
}