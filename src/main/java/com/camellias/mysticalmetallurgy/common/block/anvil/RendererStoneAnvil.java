package com.camellias.mysticalmetallurgy.common.block.anvil;

import com.camellias.mysticalmetallurgy.api.recipe.AnvilRecipe;
import com.camellias.mysticalmetallurgy.common.item.tool.ItemHammer;
import com.camellias.mysticalmetallurgy.library.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RendererStoneAnvil extends TileEntityRenderer<TileStoneAnvil>
{
    @Override
    public void render(TileStoneAnvil te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        EntityPlayer player = Minecraft.getInstance().player;
        RayTraceResult rayTrace = player.rayTrace(4, partialTicks, RayTraceFluidMode.NEVER);
        ItemStack stackHeld = player.getHeldItemMainhand();
        EnumFacing facing = te.getBlockState().get(BlockStoneAnvil.FACING);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.translatef(0.5F, 0.5F, 0.5F);
        GlStateManager.scalef(0.2F, 0.2F, 0.2F);

        TileStoneAnvil.InventorySlotTyped slotHover = null;
        if (rayTrace != null && rayTrace.hitVec != null && rayTrace.sideHit == EnumFacing.UP && rayTrace.getBlockPos().equals(te.getPos()))
        {
            float hitX = (float)(rayTrace.hitVec.x - (double)rayTrace.getBlockPos().getX());
            float hitZ = (float)(rayTrace.hitVec.z - (double)rayTrace.getBlockPos().getZ());
            slotHover = te.getSlotHit(facing, hitX, hitZ, te.hasOutput() ? TileStoneAnvil.InventorySlotTyped.SlotType.OUTPUT : TileStoneAnvil.InventorySlotTyped.SlotType.INPUT);
        }

        AnvilRecipe recipe = te.getActiveRecipe();
        if (recipe != null)
        {
            ItemStack printStack = te.getPrintForRendering();
            if (!printStack.isEmpty())
                renderSlot(player, te.slotPrint.getRenderOffset(), printStack, facing, false, false);
            renderSlot(player, te.slotOut.getRenderOffset(), recipe.getSwingStack(te.doneSwings()), facing, false, false);
        }
        else
        {
            for (TileStoneAnvil.InventorySlotTyped slot : te.getSlots())
            {
                ItemStack slotStack = slot.extract(1, true);
                if (!slotStack.isEmpty())
                    renderSlot(player, slot.getRenderOffset(), slotStack, facing, slot == slotHover, false);
                else if (slotHover == slot && !stackHeld.isEmpty() && !(stackHeld.getItem() instanceof ItemHammer))
                {
                    if (slot.acceptStack(stackHeld))
                        renderSlot(player, slot.getRenderOffset(), stackHeld, facing, false, true);
                    else
                        renderSlot(player, slot.getRenderOffset(), new ItemStack(Blocks.BARRIER), facing, false, false);
                }
            }
        }
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
