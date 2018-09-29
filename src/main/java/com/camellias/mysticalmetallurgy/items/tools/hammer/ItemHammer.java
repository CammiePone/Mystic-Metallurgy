package com.camellias.mysticalmetallurgy.items.tools.hammer;

import com.camellias.mysticalmetallurgy.Main;
import com.camellias.mysticalmetallurgy.Reference;
import com.camellias.mysticalmetallurgy.init.ModItems;
import com.camellias.mysticalmetallurgy.util.IHasModel;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ItemHammer extends ItemPickaxe implements IHasModel
{
	public ItemHammer(String name, ToolMaterial material)
	{
		super(material);
		setMaxDamage(125);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.mysticalMetallurgyItems);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public void registerModels() 
	{
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
