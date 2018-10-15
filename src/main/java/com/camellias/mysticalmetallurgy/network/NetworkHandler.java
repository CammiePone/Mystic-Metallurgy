package com.camellias.mysticalmetallurgy.network;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkHandler
{
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Main.CHANNEL);

    private static int packetId = 0;

    /**
     * Registers all packets and handlers - call this during {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent}
     */
    public static void registerPackets() {
        dispatcher.registerMessage(PlaySoundPacket.class, PlaySoundPacket.class, packetId++, Side.CLIENT);
    }

    public static IThreadListener getThreadListener(MessageContext ctx) {
        return ctx.side == Side.SERVER ? (WorldServer) ctx.getServerHandler().player.world : getClientThreadListener();
    }

    @SideOnly(Side.CLIENT)
    public static IThreadListener getClientThreadListener() {
        return Minecraft.getMinecraft();
    }

    /**
     * Send this message to everyone.
     * See {@link SimpleNetworkWrapper#sendToAll(IMessage)}
     */
    public static void sendToAll(IMessage message) {
        dispatcher.sendToAll(message);
    }

    public static void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }

    public static void sendAround(IMessage message, BlockPos pos, int dimId) {
        dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimId, pos.getX(), pos.getY(), pos.getZ(), 64));
    }
}
