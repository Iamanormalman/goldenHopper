package com.example.morehoppers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiDiamondHopper extends GuiContainer
{
    private static final ResourceLocation hopperGuiTextures = new ResourceLocation(MoreHoppersMod.MODID + ":textures/gui/container/diamond_hopper.png");
    private final TileEntityDiamondHopper hopperInventory;
    private final InventoryPlayer playerInventory;

    public GuiDiamondHopper(InventoryPlayer playerInventory, TileEntityDiamondHopper hopperInventory)
    {
        super(new ContainerDiamondHopper(playerInventory, hopperInventory));
        this.playerInventory = playerInventory;
        this.hopperInventory = hopperInventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.hopperInventory.hasCustomInventoryName()
                ? this.hopperInventory.getInventoryName()
                : I18n.format(this.hopperInventory.getInventoryName());

        this.fontRendererObj.drawString(s, 8, 6, 4210752);

        this.fontRendererObj.drawString(
                this.playerInventory.hasCustomInventoryName()
                        ? this.playerInventory.getInventoryName()
                        : I18n.format(this.playerInventory.getInventoryName()),
                8, this.ySize - 96 + 2, 4210752
        );
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(hopperGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}