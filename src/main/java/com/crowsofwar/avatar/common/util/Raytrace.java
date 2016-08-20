package com.crowsofwar.avatar.common.util;

import static java.lang.Math.toRadians;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Raytrace {
	
	/**
	 * Returns the position of the block the player is looking at. Null if the player is not
	 * targeting anything in range. This does not raycast liquids.
	 * 
	 * @param player
	 *            Player entity (works both client-side and server-side)
	 * @param range
	 *            How far to raytrace. If -1, then it is how far the player can reach.
	 * @return The position of the block that the player is looking at. May differ between server
	 *         and client.
	 */
	public static Result getTargetBlock(EntityPlayer player, double range) {
		
		return getTargetBlock(player, range, false);
		
	}
	
	/**
	 * Returns the position of the block the player is looking at. Null if the player is not
	 * targeting anything in range.
	 * 
	 * @param player
	 *            Player entity (works both client-side and server-side)
	 * @param range
	 *            How far to raytrace. If -1, then it is how far the player can reach.
	 * @param raycastLiquids
	 *            Whether liquids are detected in the raycast.
	 * @return The position of the block that the player is looking at. May differ between server
	 *         and client.
	 */
	public static Result getTargetBlock(EntityPlayer player, double range, boolean raycastLiquids) {
		
		double yaw = toRadians(player.rotationYaw);
		double pitch = toRadians(player.rotationPitch);
		
		if (range == -1) range = getReachDistance(player);
		
		Vec3d Vec3d = new Vec3d(player.posX, player.posY, player.posZ);
		Vec3d Vec3d1 = player.getLookVec();
		Vec3d Vec3d2 = Vec3d.addVector(Vec3d1.xCoord * range, Vec3d1.yCoord * range, Vec3d1.zCoord * range);
		RayTraceResult res = player.worldObj.rayTraceBlocks(Vec3d, Vec3d2, raycastLiquids, false, true);
		
		if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
			return new Result(new VectorI(res.getBlockPos()), res.sideHit);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns how far the player can reach.
	 * 
	 * @param player
	 * @return
	 */
	public static double getReachDistance(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			// TODO [1.10] how does reach distance work?
			return 5;
		} else {
			return AvatarMod.proxy.getPlayerReach();
		}
	}
	
	public static class Result {
		
		private final VectorI pos;
		private final EnumFacing side;
		
		public Result(VectorI pos, EnumFacing side) {
			this.pos = pos;
			this.side = side;
		}
		
		public VectorI getPos() {
			return pos;
		}
		
		public EnumFacing getSide() {
			return side;
		}
		
	}
	
}
