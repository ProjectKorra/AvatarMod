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

package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.ICustomHitbox;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Raytrace {

	//TODO: Some way to detect raytraces hitting each other!


	private Raytrace() {
	}

	/**
	 * Returns the position of the block the entity is looking at. Null if the
	 * entity is not targeting anything in range. This does not raycast liquids.
	 *
	 * @param entity Bending entity (for players, works both client-side and
	 *               server-side)
	 * @param range  How far to raytrace. If -1, then it is how far the entity can
	 *               reach.
	 * @return The position of the block that the entity is looking at. May
	 * differ between server and client.
	 */
	public static Result getTargetBlock(EntityLivingBase entity, double range) {

		return getTargetBlock(entity, range, false);

	}

	/**
	 * Returns the position of the block the entity is looking at.
	 * {@link Raytrace.Result#hitSomething() No hit} if the entity is not
	 * targeting anything in range, or the information doesn't require raytrace.
	 *
	 * @param info Information of this raytrace
	 */
	public static Result getTargetBlock(EntityLivingBase entity, Raytrace.Info info) {

		if (!info.needsRaytrace()) return new Raytrace.Result();

		if (info.predicateRaytrace())
			return predicateRaytrace(entity.world, Vector.getEyePos(entity),
					Vector.getLookRectangular(entity), info.range, info.predicate);

		return getTargetBlock(entity, info.getRange(), info.raycastLiquids());

	}

	/**
	 * Returns the position of the block the entity is looking at.
	 *
	 * @param entity         Bending entity (for players, works both client-side and
	 *                       server-side)
	 * @param range          How far to raytrace. If -1, then it is how far the entity can
	 *                       reach.
	 * @param raycastLiquids Whether liquids are detected in the raycast.
	 * @return The position of the block that the entity is looking at. May
	 * differ between server and client.
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
	 * @param world          World
	 * @param start          Starting position of raytrace
	 * @param direction      Normalized direction vector of where to go
	 * @param range          How far to raytrace at most
	 * @param raycastLiquids Whether to keep going when liquids are hit
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
	 * Returns a raytrace over blocks.
	 *
	 * @param world          World
	 * @param start          Starting position of raytrace
	 * @param direction      Normalized direction vec3d of where to go
	 * @param range          How far to raytrace at most
	 * @param raycastLiquids Whether to keep going when liquids are hit
	 */
	public static Result raytrace(World world, Vec3d start, Vec3d direction, double range,
								  boolean raycastLiquids) {

		RayTraceResult res = world.rayTraceBlocks(start,
				start.add(direction.scale(range)), !raycastLiquids, raycastLiquids, true);

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
	 * @param world     The world
	 * @param start     Starting position to raytrace
	 * @param direction Normalized vector to specify direction
	 * @param range     How many meters (blocks) to raytrace
	 * @param verify    A BiPredicate used to verify if that block is correct
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

	public static List<Entity> entityRaytrace(World world, Vec3d start, Vec3d direction,
											  double maxDistance) {
		return entityRaytrace(world, start, direction, maxDistance, entity -> true);
	}

	public static List<Entity> entityRaytrace(World world, Vec3d start, Vec3d direction, double maxRange,
											  Predicate<Entity> filter) {

		// Detect correct range- avoid obstructions from walls
		double range = maxRange;
		Result raytrace = raytrace(world, start, direction, maxRange, true);
		if (raytrace.hitSomething()) {
			Vec3d stopAt = raytrace.posPrecise.toMinecraft();
			range = AvatarUtils.getMagnitude(start.subtract(stopAt));
		}

		List<Entity> hit = new ArrayList<>();

		Vec3d end = start.add(direction.scale(range));
		AxisAlignedBB aabb = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);

		for (Entity entity : entities) {
			if (filter.test(entity)) {
				AxisAlignedBB collisionBox = entity.getEntityBoundingBox();
				RayTraceResult result = collisionBox.calculateIntercept(start, end);
				if (result != null) {
					hit.add(entity);
				}
			}
		}

		return hit;

	}

	public static List<Entity> entityRaytrace(World world, Vector start, Vector direction, float borderSize,
											  double maxDistance) {
		return entityRaytrace(world, start, direction, maxDistance, borderSize, entity -> true);
	}

	public static List<Entity> entityRaytrace(World world, Vector start, Vector direction, double maxRange, float borderSize,
											  Predicate<? super Entity> filter) {

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
				float entBorder = entity.getCollisionBorderSize();
				AxisAlignedBB collisionBox = entity.getEntityBoundingBox();
				collisionBox = collisionBox.grow(entBorder);
				collisionBox = collisionBox.grow(borderSize);
				RayTraceResult result = collisionBox.calculateIntercept(start.toMinecraft(),
						end.toMinecraft());
				if (result != null) {
					hit.add(entity);
				}
			}
		}

		return hit;

	}

	public static List<Entity> entityRaytrace(World world, Vec3d start, Vec3d direction, float borderSize,
											  double maxDistance) {
		return entityRaytrace(world, start, direction, maxDistance, borderSize, entity -> true);
	}

	public static List<Entity> entityRaytrace(World world, Vec3d start, Vec3d direction, double maxRange, float borderSize,
											  Predicate<? super Entity> filter) {

		// Detect correct range- avoid obstructions from walls
		double range = maxRange;
		Result raytrace = raytrace(world, start, direction, maxRange, true);
		if (raytrace.hitSomething()) {
			Vec3d stopAt = raytrace.posPrecise.toMinecraft();
			range = AvatarUtils.getMagnitude(start.subtract(stopAt));
		}

		List<Entity> hit = new ArrayList<>();

		Vec3d end = start.add(direction.scale(range));
		AxisAlignedBB aabb = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);

		for (Entity entity : entities) {
			if (filter.test(entity)) {
				float entBorder = entity.getCollisionBorderSize();
				AxisAlignedBB collisionBox = entity.getEntityBoundingBox();
				collisionBox = collisionBox.grow(entBorder);
				collisionBox = collisionBox.grow(borderSize);
				RayTraceResult result = collisionBox.calculateIntercept(start, end);
				if (result != null) {
					hit.add(entity);
				}
			}
		}

		return hit;

	}

	//Vanilla raytrace method, will be cleaned up in the rewrite

	/**
	 * /**
	 * Performs a ray trace for blocks and entities, starting at the given origin and finishing at the given endpoint.
	 * As of wizardry 4.2, the ray tracing methods have been rewritten to be more user-friendly and implement proper
	 * aim assist.
	 * <p></p>
	 * <i>N.B. It is possible to ignore entities entirely by passing in a {@code Predicate} that is always false;
	 * however, in this specific case it is more efficient to use
	 * {@link World#rayTraceBlocks(Vec3d, Vec3d, boolean, boolean, boolean)} or one of its overloads.</i>
	 *
	 * @param world                  The world in which to perform the ray trace.
	 * @param origin                 A vector representing the coordinates of the start point of the ray trace.
	 * @param endpoint               A vector representing the coordinates of the finish point of the ray trace.
	 * @param aimAssist              In addition to direct hits, the ray trace will also hit entities that are up to this distance
	 *                               from its path. For a normal ray trace, this should be 0. Values greater than 0 will give an 'aim assist' effect.
	 * @param hitLiquids             Whether liquids should be ignored when ray tracing blocks
	 * @param ignoreUncollidables    Whether blocks with no collisions should be ignored
	 * @param returnLastUncollidable If blocks with no collisions are ignored, whether to return the last one (useful if,
	 *                               for example, you want to replace snow layers or tall grass)
	 * @param entityType             The class of entities to include; all other entities will be ignored.
	 * @param filter                 A {@link Predicate} which filters out entities that can be ignored; often used to exclude the
	 *                               player that is performing the ray trace.
	 * @return A {@link RayTraceResult} representing the object that was hit, which may be an entity, a block or
	 * nothing. Returns {@code null} only if the origin and endpoint are within the same block and no entity was hit.
	 */
	// Interestingly enough, aimAssist can be negative, which means hits have to be in the middle of entities!
	//TODO: Add a custom raytrace entity when spawning entities that has a custom hitbox! This will enable raytrace detection and particle collision!
	@Nullable
	public static RayTraceResult rayTrace(World world, Vec3d origin, Vec3d endpoint, float aimAssist,
										  boolean hitLiquids, boolean ignoreUncollidables, boolean returnLastUncollidable, Class<? extends Entity> entityType, Predicate<? super Entity> filter) {

		// 1 is the standard amount of extra search volume, and aim assist needs to increase this further as well as
		// expanding the entities' bounding boxes.
		float borderSize = 1 + aimAssist;

		// The AxisAlignedBB constructor accepts min/max coords in either order.
		AxisAlignedBB searchVolume = new AxisAlignedBB(origin.x, origin.y, origin.z, endpoint.x, endpoint.y, endpoint.z)
				.grow(borderSize, borderSize, borderSize);

		// Gets all of the entities in the bounding box that could be collided with.
		List<Entity> entities = world.getEntitiesWithinAABB(entityType, searchVolume);
		// Applies the given filter to remove entities that should be ignored.
		entities.removeIf(filter);

		// Finds the first block hit by the ray trace, if any.
		RayTraceResult result = world.rayTraceBlocks(origin, endpoint, hitLiquids, ignoreUncollidables, returnLastUncollidable);

		// Clips the entity search range to the part of the ray trace before the block hit, if it hit a block.
		if (result != null) {
			endpoint = result.hitVec;
		}

		// Search variables
		Entity closestHitEntity = null;
		Vec3d closestHitPosition = endpoint;
		AxisAlignedBB entityBounds;
		Vec3d intercept = null;

		// Iterates through all the entities
		for (Entity entity : entities) {

			// I'd like to add the following line so we can, for example, use greater telekinesis through a
			// ring of fire, but doing so will stop forcefields blocking particles
			//if(!entity.canBeCollidedWith()) continue;

			float fuzziness = entity instanceof EntityLiving ? aimAssist : 0; // Only living entities have aim assist

			if (entity instanceof ICustomHitbox) { // Custom hitboxes
				intercept = ((ICustomHitbox) entity).calculateIntercept(origin, endpoint, fuzziness);

			} else { // Normal hit detection

				entityBounds = entity.getEntityBoundingBox();

				if (entityBounds != null) {

					// This is zero for everything except fireballs...
					float entityBorderSize = entity.getCollisionBorderSize();
					// ... meaning the following line does nothing in all other cases.
					// -> Added the non-zero check to prevent unnecessary AABB object creation.
					if (entityBorderSize != 0)
						entityBounds = entityBounds.grow(entityBorderSize, entityBorderSize, entityBorderSize);

					// Aim assist expands the bounding box to hit entities within the specified distance of the ray trace.
					if (fuzziness != 0) entityBounds = entityBounds.grow(fuzziness, fuzziness, fuzziness);

					// Finds the first point at which the ray trace intercepts the entity's bounding box, if any.
					RayTraceResult hit = entityBounds.calculateIntercept(origin, endpoint);
					if (hit != null) intercept = hit.hitVec;
				}
			}

			// If the ray trace hit the entity...
			if (intercept != null) {
				// Decides whether the entity that was hit is the closest so far, and if so, overwrites the old one.
				float currentHitDistance = (float) intercept.distanceTo(origin);
				float closestHitDistance = (float) closestHitPosition.distanceTo(origin);
				if (currentHitDistance < closestHitDistance) {
					closestHitEntity = entity;
					closestHitPosition = intercept;
				}
			}
		}

		// If the ray trace hit an entity, return that entity; otherwise return the result of the block ray trace.
		if (closestHitEntity != null) {
			result = new RayTraceResult(closestHitEntity, closestHitPosition);
		}

		return result;
	}

	@Nullable
	public static RayTraceResult standardEntityRayTrace(World world, Entity entity, Entity spellEntity, Vec3d startPos, Vec3d endPos,
														float borderSize, boolean transparentBlocks, Predicate<? super Entity> filter) {
		filter = filter.and(entity1 -> entity1 == entity);
		if (spellEntity != null)
			filter = filter.and(entity1 -> entity1 == spellEntity);
		return rayTrace(world, startPos, endPos,
				borderSize, false, transparentBlocks, transparentBlocks, EntityLiving.class, filter);
	}

	/**
	 * @param world         The world the raytrace is in.
	 * @param bender        The bender of the spell. This is so mobs don't attack each other when you use raytraces from mobs.
	 *                      All damage is done by the bender.
	 * @param startPos      Where the raytrace starts.
	 * @param endPos        Where the raytrace ends.
	 * @param borderSize    The width of the raytrace.
	 * @param abilityEntity The entity that's using this method, if applicable. If this method is directly used in a spell, just make this null.
	 * @param damageSource  The damage source.
	 * @param damage        The amount of damage.
	 * @param knockBack     The amount of knockback.
	 * @param setFire       Whether to set an enemy on fire.
	 * @param fireTime      How long to set an enemy on fire.
	 */

	public static void handlePiercingBeamCollision(World world, EntityLivingBase bender, Vec3d startPos, Vec3d endPos, float borderSize, Entity abilityEntity, DamageSource damageSource,
												   float damage, Vec3d knockBack, boolean setFire, int fireTime, float radius) {
		/*HashSet<Entity> excluded = new HashSet<>();
		RayTraceResult result = standardEntityRayTrace(world, bender, abilityEntity, startPos, endPos, borderSize, false, excluded);
		if (result != null && result.entityHit instanceof EntityLivingBase) {
			EntityLivingBase hit = (EntityLivingBase) result.entityHit;
			if (setFire) {
				hit.setFire(fireTime);
			}
			hit.attackEntityFrom(damageSource, damage);
			hit.motionX += knockBack.x;
			hit.motionY += knockBack.y;
			hit.motionZ += knockBack.z;
			AvatarUtils.afterVelocityAdded(hit);
			Vec3d pos = result.hitVec;
			AxisAlignedBB hitBox = new AxisAlignedBB(pos.x + radius, pos.y + radius, pos.z + radius, pos.x - radius, pos.y - radius, pos.z - radius);
			List<Entity> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox);
			excluded.add(hit);
			nearby.remove(excluded);
			//This is so it doesn't count the entity that was hit by the raytrace and mess up the chain
			if (!nearby.isEmpty()) {
				for (Entity e : nearby) {
					if (e != bender && e != hit && !excluded.contains(e) && e.getTeam() != bender.getTeam()) {
						if (setFire) {
							e.setFire(fireTime);
						}
						e.attackEntityFrom(damageSource, damage);
						e.motionX += knockBack.x;
						e.motionY += knockBack.y;
						e.motionZ += knockBack.z;
						AvatarUtils.afterVelocityAdded(e);
						excluded.add(e);
					}
				}
			} else {
				handlePiercingBeamCollision(world, bender, pos, endPos, borderSize, abilityEntity, damageSource, damage, knockBack, setFire, fireTime, radius);

			}

		}**/
	}

	//TODO: Fix raytraces returning entities that you own
	public static Predicate<Entity> ignoreEntityFilter(Entity entity) {
		return entity1 -> entity1 == entity;
	}

	public static Predicate<Entity> ignoreBenderFilter(Entity entity) {
		return entity1 -> entity1 == entity || entity1 instanceof AvatarEntity && ((AvatarEntity) entity1).getOwner() == entity;
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

		/**
		 * Get the position of the block hit. Null if hit nothing
		 */
		@Nullable
		public VectorI getPos() {
			return pos;
		}

		/**
		 * Get the side of the block hit. Null if hit nothing
		 */
		@Nullable
		public EnumFacing getSide() {
			return side;
		}

		/**
		 * Returns whether the raytrace actually hit something
		 */
		public boolean hitSomething() {
			return hit;
		}

		@Nullable
		public Vector getPosPrecise() {
			return posPrecise;
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
		 * @param range          Range of raytrace. If -1, is how far entity can reach.
		 * @param raycastLiquids Whether to keep going when liquids are hit
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
