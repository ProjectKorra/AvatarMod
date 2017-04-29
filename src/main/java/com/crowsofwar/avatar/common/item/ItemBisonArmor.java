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
public class ItemBisonArmor extends Item implements AvatarItem {
	
	public ItemBisonArmor() {
		setUnlocalizedName("bison_armor");
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
		ArmorTier tier = ArmorTier.fromId(meta);
		String tierName = tier == null ? "null" : tier.name().toLowerCase();
		return "bison_armor_" + tierName;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		ArmorTier tier = ArmorTier.fromId(stack.getMetadata());
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
	
	public enum ArmorTier {
		
		WOVEN(8, 0.8f),
		CHAIN(16, 0.7f),
		WROUGHT(20, 0.6f),
		LEGENDARY(26, 0.75f);
		
		private final float armorPoints;
		private final float speedMultiplier;
		
		private ArmorTier(float armorPoints, float speedMultiplier) {
			this.armorPoints = armorPoints;
			this.speedMultiplier = speedMultiplier;
		}
		
		public float getArmorPoints() {
			return armorPoints;
		}
		
		public float getSpeedMultiplier() {
			return speedMultiplier;
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
		public static ArmorTier fromId(int id) {
			if (!isValidId(id)) {
				throw new IllegalArgumentException("No ArmorTier for id " + id);
			}
			return values()[id];
		}
		
		public static boolean isValidId(int id) {
			return id >= 0 && id < values().length;
		}
		
	}
	
}
