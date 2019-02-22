package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import com.camellias.mysticalmetallurgy.library.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RendererQuenchingBasin extends TileEntityRenderer<TileQuenchingBasin>
{
    @Override
    public void render(TileQuenchingBasin te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        renderItems(te, x, y, z, partialTicks);

        if (te.tank.getFluidAmount() > 0)
            renderFluid(te);
    }

    private void renderFluid(TileQuenchingBasin te)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        RenderUtils.translateAgainstPlayer(te.getPos(), false);

        FluidStack fluid = te.tank.getFluid();
        if (fluid != null)
        {
            int color = fluid.getFluid().getColor(fluid);
            final TextureAtlasSprite still = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getStill(fluid));
            final TextureAtlasSprite flowing = Minecraft.getInstance().getTextureMap().getSprite(fluid.getFluid().getFlowing(fluid));

            RenderUtils.renderFluid(fluid, te.getPos(), 0.35d, 0.35d, 0.35d, 0.0d, 0.0d, 0.0d, 0.30d, 0.30d, 0.30d, color, still, flowing);
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderItems(TileQuenchingBasin te, double x, double y, double z, float partialTicks)
    {

        EntityPlayer player = Minecraft.getInstance().player;
        RayTraceResult rayTrace = player.rayTrace(4, partialTicks, RayTraceFluidMode.NEVER);
        ItemStack stackHeld = player.getHeldItemMainhand();
        EnumFacing facing = te.getBlockState().get(BlockQuenchingBasin.FACING);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.translatef(0.5F, 0.5F, 0.5F);
        GlStateManager.scalef(0.2F, 0.2F, 0.2F);


        GlStateManager.popMatrix();
    }

    private void renderSlot(EntityPlayer player, Vec3d offset, ItemStack stack, EnumFacing facing, boolean highlight, boolean ghostly)
    {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        IBakedModel model = renderer.getItemModelWithOverrides(stack, player.world, player);

        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.pushMatrix();

        RenderHelper.enableStandardItemLighting();

        GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
        RenderUtils.rotateOnFacing(facing);
        GlStateManager.translated(offset.x, offset.y, offset.z);

        if (ghostly)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        }
        renderer.renderItem(stack, model);
        if (highlight && !ghostly)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager.depthFunc(GL11.GL_EQUAL);
            renderer.renderItem(stack, model);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
        }
        if (ghostly || highlight)
            GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
