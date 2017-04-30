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
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ItemBisonSaddle extends Item implements AvatarItem {
	
	public ItemBisonSaddle() {
		setUnlocalizedName("bison_saddle");
		setMaxStackSize(1);
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		SaddleTier tier = SaddleTier.fromId(meta);
		String tierName = tier == null ? "null" : tier.name().toLowerCase();
		return "bison_saddle_" + tierName;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		SaddleTier tier = SaddleTier.fromId(stack.getMetadata());
		String tierName = tier == null ? "null" : tier.name().toLowerCase();
		return super.getUnlocalizedName(stack) + "." + tierName;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		
		for (int i = 0; i <= 3; i++) {
			subItems.add(new ItemStack(item, 1, i));
		}
		
	}
	
	public enum SaddleTier {
		
		CRUDE(2, 1),
		BASIC(4, 2),
		AVERAGE(6, 4),
		QUALITY(10, 6);
		
		private final float armorPoints;
		private final int maxPassengers;
		
		private SaddleTier(float armorPoints, int maxPassengers) {
			this.armorPoints = armorPoints;
			this.maxPassengers = maxPassengers;
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
		
		/**
		 * Finds the tier with the given id
		 * 
		 * @throws IllegalArgumentException
		 *             when id is {@link #isValidId(int) invalid}
		 */
		public static SaddleTier fromId(int id) {
			if (!isValidId(id)) {
				throw new IllegalArgumentException("No SaddleTier for id " + id);
			}
			return values()[id];
		}
		
		public static boolean isValidId(int id) {
			return id >= 0 && id < values().length;
		}
		
	}
	
}
