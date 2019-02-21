package com.camellias.mysticalmetallurgy.common.block.rack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class RendererRack extends TileEntityRenderer<TileRack>
{
    @Override
    public void render(TileRack te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        ItemStack stack = te.inventory.getStackInSlot(0);
        if (!stack.isEmpty())
        {
            EnumFacing facing = te.getBlockState().get(BlockRack.FACING);
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
            TextureManager textureManager = Minecraft.getInstance().getTextureManager();

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);
            GlStateManager.scalef(0.9F, 0.9F, 0.9F);
            GlStateManager.translated(0.5F, 0.65F, 0.5F);

            switch (facing)
            {
                case NORTH:
                    GlStateManager.rotatef(180F,0.0F, 1.0F, 0.0F);
                    GlStateManager.translatef(-0.125F, 0F, 0.45F);
                    break;
                case EAST:
                    GlStateManager.rotatef(90F,0.0F, 1.0F, 0.0F);
                    GlStateManager.translatef(-0.125F, 0.0F, 0.55F);
                    break;
                case SOUTH:
                    GlStateManager.translatef(0.0F, 0.0F, 0.55F);
                    break;
                case WEST:
                    GlStateManager.rotatef(-90F,0.0F, 1.0F, 0.0F);
                    GlStateManager.translatef(0.0F, 0.0F, 0.45F);
                    break;
            }

            RenderHelper.enableStandardItemLighting();
            renderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }
}
