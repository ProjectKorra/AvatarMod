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

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.stackCompound;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ItemScroll extends Item {
	
	// ItemDye
	
	public ItemScroll() {
		setUnlocalizedName("scroll");
		setMaxStackSize(1);
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + ScrollType.fromId(stack.getMetadata()).displayName();
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		NBTTagCompound nbt = stackCompound(stack);
		int pts = nbt.getInteger("Points");
		if (pts >= 3) return EnumRarity.EPIC;
		if (pts == 2) return EnumRarity.RARE;
		if (pts == 1) return EnumRarity.UNCOMMON;
		return EnumRarity.COMMON;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips,
			boolean advanced) {
		
		NBTTagCompound nbt = stackCompound(stack);
		int pts = nbt.getInteger("Points");
		
		tooltips.add(I18n.format("avatar.tooltip.scroll", pts));
		
	}
	
	public enum ScrollType {
		ALL,
		EARTH,
		FIRE,
		WATER,
		AIR;
		
		public String displayName() {
			return name().toLowerCase();
		}
		
		public int id() {
			return ordinal();
		}
		
		public static ScrollType fromId(int id) {
			if (id < 0 || id >= values().length) throw new IllegalArgumentException("Invalid id: " + id);
			return values()[id];
		}
		
	}
	
}
