package com.maxandnoah.avatar.common.bending;

import com.maxandnoah.avatar.common.AvatarAbility;
import com.maxandnoah.avatar.common.controls.AvatarControl;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.data.PlayerState;
import com.maxandnoah.avatar.common.util.BlockPos;

import static com.maxandnoah.avatar.common.controls.AvatarControl.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Firebending implements IBendingController {
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public int getID() {
		return BendingManager.BENDINGID_FIREBENDING;
	}
	
	@Override
	public void onAbility(AvatarAbility ability, AvatarPlayerData data) {
		PlayerState ps = data.getState();
		EntityPlayer player = ps.getPlayerEntity();
		World world = player.worldObj;
		
		if (ability == AvatarAbility.ACTION_LIGHT_FIRE) {
			BlockPos looking = ps.verifyClientLookAtBlock(-1, 5);
			if (world.getBlock(looking.x, looking.y + 1, looking.z) == Blocks.air)
				world.setBlock(looking.x, looking.y + 1, looking.z, Blocks.fire);
		}
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new FirebendingState();
	}
	
	@Override
	public void onUpdate(AvatarPlayerData data) {
		
	}
	
	@Override
	public AvatarAbility getAbility(AvatarPlayerData data, AvatarControl input) {
		return AvatarAbility.NONE;
	}
	
}
