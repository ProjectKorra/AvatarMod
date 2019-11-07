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
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;

import com.crowsofwar.avatar.common.GliderInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CrowsOfWar
 */
public class AvatarItems {

	public static List<Item> allItems;
	public static ItemWaterPouch itemWaterPouch;
	public static ItemBisonWhistle itemBisonWhistle;
	public static ItemBisonSaddle itemBisonSaddle;
	public static ItemBisonArmor itemBisonArmor;
	public static ItemOstrichEquipment itemOstrichEquipment;
	private static ItemStack stackScroll;
	public static ItemAirbenderStaff airbenderStaff;
	public static ItemHangGliderPart GLIDER_PART_Scaffolding;
	public static ItemHangGliderPart GLIDER_PART_LeftWing;
	public static ItemHangGliderPart GLIDER_PART_RightWing;
	public static ItemHangGliderBasic GLIDER_BASIC;
	public static ItemHangGliderAdvanced GLIDER_ADV;
	public static CreativeTabs tabItems = new CreativeTabs("avatar.items") {
		@Override
		public ItemStack createIcon() {
			return stackScroll;
		}
	};

	private AvatarItems() {
	}

	public static void init() {
		allItems = new ArrayList<>();
		addItem(Scrolls.ALL);
		addItem(Scrolls.AIR);
		addItem(Scrolls.EARTH);
		addItem(Scrolls.FIRE);
		addItem(Scrolls.WATER);
		addItem(Scrolls.COMBUSTION);
		addItem(Scrolls.SAND);
		addItem(Scrolls.ICE);
		addItem(Scrolls.LIGHTNING);
		addItem(itemWaterPouch = new ItemWaterPouch());
		addItem(itemBisonWhistle = new ItemBisonWhistle());
		addItem(itemBisonArmor = new ItemBisonArmor());
		addItem(itemBisonSaddle = new ItemBisonSaddle());
		addItem(itemOstrichEquipment = new ItemOstrichEquipment());
		addItem(airbenderStaff = new ItemAirbenderStaff(Item.ToolMaterial.WOOD));
		addItem(GLIDER_BASIC = new ItemHangGliderBasic());
		addItem(GLIDER_ADV = new ItemHangGliderAdvanced());

		stackScroll = new ItemStack(Scrolls.ALL);
		MinecraftForge.EVENT_BUS.register(new AvatarItems());

	}

	//Models
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		itemRender(GLIDER_BASIC, 0, GliderInfo.ITEM_GLIDER_BASIC_NAME);
		itemRender(GLIDER_ADV, 0, GliderInfo.ITEM_GLIDER_ADVANCED_NAME);
		itemRender(GLIDER_PART_LeftWing, 0, ItemHangGliderPart.names[0]);
		itemRender(GLIDER_PART_RightWing, 1, ItemHangGliderPart.names[1]);
		itemRender(GLIDER_PART_Scaffolding, 2, ItemHangGliderPart.names[2]);

	}

	private static void itemRender(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(AvatarInfo.DOMAIN + name, "inventory"));
	}

	private static void addItem(Item item) {
		item.setRegistryName("avatarmod", item.getTranslationKey().substring(5));
		item.setTranslationKey("avatarmod:" + item.getTranslationKey().substring(5));
		allItems.add(item);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		Item[] itemsArr = allItems.toArray(new Item[allItems.size()]);
		e.getRegistry().registerAll(itemsArr);

		e.getRegistry().register(new ItemHangGliderPart().setRegistryName("avatarmod:" + GliderInfo.ITEM_GLIDER_PART_NAME));
		//e.getRegistry().register(new ItemHangGliderBasic().setRegistryName("avatarmod:" + GliderInfo.ITEM_GLIDER_BASIC_NAME));
		//e.getRegistry().register(new ItemHangGliderAdvanced().setRegistryName("avatarmod:" + GliderInfo.ITEM_GLIDER_ADVANCED_NAME));

		AvatarMod.proxy.registerItemModels();
	}

}
