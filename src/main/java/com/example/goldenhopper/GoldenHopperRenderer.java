package com.example.goldenhopper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class GoldenHopperRenderer implements ISimpleBlockRenderingHandler
{
    public static int renderID;

    public GoldenHopperRenderer()
    {
        renderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this);
    }

    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        // 庫存中不渲染，保持空白如參考代碼
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        if (!(block instanceof BlockGoldenHopper))
            return false;

        BlockGoldenHopper goldenHopper = (BlockGoldenHopper) block;

        // 設置渲染邊界和顏色
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        // 調用渲染方法
        boolean result = renderBlockGoldenHopper(renderer, goldenHopper, x, y, z);

        // 重置渲染邊界和顏色
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        return result;
    }

    public int getRenderId()
    {
        return renderID;
    }

    public boolean renderBlockGoldenHopper(RenderBlocks renderer, BlockGoldenHopper goldenHopper, int x, int y, int z)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(goldenHopper.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));

        float f = 1.0F;
        int colorMultiplier = goldenHopper.colorMultiplier(renderer.blockAccess, x, y, z);
        float f1 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);

        return renderBlockGoldenHopperMetadata(renderer, goldenHopper, x, y, z,
                renderer.blockAccess.getBlockMetadata(x, y, z), false);
    }

    public boolean renderBlockGoldenHopperMetadata(RenderBlocks renderer, BlockGoldenHopper goldenHopper,
                                                   int x, int y, int z, int metadata, boolean isInventory)
    {
        Tessellator tessellator = Tessellator.instance;
        int direction = BlockHopper.getDirectionFromMetadata(metadata);
        double hopperHeight = 0.625D; // 10/16

        // 設置主漏斗體的邊界
        renderer.setRenderBounds(0.0D, hopperHeight, 0.0D, 1.0D, 1.0D, 1.0D);

        if (isInventory)
        {
            // 庫存渲染 - 渲染所有六個面
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderFaceYNeg(goldenHopper, 0.0D, 0.0D, 0.0D, goldenHopper.getIcon(0, metadata));
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(goldenHopper, 0.0D, 0.0D, 0.0D, goldenHopper.getIcon(1, metadata));
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceZNeg(goldenHopper, 0.0D, 0.0D, 0.0D, goldenHopper.getIcon(2, metadata));
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceZPos(goldenHopper, 0.0D, 0.0D, 0.0D, goldenHopper.getIcon(3, metadata));
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceXNeg(goldenHopper, 0.0D, 0.0D, 0.0D, goldenHopper.getIcon(4, metadata));
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceXPos(goldenHopper, 0.0D, 0.0D, 0.0D, goldenHopper.getIcon(5, metadata));
            tessellator.draw();
        }
        else
        {
            // **修復：世界渲染 - 不要手動操作 Tessellator**
            // 使用 renderStandardBlock，它會正確處理 Tessellator 狀態
            renderer.renderStandardBlock(goldenHopper, x, y, z);
        }

        // 設置亮度和顏色（僅世界渲染）
        if (!isInventory)
        {
            tessellator.setBrightness(goldenHopper.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
            float f1 = 1.0F;
            int colorMultiplier = goldenHopper.colorMultiplier(renderer.blockAccess, x, y, z);
            float f4 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
            float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
            float f3 = (colorMultiplier & 0xFF) / 255.0F;

            if (EntityRenderer.anaglyphEnable)
            {
                float f7 = (f4 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float f5 = (f4 * 30.0F + f2 * 70.0F) / 100.0F;
                float f6 = (f4 * 30.0F + f3 * 70.0F) / 100.0F;
                f4 = f7;
                f2 = f5;
                f3 = f6;
            }

            tessellator.setColorOpaque_F(f1 * f4, f1 * f2, f1 * f3);
        }

        // 獲取材質圖標
        IIcon outsideIcon = goldenHopper.getIcon(2, 0);
        IIcon insideIcon = goldenHopper.getHopperInsideIcon();

        float wallThickness = 0.125F;

        // 渲染漏斗壁面
        if (isInventory)
        {
            // 庫存渲染的漏斗壁面
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceXPos(goldenHopper, (-1.0F + wallThickness), 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceXNeg(goldenHopper, (1.0F - wallThickness), 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceZPos(goldenHopper, 0.0D, 0.0D, (-1.0F + wallThickness), outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceZNeg(goldenHopper, 0.0D, 0.0D, (1.0F - wallThickness), outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(goldenHopper, 0.0D, -1.0D + hopperHeight, 0.0D, insideIcon);
            tessellator.draw();
        }
        else
        {
            // **修復：世界渲染 - 使用 renderFace 方法而不是手動操作 Tessellator**
            renderer.renderFaceXPos(goldenHopper, (x - 1.0F + wallThickness), y, z, outsideIcon);
            renderer.renderFaceXNeg(goldenHopper, (x + 1.0F - wallThickness), y, z, outsideIcon);
            renderer.renderFaceZPos(goldenHopper, x, y, (z - 1.0F + wallThickness), outsideIcon);
            renderer.renderFaceZNeg(goldenHopper, x, y, (z + 1.0F - wallThickness), outsideIcon);
            renderer.renderFaceYPos(goldenHopper, x, (y - 1.0F) + hopperHeight, z, insideIcon);
        }

        // 設置材質覆蓋
        renderer.setOverrideBlockTexture(outsideIcon);

        double innerSize1 = 0.25D;
        double innerSize2 = 0.25D;
        renderer.setRenderBounds(innerSize1, innerSize2, innerSize1, 1.0D - innerSize1, hopperHeight - 0.002D, 1.0D - innerSize1);

        // 渲染內部錐形
        if (isInventory)
        {
            // 庫存渲染的內部錐形
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceXPos(goldenHopper, 0.0D, 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceXNeg(goldenHopper, 0.0D, 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceZPos(goldenHopper, 0.0D, 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceZNeg(goldenHopper, 0.0D, 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(goldenHopper, 0.0D, 0.0D, 0.0D, outsideIcon);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderFaceYNeg(goldenHopper, 0.0D, 0.0D, 0.0D, outsideIcon);
            tessellator.draw();
        }
        else
        {
            // **修復：世界渲染 - 使用 renderStandardBlock 而不是手動操作 Tessellator**
            renderer.renderStandardBlock(goldenHopper, x, y, z);
        }

        // 渲染漏嘴（僅世界渲染）
        if (!isInventory)
        {
            double spoutSize = 0.375D;
            double spoutLength = 0.25D;

            renderer.setOverrideBlockTexture(outsideIcon);

            if (direction == 0) // 向下
            {
                renderer.setRenderBounds(spoutSize, 0.0D, spoutSize, 1.0D - spoutSize, 0.25D, 1.0D - spoutSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 2) // 向北
            {
                renderer.setRenderBounds(spoutSize, innerSize2, 0.0D, 1.0D - spoutSize, innerSize2 + spoutLength, innerSize1);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 3) // 向南
            {
                renderer.setRenderBounds(spoutSize, innerSize2, 1.0D - innerSize1, 1.0D - spoutSize, innerSize2 + spoutLength, 1.0D);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 4) // 向西
            {
                renderer.setRenderBounds(0.0D, innerSize2, spoutSize, innerSize1, innerSize2 + spoutLength, 1.0D - spoutSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 5) // 向東
            {
                renderer.setRenderBounds(1.0D - innerSize1, innerSize2, spoutSize, 1.0D, innerSize2 + spoutLength, 1.0D - spoutSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
        }

        // 清除材質覆蓋
        renderer.clearOverrideBlockTexture();

        return true;
    }

    public boolean shouldRender3DInInventory(int modelId)
    {
        return false; // 與參考代碼保持一致
    }
}