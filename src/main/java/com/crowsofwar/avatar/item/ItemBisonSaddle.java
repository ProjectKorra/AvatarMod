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
package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.registry.AvatarItem;
import com.crowsofwar.avatar.registry.AvatarItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * @author CrowsOfWar
 */
public class ItemBisonSaddle extends Item implements AvatarItem {

	private static ItemBisonSaddle instance = null;

	public ItemBisonSaddle() {
		setTranslationKey("bison_saddle");
		setMaxStackSize(1);
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	public static ItemBisonSaddle getInstance() {
		if(instance == null) {
			instance = new ItemBisonSaddle();
			AvatarItems.addItem(instance);
		}

		return instance;
	}

    @Override
	public Item item() {
		return this;
	}

	@Override
	public String getModelName(int meta) {
		SaddleTier tier = SaddleTier.get(meta);
		String tierName = tier == null ? "null" : tier.name().toLowerCase();
		return "bison_saddle_" + tierName;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		SaddleTier tier = SaddleTier.get(stack.getMetadata());
		String tierName = tier == null ? "null" : tier.name().toLowerCase();
		return super.getTranslationKey(stack) + "." + tierName;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {

		if (isInCreativeTab(tab)) {
			for (int i = 0; i <= 3; i++) {
				subItems.add(new ItemStack(this, 1, i));
			}
		}

	}

	public enum SaddleTier {

		BASIC(2, 1),
		STURDY(4, 2),
		STUDDED(6, 4),
		MAJESTIC(10, 6);

		private final float armorPoints;
		private final int maxPassengers;

		SaddleTier(float armorPoints, int maxPassengers) {
			this.armorPoints = armorPoints;
			this.maxPassengers = maxPassengers;
		}

		public static SaddleTier get(int id) {
			if (!isValidId(id)) {
				return null;
			}
			return values()[id];
		}

		public static boolean isValidId(int id) {
			return id >= 0 && id < values().length;
		}

		public float getArmorPoints() {
			return armorPoints;
		}

		public int getMaxPassengers() {
			return maxPassengers;
		}

		public int id() {
			return ordinal();
		}

	}

}
