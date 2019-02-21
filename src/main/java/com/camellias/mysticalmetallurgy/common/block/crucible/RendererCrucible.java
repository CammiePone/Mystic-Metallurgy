package com.camellias.mysticalmetallurgy.common.block.crucible;

import com.camellias.mysticalmetallurgy.library.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.fluids.FluidStack;

public class RendererCrucible extends TileEntityRenderer<TileCrucible>
{
    @Override
    public void render(TileCrucible te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        for(int slot = 0; slot < TileCrucible.INPUT_SLOTS; slot++)
        {
            GlStateManager.pushMatrix();
            //Translate into the centre of the inner crucible, above the bottom
            GlStateManager.translated(x + 0.45F + 0.075F * slot, y + 0.6F, z + 0.5F);
            //make them diagonal
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(50.0F, 1.0F, 1.0F, 0.0F);
            //make them fit
            GlStateManager.scalef(0.3F, 0.3F, 0.3F);
            Minecraft.getInstance().getItemRenderer().renderItem(te.input.getStackInSlot(slot), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
        if (te.output != null && te.output.getFluidAmount() > 0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            RenderUtils.translateAgainstPlayer(te.getPos(), false);

            FluidStack fluid = te.output.getFluid();
            int color = fluid.getFluid().getColor(fluid);
            final TextureAtlasSprite still = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getStill(fluid));
            final TextureAtlasSprite flowing = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getFlowing(fluid));

            RenderUtils.renderFluid(fluid, te.getPos(), 0.35d, 0.35d, 0.35d, 0.0d, 0.0d, 0.0d, 0.30d, 0.30d, 0.30d, color, still, flowing);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
