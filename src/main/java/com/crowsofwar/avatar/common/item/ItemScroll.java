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
import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.ice.Icebending;
import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.bending.sand.Sandbending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.AvatarEntityItem;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ScrollType type = ScrollType.get(player.getHeldItem(hand).getMetadata());
		if (type.isSpecialtyType()) {
			handleSpecialtyScrollUse(world, player, player.getHeldItem(hand));
		} else {
			handleMainScrollUse(world, player);
		}
		
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));

	}

	/**
	 * Fired for right-clicking on a main bending style scroll (e.g. firebending scroll)
	 */
	private void handleMainScrollUse(World world, EntityPlayer player) {

		BendingData data = BendingData.get(player);
		if (data.getAllBending().isEmpty()) {
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_GET_BENDING, world, 0, 0, 0);
		} else {
			BendingStyle controller = data.getAllBending().get(0);
			int guiId = AvatarGuiHandler.getGuiId(controller.getId());
			player.openGui(AvatarMod.instance, guiId, world, 0, 0, 0);
		}

	}

	/**
	 * Fired for right-clicking on a specialty bending scroll (e.g. lightningbending scroll)
	 */
	private void handleSpecialtyScrollUse(World world, EntityPlayer player, ItemStack stack) {

		if (world.isRemote) {
			return;
		}

		ScrollType type = ScrollType.get(stack.getMetadata());
		BendingData data = BendingData.get(player);
		BendingStyle specialtyStyle = BendingStyles.get(type.getBendingId());

		//noinspection ConstantConditions - we already know this is a specialty bending style
		UUID requiredMainBending = specialtyStyle.getParentBendingId();

		if (data.hasBendingId(requiredMainBending)) {

			data.addBending(specialtyStyle);
			if (!player.isCreative()) {
				stack.shrink(1);
			}

			String specialtyName = specialtyStyle.getName();
			AvatarChatMessages.MSG_SPECIALTY_SCROLL_SUCCESS.send(player, specialtyName);

		} else {

			String specialtyName = specialtyStyle.getName();
			String mainName = BendingStyles.getName(requiredMainBending);
			AvatarChatMessages.MSG_SPECIALTY_SCROLL_FAIL.send(player, specialtyName, mainName);

		}

	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int metadata = stack.getMetadata() >= ScrollType.values().length ? 0 : stack.getMetadata();
		return super.getUnlocalizedName(stack) + "." + ScrollType.get(metadata).displayName();
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
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
	public void addInformation(ItemStack stack, World world, List<String> tooltips,
							   ITooltipFlag advanced) {
		
		String tooltip = I18n.format("avatar." + getScrollType(stack).getBendingName());
		tooltips.add(tooltip);
		
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		return "scroll_" + ScrollType.get(meta).displayName();
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {

		if (isInCreativeTab(tab)) {
			for (int meta = 0; meta < ScrollType.values().length; meta++) {
				subItems.add(new ItemStack(this, 1, meta));
			}
		}
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
	
	public static ScrollType getScrollType(ItemStack stack) {
		int meta = stack.getMetadata();
		if (meta < 0 || meta >= ScrollType.values().length) return ScrollType.ALL;
		return ScrollType.get(meta);
	}
	
	public static void setScrollType(ItemStack stack, ScrollType type) {
		stack.setItemDamage(type.id());
	}
	
	public enum ScrollType {

		ALL(null),
		EARTH(Earthbending.ID),
		FIRE(Firebending.ID),
		WATER(Waterbending.ID),
		AIR(Airbending.ID),
		LIGHTNING(Lightningbending.ID),
		ICE(Icebending.ID),
		SAND(Sandbending.ID),
		COMBUSTION(Combustionbending.ID);

		private final UUID bendingId;

		private ScrollType(UUID bendingId) {
			this.bendingId = bendingId;
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
		
		public boolean accepts(UUID bendingId) {

			// Universal scroll
			if (this.bendingId == null) {
				return true;
			}

			// Same type
			if (this.bendingId == bendingId) {
				return true;
			}

			// Trying to use parent-type bending scroll on specialty bending style
			if (BendingStyles.get(bendingId).getParentBendingId() == this.bendingId) {
				return true;
			}

			return false;

		}

		public String getBendingName() {
			return bendingId == null ? "all" : BendingStyles.get(bendingId).getName();
		}

		/**
		 * Gets the corresponding bending ID from this scroll. Returns null in the
		 * case of ALL.
		 */
		@Nullable
		public UUID getBendingId() {
			return bendingId;
		}

		/**
		 * Returns whether this scroll is for a specialty bending type, like lightningbending. For
		 * universal scrolls, returns false.
		 */
		public boolean isSpecialtyType() {

			BendingStyle style = BendingStyles.get(bendingId);

			if (style == null) {
				return false;
			}

			return style.isSpecialtyBending();

		}

		@Nullable
		public static ScrollType get(int id) {
			if (id < 0 || id >= values().length) return null;
			return values()[id];
		}
		
		public static int amount() {
			return values().length;
		}
		
	}
	
}
