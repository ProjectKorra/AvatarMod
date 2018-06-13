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
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public abstract class LightningSpearBehavior extends Behavior<EntityLightningSpear> {

	public static final DataSerializer<LightningSpearBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(Idle.class);
		ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
		ID_THROWN = registerBehavior(Thrown.class);
	}

	public static class Idle extends LightningSpearBehavior {

		@Override
		public LightningSpearBehavior onUpdate(EntityLightningSpear entity) {
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

	public static class Thrown extends LightningSpearBehavior {

		int time = 0;

		@Override
		public LightningSpearBehavior onUpdate(EntityLightningSpear entity) {

			time++;

			if (entity.isCollided || (!entity.world.isRemote && time > 200)) {
				entity.setDead();
				entity.onCollideWithSolid();
			}

			entity.addVelocity(Vector.DOWN.times(1 / 12000));

			Vector direction = entity.velocity().toSpherical();
			entity.rotationYaw = (float) Math.toDegrees(direction.y());
			entity.rotationPitch = (float) Math.toDegrees(direction.x());

			World world = entity.world;
			if (!entity.isDead) {
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
						entity.getExpandedHitbox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != entity.getOwner()) {
						collision((EntityLivingBase) collided, entity, entity.isGroupAttack());
					} else if (collided != entity.getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(entity));
						motion = motion.times(0.7).withY(0.09);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}

				}
			}

			return this;

		}

		private void collision(EntityLivingBase collided, EntityLightningSpear entity, boolean triggerGroupAttack) {
			double speed = entity.velocity().magnitude();

			if (collided.attackEntityFrom(AvatarDamageSource.causeFireballDamage(collided, entity.getOwner()),
					entity.getDamage())) {
				BattlePerformanceScore.addMediumScore(entity.getOwner());
			}

			Vector motion = entity.velocity().dividedBy(5);
			motion = motion.times(STATS_CONFIG.fireballSettings.push).withY(0.07);
			collided.addVelocity(motion.x(), motion.y(), motion.z());

			BendingData data = Bender.get(entity.getOwner()).getData();
			if (!collided.world.isRemote && data != null) {
				float xp = SKILLS_CONFIG.lightningspearHit;
				data.getAbilityData("lightning_spear").addXp(xp);
			}

			// Remove the fireball & spawn particles
			if (!entity.world.isRemote && !entity.isPiercing()) entity.setDead();

			if (triggerGroupAttack) {

				// Damage nearby entities in group

				double radius = 2;
				AxisAlignedBB aabb = new AxisAlignedBB(
						entity.posX - radius, entity.posY - radius, entity.posZ - radius,
						entity.posX + radius, entity.posY + radius, entity.posZ + radius);

				List<EntityLivingBase> targets = entity.world.getEntitiesWithinAABB(
						EntityLivingBase.class, aabb);
				for (EntityLivingBase target : targets) {
					if (target.getDistanceSqToEntity(entity) > radius * radius) {
						continue;
					}
					collision(target, entity, false);
				}

			}

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

	public static class PlayerControlled extends LightningSpearBehavior {

		public PlayerControlled() {
		}

		@Override
		public LightningSpearBehavior onUpdate(EntityLightningSpear entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;

			BendingData data = Bender.get(owner).getData();

			double yaw = Math.toRadians(owner.rotationYaw);
			double pitch = Math.toRadians(owner.rotationPitch);
			Vector forward = Vector.toRectangular(yaw, pitch);
			Vector eye = Vector.getEyePos(owner);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(Vector.getEntityPos(entity)).times(5);
			entity.setVelocity(motion);

			Vector direction = entity.position().minus(Vector.getEyePos(owner)).toSpherical();
			entity.rotationYaw = (float) Math.toDegrees(direction.y());
			entity.rotationPitch = (float) Math.toDegrees(direction.x());

			int size = entity.getSize();
			if (size < 60 && entity.ticksExisted % 4 == 0) {
				entity.setSize(size + 1);
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
