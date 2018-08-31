package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAirbenderStaff extends Item implements AvatarItem {

	public ItemAirbenderStaff() {
		setUnlocalizedName("airbender_staff");
		setCreativeTab(AvatarItems.tabItems);
		setMaxStackSize(1);
		setMaxDamage(2);

	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		Vector velocity = Vector.getLookRectangular(attacker).times(2);
		target.motionX += velocity.x();
		target.motionY += velocity.y();
		target.motionZ += velocity.z();
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public Item item() {
		return this;
	}

	@Override
	public String getModelName(int meta) {
		return "airbender_staff";
	}

	//Add power rating modifier
}
