package com.camellias.mysticalmetallurgy.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ToolBreakAnimationPacket
{
    private ItemStack breakingTool;

    public ToolBreakAnimationPacket(ItemStack breakingTool)
    {
        this.breakingTool = breakingTool;
    }

    public static void encode(ToolBreakAnimationPacket msg, PacketBuffer buf) {
        buf.writeItemStack(msg.breakingTool);
    }

    public static ToolBreakAnimationPacket decode(PacketBuffer buf) {
        return new ToolBreakAnimationPacket(
                buf.readItemStack()
        );
    }

    public static void handle(ToolBreakAnimationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().player.renderBrokenItemStack(msg.breakingTool));
    }
}
