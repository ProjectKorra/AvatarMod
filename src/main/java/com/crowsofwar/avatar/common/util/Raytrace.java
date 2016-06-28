package com.crowsofwar.avatar.common.util;

import static java.lang.Math.toRadians;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.util.Vec3;
import static net.minecraftforge.common.util.ForgeDirection.*;

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
	public static RaytraceResult getTargetBlock(EntityPlayer player, double range) {
		
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
			return new RaytraceResult(new BlockPos(mop.blockX, mop.blockY, mop.blockZ), mop.sideHit);
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
	
	public static class RaytraceResult {
		
		private final BlockPos pos;
		private final int side;
		
		public RaytraceResult(BlockPos pos, int side) {
			this.pos = pos;
			this.side = side;
		}

		public BlockPos getPos() {
			return pos;
		}

		public int getSide() {
			return side;
		}
		
		public ForgeDirection getDirection() {
			switch (side) {
				case 0: return DOWN;
				case 1: return UP;
				case 2: return NORTH;
				case 3: return SOUTH;
				case 4: return WEST;
				case 5: return EAST;
			}
			return null;
		}
		
	}
	
}
