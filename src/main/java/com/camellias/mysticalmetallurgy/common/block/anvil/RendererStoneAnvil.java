package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RendererStoneAnvil extends TileEntitySpecialRenderer<TileStoneAnvil>
{
    @Override
    public void render(TileStoneAnvil te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        RayTraceResult rayTrace = player.rayTrace(4, partialTicks);
        ItemStack stackHeld = player.getHeldItemMainhand();
        EnumFacing facing = te.getBlockState().getValue(BlockStoneAnvil.FACING);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.scale(0.2F, 0.2F, 0.2F);

        TileStoneAnvil.Slot slotHover = null;
        if (rayTrace != null && rayTrace.hitVec != null && rayTrace.sideHit == EnumFacing.UP && rayTrace.getBlockPos().equals(te.getPos()))
        {
            float hitX = (float) (Math.abs(rayTrace.hitVec.x) - Math.floor(Math.abs(rayTrace.hitVec.x)));
            float hitZ = (float) (Math.abs(rayTrace.hitVec.z) - Math.floor(Math.abs(rayTrace.hitVec.z)));
            slotHover = TileStoneAnvil.Slot.getSlotHit(facing, hitX, hitZ);
        }

        for (TileStoneAnvil.Slot slot : TileStoneAnvil.Slot.values())
        {
            ItemStack slotStack = te.extract(slot, true);
            if (!slotStack.isEmpty())
                renderSlot(slot.getRenderOffset(), slotStack, facing, slot == slotHover, false);
            else if (slotHover == slot && !stackHeld.isEmpty())
            {
                if (slot.acceptStack(stackHeld))
                    renderSlot(slot.getRenderOffset(), stackHeld, facing, false, true);
                else
                    renderSlot(slot.getRenderOffset(), new ItemStack(Blocks.BARRIER), facing, false, true);
            }
        }
        GlStateManager.popMatrix();
    }

    private void renderSlot(Vec3d offset, ItemStack stack, EnumFacing facing, boolean highlight, boolean ghostly)
    {
        GlStateManager.pushMatrix();
        if (ghostly)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        else if (highlight)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager.depthFunc(GL11.GL_EQUAL);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
        }
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        RenderUtils.rotateOnFacing(facing);
        GlStateManager.translate(offset.x, offset.y, offset.z);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        if (ghostly || highlight)
            GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
