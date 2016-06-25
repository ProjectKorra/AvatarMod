package com.maxandnoah.avatar.common.bending;

import static com.maxandnoah.avatar.common.util.VectorUtils.times;

import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.client.controls.AvatarKeybinding;
import com.maxandnoah.avatar.client.controls.AvatarOtherControl;
import com.maxandnoah.avatar.common.AvatarAbility;
import com.maxandnoah.avatar.common.AvatarControl;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.data.PlayerState;
import com.maxandnoah.avatar.common.entity.EntityFloatingBlock;
import com.maxandnoah.avatar.common.network.packets.PacketCControllingBlock;
import com.maxandnoah.avatar.common.util.BlockPos;
import com.maxandnoah.avatar.common.util.Raytrace;
import com.maxandnoah.avatar.common.util.VectorUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Earthbending implements IBendingController {
	
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
	public void onAbility(AvatarAbility ability, AvatarPlayerData data) {
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		World world = player.worldObj;
		EarthbendingState ebs = (EarthbendingState) data.getBendingState();
		
		if (ability == AvatarAbility.ACTION_TOGGLE_BENDING) {
			if (ebs.getPickupBlock() != null) {
				ebs.getPickupBlock().drop();
				ebs.setPickupBlock(null);//TODO sync this to the client
			} else {
				BlockPos target = state.verifyClientLookAtBlock(-1, 5);
				if (target != null) {
					Block block = world.getBlock(target.x, target.y, target.z);
					world.setBlock(target.x, target.y, target.z, Blocks.air);
					
					EntityFloatingBlock floating = new EntityFloatingBlock(world, block);
					floating.setPosition(target.x + 0.5, target.y, target.z + 0.5);
					
					double dist = 2.5;
					Vec3 force = Vec3.createVectorHelper(0, Math.sqrt(19.62*dist), 0);
					floating.addForce(force);
					floating.setGravityEnabled(true);
					floating.setCanFall(false);
//					floating.posY += 2;
					
					world.spawnEntityInWorld(floating);
					
					ebs.setPickupBlock(floating);
					
					AvatarMod.network.sendTo(new PacketCControllingBlock(floating.getID()), (EntityPlayerMP) player);
					
				}
			}
		}
		if (ability == AvatarAbility.ACTION_THROW_BLOCK) {
			EntityFloatingBlock floating = ebs.getPickupBlock();
			if (floating != null) {
				float yaw = (float) Math.toRadians(player.rotationYaw);
				float pitch = (float) Math.toRadians(player.rotationPitch);
				
				// Calculate force and everything
				Vec3 lookDir = VectorUtils.fromYawPitch(yaw, pitch);
				floating.addForce(times(lookDir, 20));
				
				floating.drop();
				ebs.setPickupBlock(null); // TODO Tell the client that the block has been dropped
				
			}
		}
		
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new EarthbendingState(data);
	}
	
	@Override
	public void onUpdate(AvatarPlayerData data) {
		EarthbendingState state = (EarthbendingState) data.getBendingState();
		if (state != null) {
			EntityPlayer player = data.getState().getPlayerEntity();
			EntityFloatingBlock floating = state.getPickupBlock();
			
			if (floating != null && floating.ticksExisted > 60) {
				double yaw = Math.toRadians(player.rotationYaw);
				double pitch = Math.toRadians(player.rotationPitch);
				Vec3 forward = VectorUtils.fromYawPitch(yaw, pitch);
				Vec3 eye = VectorUtils.getEntityPos(player);
				eye.yCoord += 1.2;
				Vec3 target = VectorUtils.plus(VectorUtils.times(forward, 2), eye);
				
				floating.setPosition(target.xCoord, target.yCoord, target.zCoord);
			}
			
		}
	}
	
}
