/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.util;

import java.util.function.BiPredicate;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Raytrace {
	
	private Raytrace() {}
	
	/**
	 * Returns the position of the block the player is looking at. Null if the
	 * player is not targeting anything in range. This does not raycast liquids.
	 * 
	 * @param player
	 *            Player entity (works both client-side and server-side)
	 * @param range
	 *            How far to raytrace. If -1, then it is how far the player can
	 *            reach.
	 * @return The position of the block that the player is looking at. May
	 *         differ between server and client.
	 */
	public static Result getTargetBlock(EntityPlayer player, double range) {
		
		return getTargetBlock(player, range, false);
		
	}
	
	/**
	 * Returns the position of the block the player is looking at.
	 * {@link Raytrace.Result#hitSomething() No hit} if the player is not
	 * targeting anything in range, or the information doesn't require raytrace.
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
	 *            How far to raytrace. If -1, then it is how far the player can
	 *            reach.
	 * @param raycastLiquids
	 *            Whether liquids are detected in the raycast.
	 * @return The position of the block that the player is looking at. May
	 *         differ between server and client.
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
	 * @param direction
	 *            Normalized direction vector of where to go
	 * @param range
	 *            How far to raytrace at most
	 * @param raycastLiquids
	 *            Whether to keep going when liquids are hit
	 */
	public static Result raytrace(World world, Vector start, Vector direction, double range,
			boolean raycastLiquids) {
		
		RayTraceResult res = world.rayTraceBlocks(start.toMinecraft(),
				start.plus(direction.times(range)).toMinecraft(), !raycastLiquids, raycastLiquids, true);
		
		if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
			return new Result(new VectorI(res.getBlockPos()), res.sideHit);
		} else {
			return new Result();
		}
	}
	
	/**
	 * Custom raytrace which allows you to specify a (Bi)Predicate to determine
	 * if the block has been hit. Unfortunately, this implementation does not
	 * correctly report the side hit (always is {@link EnumFacing#DOWN}).
	 * 
	 * @param world
	 *            The world
	 * @param start
	 *            Starting position to raytrace
	 * @param direction
	 *            Normalized vector to specify direction
	 * @param range
	 *            How many meters (blocks) to raytrace
	 * @param verify
	 *            A BiPredicate used to verify if that block is correct
	 */
	public static Result predicateRaytrace(World world, Vector start, Vector direction, double range,
			BiPredicate<BlockPos, IBlockState> verify) {
		
		Vector currentPosition = start.copy();
		Vector increment = direction.times(0.2);
		while (currentPosition.sqrDist(start) <= range * range) {
			
			BlockPos pos = currentPosition.toBlockPos();
			IBlockState blockState = world.getBlockState(pos);
			if (verify.test(pos, blockState)) {
				return new Result(new VectorI(pos), EnumFacing.DOWN);
			}
			
			currentPosition.add(increment);
			
		}
		return new Result();
		
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
		 * Constructs a raytrace information requesting a raytrace with the
		 * designated parameters.
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
