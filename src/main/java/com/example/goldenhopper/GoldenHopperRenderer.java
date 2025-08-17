package com.example.goldenhopper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
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
        // 庫存中渲染Golden Hopper
        if (!(block instanceof BlockGoldenHopper))
            return;

        BlockGoldenHopper goldenHopper = (BlockGoldenHopper) block;

        Tessellator tessellator = Tessellator.instance;
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        // 渲染庫存中的Golden Hopper，metadata設為0（向下）
        renderBlockGoldenHopperMetadata(renderer, goldenHopper, 0, 0, 0, 0, true);
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        if (!(block instanceof BlockGoldenHopper))
            return false;

        BlockGoldenHopper goldenHopper = (BlockGoldenHopper) block;
        renderBlockGoldenHopper(renderer, goldenHopper, x, y, z);

        // 重置渲染邊界
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        return true;
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

        // 獲取方塊的metadata（決定漏斗朝向）
        int metadata = renderer.blockAccess.getBlockMetadata(x, y, z);

        return renderBlockGoldenHopperMetadata(renderer, goldenHopper, x, y, z, metadata, false);
    }

    public boolean renderBlockGoldenHopperMetadata(RenderBlocks renderer, BlockGoldenHopper goldenHopper, int x, int y, int z, int metadata, boolean isInventory)
    {
        Tessellator tessellator = Tessellator.instance;

        // 使用與原版漏斗相同的朝向邏輯
        int direction = BlockHopper.getDirectionFromMetadata(metadata);
        double hopperHeight = 0.625D; // 10/16 方塊高度

        // 設置主漏斗體的邊界
        renderer.setRenderBounds(0.0D, hopperHeight, 0.0D, 1.0D, 1.0D, 1.0D);

        // 庫存渲染 - 使用簡化的渲染方式
        if (isInventory)
        {
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
            // 世界渲染
            renderer.renderStandardBlock(goldenHopper, x, y, z);
        }

        // 設置顏色和亮度
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

        // 獲取Golden Hopper的材質圖標（關鍵修改點）
        IIcon outsideIcon = goldenHopper.getIcon(2, 0); // 使用我們的outside材質
        IIcon insideIcon = goldenHopper.getHopperInsideIcon(); // 使用我們的inside材質

        float wallThickness = 0.125F; // 2/16 的壁厚

        // 渲染漏斗壁面
        if (isInventory)
        {
            // 庫存中渲染漏斗壁面
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
            // 世界中渲染漏斗壁面
            renderer.renderFaceXPos(goldenHopper, (x - 1.0F + wallThickness), y, z, outsideIcon);
            renderer.renderFaceXNeg(goldenHopper, (x + 1.0F - wallThickness), y, z, outsideIcon);
            renderer.renderFaceZPos(goldenHopper, x, y, (z - 1.0F + wallThickness), outsideIcon);
            renderer.renderFaceZNeg(goldenHopper, x, y, (z + 1.0F - wallThickness), outsideIcon);
            renderer.renderFaceYPos(goldenHopper, x, (y - 1.0F) + hopperHeight, z, insideIcon);
        }

        // 設置當前圖標為outside材質
        renderer.setOverrideBlockTexture(outsideIcon);

        // 渲染漏斗內部錐形
        double innerTopSize = 0.25D; // 4/16
        double innerBottomSize = 0.25D; // 4/16
        renderer.setRenderBounds(innerTopSize, innerBottomSize, innerTopSize, 1.0D - innerTopSize, hopperHeight - 0.002D, 1.0D - innerTopSize);

        if (isInventory)
        {
            // 庫存中渲染內部錐形的六個面
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
            renderer.renderStandardBlock(goldenHopper, x, y, z);
        }

        // 渲染漏嘴（根據朝向）
        if (!isInventory)
        {
            double spoutSize = 0.375D; // 6/16
            double spoutLength = 0.25D; // 4/16

            renderer.setOverrideBlockTexture(outsideIcon);

            // 根據朝向渲染漏嘴
            if (direction == 0) // 向下
            {
                renderer.setRenderBounds(spoutSize, 0.0D, spoutSize, 1.0D - spoutSize, 0.25D, 1.0D - spoutSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 2) // 向北
            {
                renderer.setRenderBounds(spoutSize, innerBottomSize, 0.0D, 1.0D - spoutSize, innerBottomSize + spoutLength, innerTopSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 3) // 向南
            {
                renderer.setRenderBounds(spoutSize, innerBottomSize, 1.0D - innerTopSize, 1.0D - spoutSize, innerBottomSize + spoutLength, 1.0D);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 4) // 向西
            {
                renderer.setRenderBounds(0.0D, innerBottomSize, spoutSize, innerTopSize, innerBottomSize + spoutLength, 1.0D - spoutSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
            else if (direction == 5) // 向東
            {
                renderer.setRenderBounds(1.0D - innerTopSize, innerBottomSize, spoutSize, 1.0D, innerBottomSize + spoutLength, 1.0D - spoutSize);
                renderer.renderStandardBlock(goldenHopper, x, y, z);
            }
        }

        // 清除材質覆蓋
        renderer.clearOverrideBlockTexture();

        return true;
    }

    public boolean shouldRender3DInInventory(int modelId)
    {
        return true; // 改為true以在庫存中顯示3D模型
    }
}