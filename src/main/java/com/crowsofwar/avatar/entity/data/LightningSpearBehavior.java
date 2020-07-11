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

package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.lightning.AbilityLightningSpear;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityLightningSpear;
import com.crowsofwar.avatar.util.Raytrace;
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

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_LIGHTNINGSPEAR;

/**
 * @author CrowsOfWar
 */
public abstract class LightningSpearBehavior extends Behavior<EntityLightningSpear> {

	public static final DataSerializer<LightningSpearBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_PLAYER_CONTROL, ID_THROWN;

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
			entity.noClip = false;

			World world = entity.world;
			if (!entity.isDead && !world.isRemote) {
				AxisAlignedBB box = new AxisAlignedBB(entity.posX + entity.getAvgSize(), entity.posY + entity.getAvgSize(), entity.posZ + entity.getAvgSize(),
						entity.posX - entity.getAvgSize(), entity.posY - entity.getAvgSize(), entity.posZ - entity.getAvgSize());
				List<Entity> collidedList = world.getEntitiesWithinAABB(Entity.class,
						box);
				if (!collidedList.isEmpty()) {
					for (Entity collided : collidedList) {
						if (collided != entity.getOwner() && (entity.canCollideWith(collided) || (collided.canBeCollidedWith() && collided.canBePushed()))) {
							if (collided != entity) {
								collision(collided, entity, entity.isGroupAttack());
							}
						}
					}
				}
			}

			return this;

		}



		private void collision(Entity collided, EntityLightningSpear entity, boolean triggerGroupAttack) {
			//TODO: Move all of this to the entity class.
			if (entity.canDamageEntity(collided) && collided != entity.getOwner() && collided != entity) {

			}


			if (triggerGroupAttack) {

				// Damage nearby entities in group

				double radius = 2;
				AxisAlignedBB aabb = new AxisAlignedBB(
						entity.posX - radius, entity.posY - radius, entity.posZ - radius,
						entity.posX + radius, entity.posY + radius, entity.posZ + radius);

				List<Entity> targets = entity.world.getEntitiesWithinAABB(
						Entity.class, aabb);
				for (Entity target : targets) {
					if (target.getDistanceSq(entity) > radius * radius) {
						continue;
					}
					collision(target, entity, false);
				}

			}
		//	if (!entity.world.isRemote && !entity.isPiercing()) entity.setDead();

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
		float maxDamage = 4;
		@Override
		public LightningSpearBehavior onUpdate(EntityLightningSpear entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null || entity.world.isRemote) return this;

			BendingData data = BendingData.get(owner);
			if (!data.hasStatusControl(THROW_LIGHTNINGSPEAR)) {
				EntityLightningSpear spear = AvatarEntity.lookupControlledEntity(entity.world, EntityLightningSpear.class, entity.getOwner());
				if (spear != null) {
					data.addStatusControl(THROW_LIGHTNINGSPEAR);
				}
			}
			Raytrace.Result res = Raytrace.getTargetBlock(owner, 3, false);

			Vector target;
			if (res.hitSomething()) {
				target = res.getPosPrecise();
			} else {
				Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw),
						Math.toRadians(owner.rotationPitch));
				target = Vector.getEyePos(owner).plus(look.times(1 + entity.getAvgSize()));
			}

			assert target != null;
			Vector motion = target.minus(entity.position());
			motion = motion.times(0.5 * 20);
			entity.setVelocity(motion);

			Vector direction = entity.position().minus(Vector.getEyePos(owner)).toSpherical();
			entity.rotationYaw = (float) Math.toDegrees(direction.y());
			entity.rotationPitch = (float) Math.toDegrees(direction.x());
			//entity.rotationPitch = owner.rotationPitch;
			//entity.rotationYaw = owner.rotationYaw;

			entity.noClip = true;


			float size = entity.getAvgSize();
			float damage = entity.getDamage();

			if (entity.getAbility() instanceof AbilityLightningSpear && !entity.world.isRemote) {
				AbilityData aD = AbilityData.get(entity.getOwner(), "lightning_spear");
				int lvl = aD.getLevel();
				if (lvl == 1) {
					maxSize = 1.8F;
					maxDamage = 5;
				}
				if (lvl == 2) {
					maxSize = 2F;
					maxDamage = 6;
				}
				if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					maxSize = 2.6F;
					maxDamage = 7;
				}
				if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
					maxDamage = 8;
				}
			}
			if (size < maxSize && entity.ticksExisted % 4 == 0) {
				entity.setEntitySize(size + 0.005F);
			}
			if (damage < maxDamage && entity.ticksExisted % 4 == 0) {
				entity.setDamage(damage + 0.005F);
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
