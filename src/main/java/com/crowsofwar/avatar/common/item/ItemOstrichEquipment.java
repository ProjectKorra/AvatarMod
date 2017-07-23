package com.crowsofwar.avatar.common.item;

import net.minecraft.item.Item;

/**
 * @author CrowsOfWar
 */
public class ItemOstrichEquipment extends Item implements AvatarItem {

	public ItemOstrichEquipment() {
		setUnlocalizedName("ostrich_equipment");
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
		return "ostrich_equip_" + meta;
	}

}
