package com.maxandnoah.avatar.common.bending;

import com.maxandnoah.avatar.common.AvatarControlList;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.data.PlayerState;
import com.maxandnoah.avatar.common.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Earthbending implements BendingController {
	
	Earthbending() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public int getID() {
		return BendingManager.BENDINGID_EARTHBENDING;
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public void onKeypress(String key, AvatarPlayerData data) {
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		World world = player.worldObj;
		
		if (key.equals(AvatarControlList.CONTROL_TOGGLE_BENDING)) {
			BlockPos target = state.verifyClientLookAtBlock(-1, 5);
			Block b = world.getBlock(target.x, target.y, target.z);
			world.setBlock(target.x, target.y, target.z, Blocks.air);
			world.setBlock(target.x, target.y + 3, target.z, b);
		}
		
	}
	
}
