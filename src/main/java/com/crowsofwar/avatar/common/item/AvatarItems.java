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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

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

	public static List<Item> allItems;
	public static ItemScroll itemScroll;
	public static ItemWaterPouch itemWaterPouch;
	public static ItemBisonWhistle itemBisonWhistle;
	public static ItemBisonSaddle itemBisonSaddle;
	public static ItemBisonArmor itemBisonArmor;
	
	private static ItemStack stackScroll;

	private AvatarItems() {}
	
	public static void init() {
		allItems = new ArrayList<>();
		allItems.add(itemScroll = new ItemScroll());
		allItems.add(itemWaterPouch = new ItemWaterPouch());
		allItems.add(itemBisonWhistle = new ItemBisonWhistle());
		allItems.add(itemBisonSaddle = new ItemBisonSaddle());
		allItems.add(itemBisonArmor = new ItemBisonArmor());
		
		stackScroll = new ItemStack(itemScroll);

		MinecraftForge.EVENT_BUS.register(new AvatarItems());

	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		Item[] itemsArr = allItems.toArray(new Item[allItems.size()]);
		e.getRegistry().registerAll(itemsArr);
	}

}
