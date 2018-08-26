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
import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningSpear;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import com.crowsofwar.avatar.common.util.Raytrace;
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

			if (entity.collided || (!entity.world.isRemote && time > 200)) {
				entity.onCollideWithSolid();

			}

			if (!entity.isInWater()) {
				entity.setInvisible(false);
			}

			World world = entity.world;
			if (!entity.isDead) {
				AxisAlignedBB box = new AxisAlignedBB(entity.posX + entity.getSize()/8, entity.posY + entity.getSize()/8,
						entity.posZ + entity.getSize()/8, entity.posX - entity.getSize()/8, entity.posY - entity.getSize()/8,
						entity.posZ - entity.getSize()/8);
				List<Entity> collidedList = world.getEntitiesWithinAABBExcludingEntity(entity,
						box);
				if (!collidedList.isEmpty()) {
					for (Entity collided : collidedList) {
						if (collided instanceof EntityLivingBase && collided != entity.getOwner() && entity.canCollideWith(collided)) {
							collision(collided, entity, entity.isGroupAttack());
						}
					}
				}
			}

			return this;

		}



		private void collision(Entity collided, EntityLightningSpear entity, boolean triggerGroupAttack) {
			double speed = entity.velocity().magnitude();

			if (entity.canDamageEntity(collided) && collided != entity) {
				if (collided.attackEntityFrom(AvatarDamageSource.causeFireballDamage(collided, entity.getOwner()),
						entity.getDamage())) {
					BattlePerformanceScore.addMediumScore(entity.getOwner());
				}
			}
			if (collided.canBeCollidedWith() && collided.canBePushed() && collided != entity) {

				entity.onCollideWithSolid();
				Vector motion = entity.velocity().dividedBy(20);
				motion = motion.times(STATS_CONFIG.fireballSettings.push).withY(0.07);
				collided.addVelocity(motion.x(), motion.y(), motion.z());

				BendingData data = Bender.get(entity.getOwner()).getData();
				if (!collided.world.isRemote && data != null) {
					float xp = SKILLS_CONFIG.lightningspearHit;
					data.getAbilityData("lightning_spear").addXp(xp);
				}
			}


			if (triggerGroupAttack) {

				// Damage nearby entities in group

				double radius = 2;
				AxisAlignedBB aabb = new AxisAlignedBB(
						entity.posX - radius, entity.posY - radius, entity.posZ - radius,
						entity.posX + radius, entity.posY + radius, entity.posZ + radius);

				List<EntityLivingBase> targets = entity.world.getEntitiesWithinAABB(
						EntityLivingBase.class, aabb);
				for (EntityLivingBase target : targets) {
					if (target.getDistanceSq(entity) > radius * radius) {
						continue;
					}
					collision(target, entity, false);
				}

			}
			if (!entity.world.isRemote && !entity.isPiercing()) entity.setDead();

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

		float maxSize = 1.6F;
		@Override
		public LightningSpearBehavior onUpdate(EntityLightningSpear entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;

			Raytrace.Result res = Raytrace.getTargetBlock(owner, 3, false);

			Vector target;
			if (res.hitSomething()) {
				target = res.getPosPrecise();
			} else {
				Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw),
						Math.toRadians(owner.rotationPitch));
				target = Vector.getEyePos(owner).plus(look.times(3));
			}

			Vector motion = target.minus(entity.position());
			motion = motion.times(0.5 * 20);
			entity.setVelocity(motion);

			Vector direction = entity.position().minus(Vector.getEyePos(owner)).toSpherical();
			entity.rotationYaw = (float) Math.toDegrees(direction.y());
			entity.rotationPitch = (float) Math.toDegrees(direction.x());


			float size = entity.getSize();

			if (entity.getAbility() instanceof AbilityLightningSpear && !entity.world.isRemote) {
				AbilityData aD = AbilityData.get(entity.getOwner(), "lightning_spear");
				int lvl = aD.getLevel();
				if (lvl == 1) {
					maxSize = 1.8F;
				}
				if (lvl == 2) {
					maxSize = 2F;
				}
				if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					maxSize = 2.6F;
				}
			}
			if (size < maxSize && entity.ticksExisted % 4 == 0) {
				entity.setSize(size + 0.005F);
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
