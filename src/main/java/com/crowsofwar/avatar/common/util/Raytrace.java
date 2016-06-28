package com.crowsofwar.avatar.common.util;

import static java.lang.Math.toRadians;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class Raytrace {
	
	/**
	 * Returns the position of the block the player is looking at.
	 * Null if the player is not targeting anything in range.
	 * 
	 * @param player Player entity (works both client-side and server-side)
	 * @param range How far to raytrace. If -1, then it is how
	 * far the player can reach.
	 * @return The position of the block that the player is looking
	 * at. May differ between server and client.
	 */
	public static BlockPos getTargetBlock(EntityPlayer player, double range) {
		
		double yaw = toRadians(player.rotationYaw);
		double pitch = toRadians(player.rotationPitch);
		
		if (range == -1) range = getReachDistance(player);
		
		Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        Vec3 vec31 = player.getLookVec();
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range,
        		vec31.zCoord * range);
        MovingObjectPosition mop = player.worldObj.func_147447_a(vec3, vec32, false,
        		false, true);
        
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			return new BlockPos(mop.blockX, mop.blockY, mop.blockZ);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns how far the player can reach.
	 * @param player
	 * @return
	 */
	public static double getReachDistance(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			return ((EntityPlayerMP) player).theItemInWorldManager
					.getBlockReachDistance();
		} else {
			return AvatarMod.proxy.getPlayerReach();
		}
	}
	
}
