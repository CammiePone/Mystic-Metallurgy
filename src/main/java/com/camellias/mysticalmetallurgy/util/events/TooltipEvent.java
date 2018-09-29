package com.camellias.mysticalmetallurgy.util.events;

import java.util.List;

import com.camellias.mysticalmetallurgy.Reference;
import com.camellias.mysticalmetallurgy.init.ModItems;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MODID, value = Side.CLIENT)
public class TooltipEvent
{
	@SubscribeEvent
	public static void addToolTip(ItemTooltipEvent event)
	{
		//<Hold Shift For More Info>
		String shift = new String(TextFormatting.GOLD + I18n.format("shift.info"));
		
		//Attributes:
		String attributes = new String(TextFormatting.GOLD + TextFormatting.BOLD.toString() + I18n.format("attributes.info"));
		
		Item item = event.getItemStack().getItem();
		List<String> tooltip = event.getToolTip();
		
		if(item == Items.GOLD_INGOT)
        {
			//Magical 3
			String info = new String(TextFormatting.YELLOW + I18n.format(item.getUnlocalizedName() + ".info"));
			
			tooltip.add(shift);
			
			if(GuiScreen.isShiftKeyDown())
			{
				tooltip.add(attributes);
				tooltip.add(info);
				
				tooltip.remove(shift);
			}
        }
		
		
		
		
		
		if(item == Items.IRON_INGOT)
        {
			//Dense 3
			String info = new String(TextFormatting.GRAY + I18n.format(item.getUnlocalizedName() + ".info"));
			
			tooltip.add(shift);
			
			if(GuiScreen.isShiftKeyDown())
			{
				tooltip.add(attributes);
				tooltip.add(info);
				
				tooltip.remove(shift);
			}
        }
		
		
		
		
		
		if(item == ModItems.SILVER_INGOT)
		{
			//Consecrated 3
			String info = new String(TextFormatting.WHITE + I18n.format(item.getUnlocalizedName() + ".info"));
			
			tooltip.add(shift);
			
			if(GuiScreen.isShiftKeyDown())
			{
				tooltip.add(attributes);
				tooltip.add(info);
				
				tooltip.remove(shift);
			}
		}
	}
}
