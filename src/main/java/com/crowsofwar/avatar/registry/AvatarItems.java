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
package com.crowsofwar.avatar.registry;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.blocks.AvatarBlocks;
import com.crowsofwar.avatar.item.*;
import com.crowsofwar.avatar.item.scroll.*;

import com.crowsofwar.avatar.util.GliderInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
	public static ItemWaterPouch itemWaterPouch;
	public static ItemBisonWhistle itemBisonWhistle;
	public static ItemBisonSaddle itemBisonSaddle;
	public static ItemBisonArmor itemBisonArmor;
	public static ItemOstrichEquipment itemOstrichEquipment;
	public static ItemStack stackScroll;
	public static ItemHangGliderPart gliderPart;
	public static ItemHangGliderBasic gliderBasic;
	public static ItemHangGliderAdvanced gliderAdv;

	private static void PopulateItems() {
		Scrolls.ALL = ItemScrollAll.getInstance();
		Scrolls.AIR = ItemScrollAir.getInstance();
		Scrolls.EARTH = ItemScrollEarth.getInstance();
		Scrolls.FIRE = ItemScrollFire.getInstance();
		Scrolls.WATER = ItemScrollWater.getInstance();
		Scrolls.COMBUSTION = ItemScrollCombustion.getInstance();
		Scrolls.SAND = ItemScrollSand.getInstance();
		Scrolls.ICE = ItemScrollIce.getInstance();
		Scrolls.LIGHTNING = ItemScrollLightning.getInstance();
		itemWaterPouch = ItemWaterPouch.getInstance();
		itemBisonWhistle = ItemBisonWhistle.getInstance();
		itemBisonArmor = ItemBisonArmor.getInstance();
		itemBisonSaddle = ItemBisonSaddle.getInstance();
		itemOstrichEquipment = ItemOstrichEquipment.getInstance();
		gliderBasic = ItemHangGliderBasic.getInstance();
		gliderAdv = ItemHangGliderAdvanced.getInstance();
		gliderPart = ItemHangGliderPart.getInstance();
	}

	//Models
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		itemRender(gliderBasic, 0, GliderInfo.itemGliderBasicName);
		itemRender(gliderAdv, 0, GliderInfo.itemGliderAdvancedName);
		itemRender(gliderPart, 0, GliderInfo.ITEM_GLIDER_PART_LEFTWING);
		itemRender(gliderPart, 1, GliderInfo.ITEM_GLIDER_PART_RIGHTWING);
		itemRender(gliderPart, 2, GliderInfo.ITEM_GLIDER_PART_SCAFFOLDING);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		Item[] itemsArr = allItems.toArray(new Item[allItems.size()]);

		e.getRegistry().registerAll(itemsArr);
		for (Block block : AvatarBlocks.allBlocks)
			e.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

		AvatarMod.proxy.registerItemModels();
	}

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

		//separated this out so that new items have a variable added then initialised directly beneath.
		// the rest of how it works shouldn't matter when adding new stuff.
		PopulateItems();

		stackScroll = new ItemStack(Scrolls.ALL);
		MinecraftForge.EVENT_BUS.register(new AvatarItems());
	}

	private static void itemRender(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(AvatarInfo.DOMAIN + name, "inventory"));
	}

	public static void addItem(Item item) {
		item.setRegistryName("avatarmod", item.getTranslationKey().substring(5));
		item.setTranslationKey("avatarmod:" + item.getTranslationKey().substring(5));
		allItems.add(item);
	}

}
