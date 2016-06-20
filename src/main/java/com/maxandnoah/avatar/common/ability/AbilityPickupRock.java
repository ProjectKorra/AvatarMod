package com.maxandnoah.avatar.common.ability;

import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class AbilityPickupRock implements IAbility {

	@Override
	public void onAbilityActive(EntityPlayer player, AvatarPlayerData data) {
		World world = player.worldObj;
		BlockPos target = data.getTargetPos();
		Block b = world.getBlock(target.x, target.y, target.z);
		world.setBlock(target.x, target.y, target.z, Blocks.air);
		world.setBlock(target.x, target.y + 3, target.z, b);
	}
	
}
