/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.AvatarInfo;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarItems {
	
	public static CreativeTabs tabItems = new CreativeTabs("avatar.items") {
		@Override
		public ItemStack getTabIconItem() {
			return stackScroll;
		}
	};
	
	public static ItemScroll itemScroll;
	public static ItemWaterPouch itemWaterPouch;
	public static ItemBisonWhistle itemBisonWhistle;
	
	private static ItemStack stackScroll;
	
	public static void init() {
		registerItem(itemScroll = new ItemScroll());
		registerItem(itemWaterPouch = new ItemWaterPouch());
		registerItem(itemBisonWhistle = new ItemBisonWhistle());
		
		stackScroll = new ItemStack(itemScroll);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Points", 2);
		stackScroll.setTagCompound(nbt);
		
	}
	
	private static void registerItem(Item item) {
		item.setRegistryName(AvatarInfo.MOD_ID, item.getUnlocalizedName().substring(5));
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}
	
}
