package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAirbenderStaff extends Item {

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
		Vector velocity = Vector.getLookRectangular(attacker).times(10);
		target.motionX = velocity.x();
		target.motionY = velocity.y();
		target.motionZ = velocity.z();
		return super.hitEntity(stack, target, attacker);
	}

	//Add power rating modifier
}
