package com.camellias.mysticalmetallurgy.api;

import com.camellias.mysticalmetallurgy.common.effect.EffectHandler;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RegisterItemEffectsEvent extends Event
{
    private EffectHandler handler;
    
    public RegisterItemEffectsEvent(EffectHandler registry)
    {
        handler = registry;
    }

    public EffectHandler getRegistry()
    {
    	return handler;
    }
}
