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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public abstract class FloatingBlockBehavior extends Behavior<EntityFloatingBlock> {

	public static final DataSerializer<FloatingBlockBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLACE, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(DoNothing.class);
		ID_FALL = registerBehavior(Fall.class);
		ID_PICKUP = registerBehavior(PickUp.class);
		ID_PLACE = registerBehavior(Place.class);
		ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
		ID_THROWN = registerBehavior(Thrown.class);
	}

	public static class DoNothing extends FloatingBlockBehavior {

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {
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

	public static class Place extends FloatingBlockBehavior {

		private BlockPos placeAt;

		public Place() {
		}

		public Place(BlockPos placeAt) {
			this.placeAt = placeAt;
		}

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {

			Vector placeAtVec = new Vector(placeAt.getX() + 0.5, placeAt.getY(), placeAt.getZ() + 0.5);
			Vector thisPos = new Vector(entity);
			Vector force = placeAtVec.minus(thisPos);
			force = force.normalize().times(3);
			entity.setVelocity(force);

			if (!entity.world.isRemote && placeAtVec.sqrDist(thisPos) < 0.01) {

				entity.setDead();
				entity.world.setBlockState(new BlockPos(entity), entity.getBlockState());

				SoundType sound = entity.getBlock().getSoundType();
				if (sound != null) {
					entity.world.playSound(null, entity.getPosition(), sound.getPlaceSound(),
							SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
				}

			}

			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
			placeAt = buf.readBlockPos();
		}

		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeBlockPos(placeAt);
		}

		@Override
		public void load(NBTTagCompound nbt) {
			placeAt = new BlockPos(nbt.getInteger("PlaceX"), nbt.getInteger("PlaceY"),
					nbt.getInteger("PlaceZ"));
		}

		@Override
		public void save(NBTTagCompound nbt) {
			nbt.setInteger("PlaceX", placeAt.getX());
			nbt.setInteger("PlaceY", placeAt.getY());
			nbt.setInteger("PlaceZ", placeAt.getZ());
		}

	}

	public static class Thrown extends FloatingBlockBehavior {

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {

			if (entity.collided) {
				if (!entity.world.isRemote) entity.setDead();
				entity.onCollideWithSolid();

				World world = entity.world;
				Block block = entity.getBlockState().getBlock();
				SoundType sound = block.getSoundType();
				if (sound != null) {
					entity.world.playSound(null, entity.getPosition(), sound.getBreakSound(),
							SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
				}

			}

			entity.addVelocity(Vector.DOWN.times(9.81 / 20));

			World world = entity.world;
			if (!entity.isDead) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
						entity.getExpandedHitbox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != entity.getOwner()) {
						return collision((EntityLivingBase) collided, entity);
					} else if (collided != entity.getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(entity));
						motion = motion.times(0.3).withY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}

				}
			}

			return this;

		}

		private FloatingBlockBehavior collision(EntityLivingBase collided, EntityFloatingBlock entity) {
			// Add damage
			double speed = entity.velocity().magnitude();

			if (collided.attackEntityFrom(
					AvatarDamageSource.causeFloatingBlockDamage(collided, entity.getOwner()),
					(float) (speed * STATS_CONFIG.floatingBlockSettings.damage * entity.getDamageMult()))) {
				BattlePerformanceScore.addMediumScore(entity.getOwner());
			}

			// Push entity
			Vector motion = new Vector(collided).minus(new Vector(entity));
			motion = motion.times(STATS_CONFIG.floatingBlockSettings.push).withY(0.08);
			collided.addVelocity(motion.x(), motion.y(), motion.z());

			// Add XP
			BendingData data = Bender.get(entity.getOwner()).getData();
			if (!collided.world.isRemote && data != null) {
				float xp = SKILLS_CONFIG.blockThrowHit;
				if (collided.getHealth() <= 0) {
					xp = SKILLS_CONFIG.blockKill;
				}
				data.getAbilityData("pickup_block").addXp(xp);
			}

			// Remove the floating block & spawn particles
			entity.onCollideWithSolid();
			// boomerang upgrade handling
			if (!entity.world.isRemote) {
				if (data.getAbilityData("pickup_block")
						.isMasterPath(AbilityTreePath.FIRST)) {

					Bender bender = Bender.get(entity.getOwner());
					if (bender.consumeChi(STATS_CONFIG.chiPickUpBlock)) {
						data.addStatusControl(StatusControl.THROW_BLOCK);
						data.addStatusControl(StatusControl.PLACE_BLOCK);

						// the entity was already setDead from onCollideWithSolid, we need to mark it alive again
						entity.isDead = false;

						return new FloatingBlockBehavior.PlayerControlled();
					}

				}
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

	public static class PickUp extends FloatingBlockBehavior {

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {
			entity.addVelocity(Vector.DOWN.times(9.81 / 20));

			Vector velocity = entity.velocity();
			if (velocity.y() <= 0) {
				entity.setVelocity(velocity.withY(0));
				return new PlayerControlled();
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

	public static class PlayerControlled extends FloatingBlockBehavior {

		public PlayerControlled() {
		}

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;

			BendingData data = Bender.get(owner).getData();

			double yaw = Math.toRadians(owner.rotationYaw);
			double pitch = Math.toRadians(owner.rotationPitch);
			Vector forward = Vector.toRectangular(yaw, pitch);
			Vector eye = Vector.getEyePos(owner);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(new Vector(entity));
			motion = motion.times(5);
			entity.setVelocity(motion);

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

	public static class Fall extends FloatingBlockBehavior {

		@Override
		public FloatingBlockBehavior onUpdate(EntityFloatingBlock entity) {
			entity.addVelocity(Vector.DOWN.times(9.81 / 20));
			if (entity.collided) {
				if (!entity.world.isRemote) entity.setDead();
				entity.onCollideWithSolid();
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

}
