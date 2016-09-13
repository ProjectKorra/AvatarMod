package com.crowsofwar.avatar.common.util;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Raytrace {
	
	private Raytrace() {}
	
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
	 * Returns the position of the block the player is looking at.
	 * {@link Raytrace.Result#hitSomething() No hit} if the player is not targeting anything in
	 * range, or the information doesn't require raytrace.
	 * 
	 * @param info
	 *            Information of this raytrace
	 */
	public static Result getTargetBlock(EntityPlayer player, Raytrace.Info info) {
		
		if (!info.needsRaytrace()) return new Raytrace.Result();
		
		return getTargetBlock(player, info.getRange(), info.raycastLiquids());
		
	}
	
	/**
	 * Returns the position of the block the player is looking at.
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
		
		if (range == -1) range = getReachDistance(player);
		
		Vector playerPos = Vector.getEyePos(player);
		Vector look = new Vector(player.getLookVec());
		Vector end = playerPos.plus(look.times(range));
		RayTraceResult res = player.worldObj.rayTraceBlocks(playerPos.toMinecraft(), end.toMinecraft(),
				!raycastLiquids, raycastLiquids, true);
		
		if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
			return new Result(new VectorI(res.getBlockPos()), res.sideHit);
		} else {
			return new Result();
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
	
	/**
	 * Returns a raytrace over blocks.
	 * 
	 * @param world
	 *            World
	 * @param start
	 *            Starting position of raytrace
	 * @param rotations
	 *            Euler angles of direction to raytrace, in radians
	 * @param range
	 *            How far to raytrace at most
	 * @param raycastLiquids
	 *            Whether to keep going when liquids are hit
	 */
	public static Result raytrace(World world, Vector start, Vector rotations, double range,
			boolean raycastLiquids) {
		
		RayTraceResult res = world.rayTraceBlocks(start.toMinecraft(),
				start.plus(rotations.times(range)).toMinecraft(), !raycastLiquids, raycastLiquids, true);
		
		if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
			return new Result(new VectorI(res.getBlockPos()), res.sideHit);
		} else {
			return new Result();
		}
	}
	
	public static class Result {
		
		private final boolean hit;
		private final VectorI pos;
		private final EnumFacing side;
		
		public Result() {
			this(null, null);
		}
		
		public Result(VectorI pos, EnumFacing side) {
			this.pos = pos;
			this.side = side;
			this.hit = pos != null;
		}
		
		/**
		 * Get the position of the block hit. Null if hit nothing
		 */
		public VectorI getPos() {
			return pos;
		}
		
		/**
		 * Get the side of the block hit. Null if hit nothing
		 */
		public EnumFacing getSide() {
			return side;
		}
		
		/**
		 * Returns whether the raytrace actually hit something
		 */
		public boolean hitSomething() {
			return hit;
		}
		
	}
	
	/**
	 * Encapsulates information about whether a raytrace is needed.
	 * 
	 * @author CrowsOfWar
	 */
	public static class Info {
		
		private final double range;
		private final boolean needsRaytrace;
		private final boolean raycastLiquids;
		
		/**
		 * Constructs a raytrace information requesting a no raytrace.
		 */
		public Info() {
			this.range = -1;
			this.needsRaytrace = false;
			this.raycastLiquids = false;
		}
		
		/**
		 * Constructs a raytrace information requesting a raytrace with the designated parameters.
		 * 
		 * @param range
		 *            Range of raytrace. If -1, how far player can reach.
		 * @param raycastLiquids
		 *            Whether to keep going when liquids are hit
		 */
		public Info(double range, boolean raycastLiquids) {
			super();
			this.range = range;
			this.needsRaytrace = true;
			this.raycastLiquids = raycastLiquids;
		}
		
		public double getRange() {
			return range;
		}
		
		public boolean needsRaytrace() {
			return needsRaytrace;
		}
		
		public boolean raycastLiquids() {
			return raycastLiquids;
		}
		
	}
	
}
