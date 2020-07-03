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

import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author CrowsOfWar
 */
public abstract class FloatingBlockBehavior extends OffensiveBehaviour {

	public static final DataSerializer<FloatingBlockBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLACE, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(Idle.class);
		ID_FALL = registerBehavior(Fall.class);
		ID_PICKUP = registerBehavior(PickUp.class);
		ID_PLACE = registerBehavior(Place.class);
		ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
		ID_THROWN = registerBehavior(Thrown.class);
	}

	public static class Idle extends FloatingBlockBehavior {

		@Override
		public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
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
		public FloatingBlockBehavior onUpdate(EntityOffensive entity) {

			if (entity instanceof EntityFloatingBlock) {
				Vector placeAtVec = new Vector(placeAt.getX() + 0.5, placeAt.getY(), placeAt.getZ() + 0.5);
				Vector thisPos = new Vector(entity);
				Vector force = placeAtVec.minus(thisPos);
				force = force.normalize().times(3);
				entity.setVelocity(force);

				if (!entity.world.isRemote && placeAtVec.sqrDist(thisPos) < 0.01) {

					entity.setDead();
					entity.world.setBlockState(new BlockPos(entity), ((EntityFloatingBlock) entity).getBlockState());

					SoundType sound = ((EntityFloatingBlock) entity).getBlock().getSoundType();
					if (sound != null) {
						entity.world.playSound(null, entity.getPosition(), sound.getPlaceSound(),
								SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
					}

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
		public FloatingBlockBehavior onUpdate(EntityOffensive entity) {

			if (entity.collided && entity instanceof EntityFloatingBlock) {

				World world = entity.world;
				Block block = ((EntityFloatingBlock) entity).getBlockState().getBlock();
				SoundType sound = block.getSoundType();
				if (sound != null) {
					world.playSound(null, entity.getPosition(), sound.getBreakSound(),
							SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
				}

			}

			entity.addVelocity(Vector.DOWN.times(9.81 / 30));

			World world = entity.world;
			/*if (!entity.isDead) {
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
				entity.setBehavior(new FloatingBlockBehavior.PlayerControlled());
			}**/

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
		public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
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

		int ticks = 0;

		public PlayerControlled() {
		}

		@Override
		public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null || !(entity instanceof EntityFloatingBlock)) return this;

			Vector forward = Vector.getLookRectangular(owner);
			Vector eye = Vector.getEyePos(owner);
			Vector target = forward.times(2.5).plus(eye);
			Vec3d motion = target.minus(Vector.getEntityPos(entity)).times(0.5).toMinecraft();
			int angle = ticks % 360;
			List<EntityFloatingBlock> blocks = entity.world.getEntitiesWithinAABB(EntityFloatingBlock.class,
					owner.getEntityBoundingBox().grow(3, 3, 3));
			//S P I N
			if (!blocks.isEmpty() && blocks.size() > 1) {
				angle *= 5;
				angle += 360 / (blocks.indexOf(entity) + 1);
				double radians = Math.toRadians(angle);
				double x = 2.5 * Math.cos(radians);
				double z = 2.5 * Math.sin(radians);
				Vec3d pos = new Vec3d(x, 0, z);
				pos = pos.add(owner.posX, owner.getEntityBoundingBox().minY + 1.5, owner.posZ);
				motion = pos.subtract(entity.getPositionVector()).scale(.5);
			}

			entity.setVelocity(motion);
			ticks++;
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
		public FloatingBlockBehavior onUpdate(EntityOffensive entity) {
			if (entity instanceof EntityFloatingBlock) {
				entity.addVelocity(Vector.DOWN.times(9.81 / 20));
				if (entity.collided) {
					if (!entity.world.isRemote) entity.setDead();
					entity.onCollideWithSolid();
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

}
