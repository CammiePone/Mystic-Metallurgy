package com.camellias.mysticalmetallurgy.api;

import com.camellias.mysticalmetallurgy.api.effect.EffectLinker;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RegisterItemEffectsEvent extends Event
{
    private EffectLinker handler;
    
    public RegisterItemEffectsEvent(EffectLinker registry)
    {
        handler = registry;
    }

    public EffectLinker getRegistry()
    {
    	return handler;
    }
}
