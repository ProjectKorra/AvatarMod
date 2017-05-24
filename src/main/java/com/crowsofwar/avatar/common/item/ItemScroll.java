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
import java.util.function.Predicate;

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.AvatarEntityItem;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ItemScroll extends Item implements AvatarItem {
	
	public ItemScroll() {
		setUnlocalizedName("scroll");
		setMaxStackSize(1);
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int metadata = stack.getMetadata() >= ScrollType.values().length ? 0 : stack.getMetadata();
		return super.getUnlocalizedName(stack) + "." + ScrollType.fromId(metadata).displayName();
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
	public boolean hasCustomEntity(ItemStack stack) {
		return getScrollType(stack) == ScrollType.FIRE;
	}
	
	@Override
	public Entity createEntity(World world, Entity old, ItemStack stack) {
		AvatarEntityItem custom = new AvatarEntityItem(world, old.posX, old.posY, old.posZ, stack);
		custom.setResistFire(true);
		custom.motionX = old.motionX;
		custom.motionY = old.motionY;
		custom.motionZ = old.motionZ;
		custom.setDefaultPickupDelay();
		return custom;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltips,
			boolean advanced) {
		
		NBTTagCompound nbt = stackCompound(stack);
		int pts = nbt.getInteger("Points");
		
		tooltips.add(I18n.format("avatar.tooltip.scroll", pts));
		
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		return "scroll_" + ScrollType.fromId(meta).displayName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		
		for (int meta = 0; meta < ScrollType.values().length; meta++) {
			subItems.add(setPoints(new ItemStack(item, 1, meta), 1));
			subItems.add(setPoints(new ItemStack(item, 1, meta), 2));
			subItems.add(setPoints(new ItemStack(item, 1, meta), 3));
		}
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return getPoints(stack) > 0;
	}
	
	public static int getPoints(ItemStack stack) {
		return stackCompound(stack).getInteger("Points");
	}
	
	public static ItemStack setPoints(ItemStack stack, int points) {
		stackCompound(stack).setInteger("Points", points);
		return stack;
	}
	
	public static ScrollType getScrollType(ItemStack stack) {
		int meta = stack.getMetadata();
		if (meta < 0 || meta >= ScrollType.values().length) return ScrollType.ALL;
		return ScrollType.fromId(meta);
	}
	
	public static void setScrollType(ItemStack stack, ScrollType type) {
		stack.setItemDamage(type.id());
	}
	
	public enum ScrollType {
		ALL(type -> true),
		EARTH(type -> type == BendingType.EARTHBENDING),
		FIRE(type -> type == BendingType.FIREBENDING),
		WATER(type -> type == BendingType.WATERBENDING),
		AIR(type -> type == BendingType.AIRBENDING);
		
		private final Predicate<BendingType> test;
		
		private ScrollType(Predicate<BendingType> test) {
			this.test = test;
		}
		
		public boolean isCompatibleWith(ScrollType other) {
			return other == this || this == ALL || other == ALL;
		}
		
		public String displayName() {
			return name().toLowerCase();
		}
		
		public int id() {
			return ordinal();
		}
		
		public boolean accepts(BendingType type) {
			return test.test(type);
		}
		
		public static ScrollType fromId(int id) {
			if (id < 0 || id >= values().length) throw new IllegalArgumentException("Invalid id: " + id);
			return values()[id];
		}
		
		public static int amount() {
			return values().length;
		}
		
	}
	
}
