package com.maxandnoah.avatar.common.bending;

import com.maxandnoah.avatar.common.AvatarControlList;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.data.PlayerState;
import com.maxandnoah.avatar.common.entity.EntityFloatingBlock;
import com.maxandnoah.avatar.common.util.BlockPos;
import com.maxandnoah.avatar.common.util.VectorUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Earthbending implements BendingController {
	//EntityFallingBlock
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
			if (target != null) {
				Block block = world.getBlock(target.x, target.y, target.z);
				world.setBlock(target.x, target.y, target.z, Blocks.air);
				
				EntityFloatingBlock floating = new EntityFloatingBlock(world, block);
				floating.setPosition(target.x + 0.5, target.y, target.z + 0.5);
				
				Vec3 playerPos = VectorUtils.getEntityPos(player);
				Vec3 floatingPos = VectorUtils.getEntityPos(floating);
				Vec3 force = VectorUtils.minus(floatingPos, playerPos);
				force.normalize();
				VectorUtils.mult(force, 2);
//				floating.addForce(force);
				floating.lift();
				
//				floating.setGravityEnabled(true);
				world.spawnEntityInWorld(floating);
			}
		}
		
	}
	
}
