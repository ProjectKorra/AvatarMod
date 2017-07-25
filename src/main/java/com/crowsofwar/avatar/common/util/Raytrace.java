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

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Raytrace {
	
	private Raytrace() {}
	
	/**
	 * Returns the position of the block the entity is looking at. Null if the
	 * entity is not targeting anything in range. This does not raycast liquids.
	 * 
	 * @param entity
	 *            Bending entity (for players, works both client-side and
	 *            server-side)
	 * @param range
	 *            How far to raytrace. If -1, then it is how far the entity can
	 *            reach.
	 * @return The position of the block that the entity is looking at. May
	 *         differ between server and client.
	 */
	public static Result getTargetBlock(EntityLivingBase entity, double range) {
		
		return getTargetBlock(entity, range, false);
		
	}
	
	/**
	 * Returns the position of the block the entity is looking at.
	 * {@link Raytrace.Result#hitSomething() No hit} if the entity is not
	 * targeting anything in range, or the information doesn't require raytrace.
	 * 
	 * @param info
	 *            Information of this raytrace
	 */
	public static Result getTargetBlock(EntityLivingBase entity, Raytrace.Info info) {
		
		if (!info.needsRaytrace()) return new Raytrace.Result();
		
		if (info.predicateRaytrace()) return predicateRaytrace(entity.world, Vector.getEyePos(entity),
				Vector.getLookRectangular(entity), info.range, info.predicate);
		
		return getTargetBlock(entity, info.getRange(), info.raycastLiquids());
		
	}
	
	/**
	 * Returns the position of the block the entity is looking at.
	 * 
	 * @param entity
	 *            Bending entity (for players, works both client-side and
	 *            server-side)
	 * @param range
	 *            How far to raytrace. If -1, then it is how far the entity can
	 *            reach.
	 * @param raycastLiquids
	 *            Whether liquids are detected in the raycast.
	 * @return The position of the block that the entity is looking at. May
	 *         differ between server and client.
	 */
	public static Result getTargetBlock(EntityLivingBase entity, double range, boolean raycastLiquids) {
		
		if (range == -1) range = getReachDistance(entity);
		
		Vector eyePos = Vector.getEyePos(entity);
		Vector look = new Vector(entity.getLookVec());
		Vector end = eyePos.plus(look.times(range));
		RayTraceResult res = entity.world.rayTraceBlocks(eyePos.toMinecraft(), end.toMinecraft(),
				!raycastLiquids, raycastLiquids, true);
		
		if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
			return new Result(new VectorI(res.getBlockPos()), res.sideHit, new Vector(res.hitVec));
		} else {
			return new Result();
		}
	}
	
	/**
	 * Returns how far the entity can reach.
	 * 
	 * @param entity
	 * @return
	 */
	public static double getReachDistance(EntityLivingBase entity) {
		if (entity instanceof EntityPlayerMP) {
			return 5;
		} else if (entity instanceof EntityPlayer) {
			return AvatarMod.proxy.getPlayerReach();
		} else {
			return 4;
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
			return new Result(new VectorI(res.getBlockPos()), res.sideHit, new Vector(res.hitVec));
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
		
		if (range == -1) range = 3;
		
		Vector currentPosition = start;
		Vector increment = direction.times(0.2);
		while (currentPosition.sqrDist(start) <= range * range) {
			
			BlockPos pos = currentPosition.toBlockPos();
			IBlockState blockState = world.getBlockState(pos);
			if (verify.test(pos, blockState)) {
				return new Result(new VectorI(pos), EnumFacing.DOWN, currentPosition);
			}
			
			currentPosition = currentPosition.plus(increment);
			
		}
		return new Result();
		
	}
	
	public static List<Entity> entityRaytrace(World world, Vector start, Vector direction,
			double maxDistance) {
		return entityRaytrace(world, start, direction, maxDistance, entity -> true);
	}
	
	public static List<Entity> entityRaytrace(World world, Vector start, Vector direction, double maxRange,
			Predicate<Entity> filter) {
		
		// Detect correct range- avoid obstructions from walls
		double range = maxRange;
		Result raytrace = raytrace(world, start, direction, maxRange, true);
		if (raytrace.hitSomething()) {
			Vector stopAt = raytrace.posPrecise;
			range = start.minus(stopAt).magnitude();
		}
		
		List<Entity> hit = new ArrayList<>();
		
		Vector end = start.plus(direction.times(range));
		AxisAlignedBB aabb = new AxisAlignedBB(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		
		for (Entity entity : entities) {
			if (filter.test(entity)) {
				AxisAlignedBB collisionBox = entity.getEntityBoundingBox();
				RayTraceResult result = collisionBox.calculateIntercept(start.toMinecraft(),
						end.toMinecraft());
				if (result != null) {
					hit.add(entity);
				}
			}
		}
		
		return hit;
		
	}
	
	public static class Result {
		
		private final boolean hit;
		private final VectorI pos;
		private final EnumFacing side;
		private final Vector posPrecise;
		
		/**
		 * Creates a raytrace result with no block hit
		 */
		public Result() {
			this(null, null, null);
		}
		
		public Result(VectorI pos, EnumFacing side, Vector posPrecise) {
			this.pos = pos;
			this.side = side;
			this.hit = pos != null;
			this.posPrecise = posPrecise;
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
		
		public Vector getPosPrecise() {
			return posPrecise;
		}
		
		public static Raytrace.Result fromBytes(ByteBuf buf) {
			boolean hit = buf.readBoolean();
			EnumFacing side = null;
			VectorI pos = null;
			Vector posPrecise = null;
			if (hit) {
				side = EnumFacing.values()[buf.readInt()];
				pos = VectorI.fromBytes(buf);
				posPrecise = Vector.fromBytes(buf);
			}
			return new Result(pos, side, posPrecise);
		}
		
		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(hit);
			if (hit) {
				buf.writeInt(side.ordinal());
				pos.toBytes(buf);
				posPrecise.toBytes(buf);
			}
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
		private BiPredicate<BlockPos, IBlockState> predicate;
		
		/**
		 * Constructs a raytrace information requesting a no raytrace.
		 */
		public Info() {
			this.range = -1;
			this.needsRaytrace = false;
			this.raycastLiquids = false;
			this.predicate = null;
		}
		
		/**
		 * Constructs a raytrace information requesting a raytrace with the
		 * designated parameters.
		 * 
		 * @param range
		 *            Range of raytrace. If -1, is how far entity can reach.
		 * @param raycastLiquids
		 *            Whether to keep going when liquids are hit
		 */
		public Info(double range, boolean raycastLiquids) {
			super();
			this.range = range;
			this.needsRaytrace = true;
			this.raycastLiquids = raycastLiquids;
			this.predicate = null;
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
		
		public boolean predicateRaytrace() {
			return predicate != null;
		}
		
		public BiPredicate<BlockPos, IBlockState> getPredicate() {
			return predicate;
		}
		
		public void setPredicate(BiPredicate<BlockPos, IBlockState> predicate) {
			this.predicate = predicate;
		}
		
	}
	
}
