package com.camellias.mysticalmetallurgy.common.block.basin;

import com.camellias.mysticalmetallurgy.library.utils.RenderUtils;
import com.camellias.mysticalmetallurgy.library.tileslottedinventory.InventorySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RendererQuenchingBasin extends TileEntitySpecialRenderer<TileQuenchingBasin>
{
    @Override
    public void render(TileQuenchingBasin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
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
        int color = fluid.getFluid().getColor(fluid);
        final TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
        final TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

        RenderUtils.renderFluid(fluid, te.getPos(), 0.35d, 0.35d, 0.35d, 0.0d, 0.0d, 0.0d, 0.30d, 0.30d, 0.30d, color, still, flowing);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderItems(TileQuenchingBasin te, double x, double y, double z, float partialTicks)
    {

        EntityPlayer player = Minecraft.getMinecraft().player;
        RayTraceResult rayTrace = player.rayTrace(4, partialTicks);
        ItemStack stackHeld = player.getHeldItemMainhand();
        EnumFacing facing = te.getBlockState().getValue(BlockQuenchingBasin.FACING);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        InventorySlot slotHover = null;
        if (rayTrace != null && rayTrace.hitVec != null && rayTrace.sideHit == EnumFacing.UP && rayTrace.getBlockPos().equals(te.getPos()))
        {
            float hitX = (float) (Math.abs(rayTrace.hitVec.x) - Math.floor(Math.abs(rayTrace.hitVec.x)));
            float hitZ = (float) (Math.abs(rayTrace.hitVec.z) - Math.floor(Math.abs(rayTrace.hitVec.z)));
            slotHover = te.getSlotHit(facing, hitX, hitZ);
        }

        for (InventorySlot slot : te.getSlots())
        {
            ItemStack slotStack = te.extract(slot, true);
            if (!slotStack.isEmpty())
                renderSlot(player, slot.getRenderOffset(), slotStack, facing, slot == slotHover, false);
            else if (slotHover == slot && !stackHeld.isEmpty() && slotHover.acceptStack(stackHeld))
                renderSlot(player, slot.getRenderOffset(), stackHeld, facing, false, true);
        }
        GlStateManager.popMatrix();
    }

    private void renderSlot(EntityPlayer player, Vec3d offset, ItemStack stack, EnumFacing facing, boolean highlight, boolean ghostly)
    {
        RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        IBakedModel model = renderer.getItemModelWithOverrides(stack, player.world, player);

        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.pushMatrix();

        RenderHelper.enableStandardItemLighting();

        //GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        RenderUtils.rotateOnFacing(facing);
        GlStateManager.translate(offset.x, offset.y, offset.z);

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
