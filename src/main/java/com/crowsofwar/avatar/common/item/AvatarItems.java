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

import com.crowsofwar.avatar.AvatarMod;
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
public class
AvatarItems {
	
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
	public static ItemOstrichEquipment itemOstrichEquipment;
	
	private static ItemStack stackScroll;

	private AvatarItems() {}
	
	public static void init() {
		allItems = new ArrayList<>();
		addItem(itemScroll = new ItemScroll());
		addItem(itemWaterPouch = new ItemWaterPouch());
		addItem(itemBisonWhistle = new ItemBisonWhistle());
		addItem(itemBisonArmor = new ItemBisonArmor());
		addItem(itemBisonSaddle = new ItemBisonSaddle());
		addItem(itemOstrichEquipment = new ItemOstrichEquipment());
		
		stackScroll = new ItemStack(itemScroll);
		MinecraftForge.EVENT_BUS.register(new AvatarItems());

	}

	private static void addItem(Item item) {
		item.setRegistryName("avatarmod", item.getUnlocalizedName().substring(5));
		item.setUnlocalizedName("avatarmod:" + item.getUnlocalizedName().substring(5));
		allItems.add(item);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		Item[] itemsArr = allItems.toArray(new Item[allItems.size()]);
		e.getRegistry().registerAll(itemsArr);
		AvatarMod.proxy.registerItemModels();
	}

}
