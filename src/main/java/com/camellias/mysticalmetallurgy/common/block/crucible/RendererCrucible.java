package com.camellias.mysticalmetallurgy.common.block.crucible;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RendererCrucible extends TileEntitySpecialRenderer<TileCrucible>
{
    @Override
    public void render(TileCrucible te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        for(int slot = 0; slot < TileCrucible.INPUT_SLOTS; slot++)
        {
            GlStateManager.pushMatrix();
            //Translate into the centre of the inner crucible, above the bottom
            GlStateManager.translate(x + 0.45F + 0.075F * slot, y + 0.6F, z + 0.5F);
            //make them diagonal
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(50.0F, 1.0F, 1.0F, 0.0F);
            //make them fit
            GlStateManager.scale(0.3F, 0.3F, 0.3F);
            Minecraft.getMinecraft().getRenderItem().renderItem(te.input.getStackInSlot(slot), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }
}
