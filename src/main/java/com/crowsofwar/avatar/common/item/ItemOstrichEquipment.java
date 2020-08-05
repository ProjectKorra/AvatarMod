package com.crowsofwar.avatar.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class ItemOstrichEquipment extends Item implements AvatarItem {
	private static ItemOstrichEquipment instance = null;

	public ItemOstrichEquipment() {
		setTranslationKey("ostrich_equip");
		setMaxStackSize(1);
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	public static ItemOstrichEquipment getInstance() {
		if(instance == null) {
			instance = new ItemOstrichEquipment();
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
		return "ostrich_equip_" + EquipmentTier.getTierName(meta);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey(stack) + "." + EquipmentTier
				.getTierName(stack.getMetadata());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {

		if (isInCreativeTab(tab)) {
			for (int i = 0; i < EquipmentTier.values().length; i++) {
				subItems.add(new ItemStack(this, 1, i));
			}
		}

	}

	public enum EquipmentTier {

		WOVEN,
		CHAIN,
		PLATE;

		/**
		 * Get the lowercase of the equipment tier specified by the index, or null if there isn't
		 * any tier with that index.
		 */
		@Nullable
		public static String getTierName(int index) {
			if (!isValidIndex(index)) return null;
			return values()[index].name().toLowerCase();
		}

		/**
		 * Get the equipment specified by the index, or null if there isn't any tier with that
		 * index.
		 */
		@Nullable
		public static EquipmentTier getTier(int index) {
			if (!isValidIndex(index)) return null;
			return values()[index];
		}

		private static boolean isValidIndex(int index) {
			return index >= 0 && index < values().length;
		}

	}

}
