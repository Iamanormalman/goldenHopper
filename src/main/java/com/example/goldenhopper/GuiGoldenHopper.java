package com.example.goldenhopper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiGoldenHopper extends GuiContainer
{
    private static final ResourceLocation hopperGuiTextures = new ResourceLocation(GoldenHopper.MODID + ":textures/gui/container/golden_hopper.png");
    private TileEntityGoldenHopper hopperInventory;
    private InventoryPlayer playerInventory;

    public GuiGoldenHopper(InventoryPlayer playerInventory, TileEntityGoldenHopper hopperInventory)
    {
        super(new ContainerGoldenHopper(playerInventory, hopperInventory));
        this.playerInventory = playerInventory;
        this.hopperInventory = hopperInventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    /**
     * 畫出前景層（文字等）
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.hopperInventory.hasCustomInventoryName() ? this.hopperInventory.getInventoryName() : I18n.format(this.hopperInventory.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.hasCustomInventoryName() ? this.playerInventory.getInventoryName() : I18n.format(this.playerInventory.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * 畫出背景層（容器背景）
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(hopperGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}