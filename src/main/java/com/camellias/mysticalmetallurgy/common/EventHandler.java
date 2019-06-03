package com.camellias.mysticalmetallurgy.common;

import com.camellias.mysticalmetallurgy.library.utils.HotUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler
{
    @SubscribeEvent
    public static void onLivingUpdate (LivingEvent.LivingUpdateEvent event) {

        if (event.getEntityLiving() instanceof EntityPlayer) {

            final EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            HotUtils.hotItemCheck(player);
        }
    }
}
