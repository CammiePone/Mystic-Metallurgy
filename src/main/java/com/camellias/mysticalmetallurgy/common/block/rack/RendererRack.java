package com.camellias.mysticalmetallurgy.common.block.rack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class RendererRack extends TileEntitySpecialRenderer<TileRack>
{

    @Override
    public void render(TileRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        ItemStack stack = te.inventory.getStackInSlot(0);
        if (!stack.isEmpty())
        {
            EnumFacing facing = te.getBlockState().getValue(BlockRack.FACING);
            RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(0.9F, 0.9F, 0.9F);
            GlStateManager.translate(0.5F, 0.65F, 0.5F);

            switch (facing)
            {
                case NORTH:
                    GlStateManager.rotate(180F,0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.125F, 0F, 0.45F);
                    break;
                case EAST:
                    GlStateManager.rotate(90F,0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.125F, 0.0F, 0.55F);
                    break;
                case SOUTH:
                    GlStateManager.translate(0.0F, 0.0F, 0.55F);
                    break;
                case WEST:
                    GlStateManager.rotate(-90F,0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(0.0F, 0.0F, 0.45F);
                    break;
            }

            RenderHelper.enableStandardItemLighting();
            renderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }
}
