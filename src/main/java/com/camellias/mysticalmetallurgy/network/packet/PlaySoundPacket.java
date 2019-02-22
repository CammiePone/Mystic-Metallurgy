package com.camellias.mysticalmetallurgy.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.GameData;

import java.util.function.Supplier;

public class PlaySoundPacket
{
    private SoundEvent sound;
    private BlockPos pos;
    private SoundCategory soundCategory;
    private float volume;
    private float pitch;

    public PlaySoundPacket(BlockPos pos, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        this.sound = sound;
        this.pos = pos;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(PlaySoundPacket msg, PacketBuffer buf) {
        buf.writeLong(msg.pos.toLong());
        buf.writeResourceLocation(msg.sound.getRegistryName());
        buf.writeInt(msg.soundCategory.ordinal());
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
    }

    public static PlaySoundPacket decode(PacketBuffer buf) {
        return new PlaySoundPacket(
                BlockPos.fromLong(buf.readLong()),
                GameData.getWrapper(SoundEvent.class).get(buf.readResourceLocation()),
                SoundCategory.values()[buf.readInt()],
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().getSoundHandler()
                .play(new SimpleSound(msg.sound, msg.soundCategory, msg.volume, msg.pitch, msg.pos)));
    }
}
