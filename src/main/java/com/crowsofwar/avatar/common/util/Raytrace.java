package com.crowsofwar.avatar.common.util;

import static java.lang.Math.toRadians;
import static net.minecraft.util.EnumFacing.*;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
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
	public static RaytraceResult getTargetBlock(EntityPlayer player, double range) {
		
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
	public static RaytraceResult getTargetBlock(EntityPlayer player, double range, boolean raycastLiquids) {
		
		double yaw = toRadians(player.rotationYaw);
		double pitch = toRadians(player.rotationPitch);
		
		if (range == -1) range = getReachDistance(player);
		
		Vec3d Vec3d = Vec3d.createVectorHelper(player.posX, player.posY, player.posZ);
		Vec3d Vec3d1 = player.getLookVec();
		Vec3d Vec3d2 = Vec3d.addVector(Vec3d1.xCoord * range, Vec3d1.yCoord * range, Vec3d1.zCoord * range);
		MovingObjectPosition mop = player.worldObj.func_147447_a(Vec3d, Vec3d2, raycastLiquids, false, true);
		
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			return new RaytraceResult(new AvBlockPos(mop.blockX, mop.blockY, mop.blockZ), mop.sideHit);
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
			return ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		} else {
			return AvatarMod.proxy.getPlayerReach();
		}
	}
	
	public static class RaytraceResult {
		
		private final AvBlockPos pos;
		private final int side;
		
		public RaytraceResult(AvBlockPos pos, int side) {
			this.pos = pos;
			this.side = side;
		}
		
		public AvBlockPos getPos() {
			return pos;
		}
		
		public int getSide() {
			return side;
		}
		
		public EnumFacing getDirection() {
			switch (side) {
			case 0:
				return DOWN;
			case 1:
				return UP;
			case 2:
				return NORTH;
			case 3:
				return SOUTH;
			case 4:
				return WEST;
			case 5:
				return EAST;
			}
			return null;
		}
		
	}
	
}
