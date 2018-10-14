package com.camellias.mysticalmetallurgy.common.block.crucible;

import com.camellias.mysticalmetallurgy.api.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

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
        if (te.output != null && te.output.getFluidAmount() > 0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            RenderUtils.translateAgainstPlayer(te.getPos(), false);

            FluidStack fluid = FluidUtil.getFluidContained(new ItemStack(Items.LAVA_BUCKET)); //te.output.getFluid(); for testing (color missing)
            int color = fluid.getFluid().getColor(fluid);
            final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
            final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

            RenderUtils.renderFluid(fluid, te.getPos(), 0.35d, 0.35d, 0.35d, 0.0d, 0.0d, 0.0d, 0.30d, 0.30d, 0.30d, color, still, flowing);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
