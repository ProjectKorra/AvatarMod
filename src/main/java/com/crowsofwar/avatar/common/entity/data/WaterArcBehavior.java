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

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterArc;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public abstract class WaterArcBehavior extends Behavior<EntityWaterArc> {

	public static final DataSerializer<WaterArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	WaterArcBehavior() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);

		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
		registerBehavior(Idle.class);

	}

	public static class PlayerControlled extends WaterArcBehavior {

		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc water) {

			EntityLivingBase owner = water.getOwner();
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

			assert target != null;
			Vector motion = target.minus(water.position());
			motion = motion.times(0.5 * 20);
			water.setVelocity(motion);

			if (water.world.isRemote && water.canPlaySplash()) {
				if (motion.sqrMagnitude() >= 0.004) water.playSplash();
			}

			// Ensure that owner always has stat ctrl active
			if (water.ticksExisted % 10 == 0) {
				BendingData.get(owner).addStatusControl(StatusControl.THROW_WATER);
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

	public static class Thrown extends WaterArcBehavior {

		float ticks = 0;

		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc entity) {
			ticks++;


			boolean waterSpear = false;
			BendingData data = null;
			AbilityData abilityData = null;
			int lvl = 0;

			Bender bender = Bender.get(entity.getOwner());
			if (bender != null && entity.getAbility() != null && !entity.world.isRemote) {
				data = bender.getData();
				abilityData = data.getAbilityData(entity.getAbility().getName());
				if (entity.getAbility() instanceof AbilityWaterArc) {
					waterSpear = abilityData.isMasterPath(AbilityTreePath.SECOND);
				}
				lvl = abilityData.getLevel();
			}
			if (lvl <= 0) {
				//Level I or in Creative Mode
				if (ticks >= STATS_CONFIG.waterArcTicks) {
					//Default is 120
					entity.Splash();
					entity.setDead();
				}
			}
			if (lvl == 1) {
				//Level II.
				if (ticks >= STATS_CONFIG.waterArcTicks * (5F / 4)) {
					//150
					entity.Splash();
					entity.setDead();
				}
			}
			if (lvl == 2) {
				//Level III
				if (ticks >= STATS_CONFIG.waterArcTicks * (6F / 4)) {
					//180
					entity.Splash();
					entity.setDead();
				}
			}
			if (waterSpear) {
				//Level 4 Path Two
				if (ticks >= STATS_CONFIG.waterArcTicks * (3)) {
					//360 ticks
					entity.Splash();
					entity.setDead();
				}
			}

			if (abilityData != null) {
				if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
					//Level 4 Path One
					if (ticks >= STATS_CONFIG.waterArcTicks * (5F / 4)) {
						//150
						entity.Splash();
						entity.setDead();
					}

				}
			}

			entity.addVelocity(Vector.DOWN.times(entity.getGravity() / 90));


			List<EntityLivingBase> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					EntityLivingBase.class, entity.getEntityBoundingBox().grow(0.5, 0.5, 0.5),
					collided -> collided != entity.getOwner());

			for (EntityLivingBase collided : collidedList) {
				if (collided == entity.getOwner()) return this;
				if (entity.canCollideWith(collided)) {
					double x = entity.motionX / 2 * STATS_CONFIG.waterArcSettings.push;
					double y = entity.motionY / 20 * STATS_CONFIG.waterArcSettings.push > 0.75 ? 0.75 : entity.motionY / 20 * STATS_CONFIG.waterArcSettings.push;
					double z = entity.motionZ / 2 * STATS_CONFIG.waterArcSettings.push;
					collided.addVelocity(x, y, z);
					if (entity.canDamageEntity(collided)) {
						entity.setDamageMult(1);
						entity.damageEntity(collided);
					}

					if (!entity.world.isRemote && data != null) {

						abilityData.addXp(ConfigSkills.SKILLS_CONFIG.waterHit);

						if (!waterSpear) {
							entity.Splash();
							entity.setDead();
							entity.cleanup();
						}

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

	public static class Idle extends WaterArcBehavior {

		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc entity) {
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
