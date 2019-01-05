package com.camellias.mysticalmetallurgy.common.block.rack;

import com.camellias.mysticalmetallurgy.api.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.asm.transformers.ItemStackTransformer;
import org.lwjgl.opengl.GL11;

public class RendererRack extends TileEntitySpecialRenderer<TileRack>
{

    @Override
    public void render(TileRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        ItemStack stack = te.inventory.getStackInSlot(0);
        if (!stack.isEmpty())
        {
            RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.pushMatrix();

            RenderHelper.enableStandardItemLighting();

            //GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            //RenderUtils.rotateOnFacing(facing);
            //GlStateManager.translate(offset.x, offset.y, offset.z);
            renderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }
}
