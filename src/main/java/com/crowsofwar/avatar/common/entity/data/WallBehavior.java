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

package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.PULL_WALL;

/**
 * @author CrowsOfWar
 */
public abstract class WallBehavior extends Behavior<EntityWallSegment> {

	public static DataSerializer<WallBehavior> SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static void register() {
		DataSerializers.registerSerializer(SERIALIZER);
		registerBehavior(Drop.class);
		registerBehavior(Place.class);
		registerBehavior(Rising.class);
		registerBehavior(Waiting.class);
		registerBehavior(Push.class);
		registerBehavior(Pull.class);
	}

	public static class Drop extends WallBehavior {

		@Override
		public Behavior onUpdate(EntityWallSegment entity) {

			if (entity == null) return this;
			if (entity.getWall() == null) return this;

			entity.addVelocity(Vector.DOWN.times(7.0 / 20));

			// Check everything is on the ground
			int nbOnGround = 0;
			for (int i = 0; i < 7; i++) {
				EntityWallSegment current = entity.getWall().getSegment(i);
				if (current != null && !current.onGround) nbOnGround++;
			}

			// Drop them if they are
			if (nbOnGround == 0) {
				entity.dropBlocks();
				entity.setDead();
			}

			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
		}

		@Override
		public void toBytes(PacketBuffer buf) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

	public static class Place extends WallBehavior {

		private int ticks = 0;

		@Override
		public Behavior onUpdate(EntityWallSegment entity) {

			if (entity == null) return this;
			if (entity.getWall() == null) return this;

			ticks++;
			// Prevents some glitches
			if (ticks == 2) {
				entity.setVelocity(Vector.ZERO);
				entity.getWall().setDropTypePlace(true);
				entity.getWall().setDead();
			}
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
		}

		@Override
		public void toBytes(PacketBuffer buf) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

	public static class Rising extends WallBehavior {

		private int ticks = 0;

		@Override
		public Behavior onUpdate(EntityWallSegment entity) {

			if (entity == null) return this;
			if (entity.getWall() == null) return this;

			// not 0 since client missed 0th tick
			if (ticks == 1) {

				int maxHeight = 0;
				for (int i = 0; i < 5; i++) {
					EntityWallSegment seg = entity.getWall().getSegment(i);
					if (seg.height > maxHeight)
						maxHeight = (int) seg.height;
				}

				entity.motionY = STATS_CONFIG.wallMomentum / 5 * maxHeight / 20;

			} else {
				entity.motionY *= 0.9;
			}

			// For some reason, the same entity instance is on server/client,
			// but has different world reference when this is called...?
			if (!entity.world.isRemote) ticks++;

			return ticks > 5 && entity.velocity().y() <= 0.2 ? new Waiting() : this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
		}

		@Override
		public void toBytes(PacketBuffer buf) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

	public static class Waiting extends WallBehavior {

		private int ticks = 0;

		@Override
		public Behavior onUpdate(EntityWallSegment entity) {

			if (entity == null) return this;
			if (entity.getWall() == null) return this;

			entity.setVelocity(Vector.ZERO);
			ticks++;

			return ticks >= STATS_CONFIG.wallWaitTime * 20 ? new Drop() : this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
		}

		@Override
		public void toBytes(PacketBuffer buf) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

	public static class Push extends WallBehavior {

		private int ticks = 0;
		private double lastApplied;

		@Override
		public Behavior onUpdate(EntityWallSegment entity) {

			if (entity == null) return this;
			if (entity.getWall() == null) return this;

			ticks++;

			// Get in which direction the wall should be pushed
			EnumFacing cardinalToPush = entity.getDirection();

			// Safety check
			if (cardinalToPush == null) return this;

			entity.setRestrictToVertical(false);

			if (ticks == 1) {
				// Save the position so that pulling can work
				entity.setInitialPos(new Vector(entity.getPositionVector()));

				// We want the wall to move... So it needs to be a little higher
				entity.motionY = 0.1;

				int pushDistance = 4;
				double velocity = STATS_CONFIG.wallMomentum / 5 * pushDistance / 20;
				lastApplied = velocity;
				AvatarEntityUtils.applyMotionToEntityInDirection(entity, cardinalToPush, velocity);

				// Consume Chi.
				BendingData.get(entity.getOwner()).chi().consumeChi(STATS_CONFIG.chiPushWall);
			} else {
				// Prevent it from moving on the Y axis
				entity.motionY = 0;

				double velocity = lastApplied * 0.9;
				AvatarEntityUtils.applyMotionToEntityInDirection(entity, cardinalToPush, velocity);
				lastApplied = velocity;
			}

			// Push entities that touches the wall
			List<Entity> collidingEntities = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity,
					entity.getCollisionBox(entity));

			if (collidingEntities.size() > 0) {
				for (Entity current : collidingEntities) {
					AvatarEntityUtils.applyMotionToEntityInDirection(current, cardinalToPush, 0.4);
				}
			}

			boolean done = ticks > 50;

			if (done) {
				BendingData.get(entity.getOwner()).addStatusControl(PULL_WALL);
			}

			return done ? new Waiting() : this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
		}

		@Override
		public void toBytes(PacketBuffer buf) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

	public static class Pull extends WallBehavior {

		private int ticks = 0;
		private double lastApplied;

		@Override
		public Behavior onUpdate(EntityWallSegment entity) {

			if (entity == null) return this;
			if (entity.getWall() == null) return this;

			ticks++;

			// Get in which direction the wall should be pulled. Needs to be reversed later
			// by -velocity
			EnumFacing cardinalToPush = entity.getDirection();

			// Safety check
			if (cardinalToPush == null) return this;

			entity.setRestrictToVertical(false);

			if (ticks == 1) {
				// Done so that we know how far it is from where it spawned...
				double pushDistance = new Vector(entity.getPositionVector()).withY(0)
						.dist(entity.getInitialPos().withY(0));

				double velocity = STATS_CONFIG.wallMomentum / 5 * pushDistance / 20;
				lastApplied = velocity;
				AvatarEntityUtils.applyMotionToEntityInDirection(entity, cardinalToPush, -velocity);

				// Consume Chi.
				BendingData.get(entity.getOwner()).chi().consumeChi(STATS_CONFIG.chiPushWall * 2);
			} else {
				// Prevent it from moving on the Y axis
				entity.motionY = 0;

				double velocity = lastApplied * 0.9;
				AvatarEntityUtils.applyMotionToEntityInDirection(entity, cardinalToPush, -velocity);
				lastApplied = velocity;
			}

			// Push entities that touches the wall
			List<Entity> collidingEntities = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity,
					entity.getCollisionBox(entity));

			if (collidingEntities.size() > 0) {
				for (Entity current : collidingEntities) {
					AvatarEntityUtils.applyMotionToEntityInDirection(current, cardinalToPush, -0.4);
				}
			}

			return ticks > 100 ? new Waiting() : this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
		}

		@Override
		public void toBytes(PacketBuffer buf) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

}
