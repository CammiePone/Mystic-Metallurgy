package com.camellias.mysticalmetallurgy.network;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.network.packet.PlaySoundPacket;
import com.camellias.mysticalmetallurgy.network.packet.ToolBreakAnimationPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler
{
    private static final SimpleChannel dispatcher = NetworkRegistry.ChannelBuilder.named(Main.CHANNEL).simpleChannel();

    private static int packetId = 0;

    public static void registerPackets() {
        dispatcher.registerMessage(packetId++, PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::decode, PlaySoundPacket::handle);
        dispatcher.registerMessage(packetId++, ToolBreakAnimationPacket.class, ToolBreakAnimationPacket::encode, ToolBreakAnimationPacket::decode, ToolBreakAnimationPacket::handle);
    }

    public static void sendToAll(Object message) {
        dispatcher.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToServer(Object message) {
        dispatcher.sendToServer(message);
    }

    public static void sendAround(Object message, BlockPos pos, DimensionType dimType) {
        dispatcher.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 64, dimType)), message);
    }

    public static void sendTo(Object message, EntityPlayerMP playerMP) {
        dispatcher.sendTo(message, playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendPacket(Entity player, Packet<?> packet) {
        if(player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
            ((EntityPlayerMP) player).connection.sendPacket(packet);
        }
    }
}
