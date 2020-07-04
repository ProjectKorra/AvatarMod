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
import com.crowsofwar.avatar.common.item.scroll.*;

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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CrowsOfWar
 */
public class AvatarItems {

	public static List<Item> allItems;
	public static List<Item> modeledItems;
	public static ItemScroll itemScroll;
	public static ItemWaterPouch itemWaterPouch;
	public static ItemBisonWhistle itemBisonWhistle;
	public static ItemBisonSaddle itemBisonSaddle;
	public static ItemBisonArmor itemBisonArmor;
	public static ItemOstrichEquipment itemOstrichEquipment;
	public static ItemStack stackScroll;
	public static ItemAirbenderStaff airbenderStaff;
	public static ItemHangGliderPart gliderPartScaffolding;
	public static ItemHangGliderPart gliderPartLeftWing;
	public static ItemHangGliderPart gliderPartRightWing;
	public static ItemHangGliderBasic gliderBasic;
	public static ItemHangGliderAdvanced gliderAdv;

	public static CreativeTabs tabItems = new CreativeTabs("avatar.items") {
		@Nonnull
		@Override
		public ItemStack createIcon() {
			return AvatarItems.stackScroll;
		}
	};

	private AvatarItems() {
	}

	public static void init() {
		allItems = new ArrayList<>();
		Scrolls.ALL = ItemScrollAll.getInstance();
		Scrolls.AIR = ItemScrollAir.getInstance();
		Scrolls.EARTH = ItemScrollEarth.getInstance();
		Scrolls.FIRE = ItemScrollFire.getInstance();
		Scrolls.WATER = ItemScrollWater.getInstance();
		addItem(Scrolls.COMBUSTION = new ItemScrollCombustion());
		addItem(Scrolls.SAND = new ItemScrollSand());
		addItem(Scrolls.ICE = new ItemScrollIce());
		addItem(Scrolls.LIGHTNING = new ItemScrollLightning());
		addItem(itemWaterPouch = new ItemWaterPouch());
		addItem(itemBisonWhistle = new ItemBisonWhistle());
		addItem(itemBisonArmor = new ItemBisonArmor());
		addItem(itemBisonSaddle = new ItemBisonSaddle());
		addItem(itemOstrichEquipment = new ItemOstrichEquipment());
		addItem(airbenderStaff = new ItemAirbenderStaff(Item.ToolMaterial.WOOD));
		addItem(gliderBasic = new ItemHangGliderBasic());
		addItem(gliderAdv = new ItemHangGliderAdvanced());

		stackScroll = new ItemStack(Scrolls.ALL);
		MinecraftForge.EVENT_BUS.register(new AvatarItems());

	}

	//Models
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		itemRender(gliderBasic, 0, GliderInfo.itemGliderBasicName);
		itemRender(gliderAdv, 0, GliderInfo.itemGliderAdvancedName);
		itemRender(gliderPartLeftWing, 0, ItemHangGliderPart.names[0]);
		itemRender(gliderPartRightWing, 1, ItemHangGliderPart.names[1]);
		itemRender(gliderPartScaffolding, 2, ItemHangGliderPart.names[2]);
	}

	private static void itemRender(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(AvatarInfo.DOMAIN + name, "inventory"));
	}

	public static void addItem(Item item) {
		item.setRegistryName("avatarmod", item.getTranslationKey().substring(5));
		item.setTranslationKey("avatarmod:" + item.getTranslationKey().substring(5));
		allItems.add(item);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		Item[] itemsArr = allItems.toArray(new Item[allItems.size()]);
		e.getRegistry().registerAll(itemsArr);

		e.getRegistry().register(new ItemHangGliderPart().setRegistryName(AvatarInfo.MOD_ID + GliderInfo.itemGliderPartName));
		e.getRegistry().register(new ItemHangGliderBasic().setRegistryName(AvatarInfo.MOD_ID + GliderInfo.itemGliderBasicName));
		e.getRegistry().register(new ItemHangGliderAdvanced().setRegistryName(AvatarInfo.MOD_ID + GliderInfo.itemGliderAdvancedName));

		AvatarMod.proxy.registerItemModels();
	}

}
