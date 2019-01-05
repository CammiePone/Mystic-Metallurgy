package com.camellias.mysticalmetallurgy.network.packet;

import com.camellias.mysticalmetallurgy.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ToolBreakAnimationPacket implements IMessage, IMessageHandler<ToolBreakAnimationPacket, IMessage>
{
    private ItemStack breakingTool;

    public ToolBreakAnimationPacket()
    {
    }

    public ToolBreakAnimationPacket(ItemStack breakingTool)
    {
        this.breakingTool = breakingTool;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        breakingTool = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, breakingTool);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(ToolBreakAnimationPacket message, MessageContext ctx)
    {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> Minecraft.getMinecraft().player.renderBrokenItemStack(breakingTool));
        return null;
    }
}
