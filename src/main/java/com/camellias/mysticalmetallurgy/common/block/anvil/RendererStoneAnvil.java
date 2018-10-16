package com.camellias.mysticalmetallurgy.common.block.anvil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

public class RendererStoneAnvil extends TileEntitySpecialRenderer<TileStoneAnvil>
{
    @Override
    public void render(TileStoneAnvil te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        ItemStack print = te.extractPrint(true);
        if (!print.isEmpty())
        {
            GlStateManager.pushMatrix();
            //Translate into the centre of the inner crucible, above the bottom
            GlStateManager.translate(x + 0.5F + 0.075F, y + 0.435F, z + 0.3F);
            //make them diagonal
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(240.0F, 0.0F, 0.0F, 1.0F);
            //make them fit
            GlStateManager.scale(0.2F, 0.2F, 0.2F);
            itemRenderer.renderItem(print, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
        ItemStack metal = te.extractMetal(true);
        if (!metal.isEmpty())
        {
            GlStateManager.pushMatrix();
            //Translate into the centre of the inner crucible, above the bottom
            GlStateManager.translate(x + 0.5F + 0.075F, y + 0.435F, z + 0.55F);
            //make them diagonal
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(270.0F, 0.0F, 0.0F, 1.0F);
            //make them fit
            GlStateManager.scale(0.2F, 0.2F, 0.2F);
            itemRenderer.renderItem(metal, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
        ItemStack stick = te.extractStick(true);
        if (!stick.isEmpty())
        {
            GlStateManager.pushMatrix();
            //Translate into the centre of the inner crucible, above the bottom
            GlStateManager.translate(x + 0.4F, y + 0.435F, z + 0.65F);
            //make them diagonal
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            //GlStateManager.rotate(240.0F, 0.0F, 0.0F, 1.0F);
            //make them fit
            GlStateManager.scale(0.2F, 0.2F, 0.2F);
            itemRenderer.renderItem(stick, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }
}
