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

/**
 * @author CrowsOfWar
 */
public abstract class WaterArcBehavior extends Behavior<EntityWaterArc> {

	public static final DataSerializer<WaterArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public WaterArcBehavior() {
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

			Vector motion = target.minus(water.position());
			motion = motion.times(0.5 * 20);
			water.setVelocity(motion);

			if (water.world.isRemote && water.canPlaySplash()) {
				if (motion.sqrMagnitude() >= 0.004) water.playSplash();
			}

			// Ensure that owner always has stat ctrl active
			BendingData.get(owner).addStatusControl(StatusControl.THROW_WATER);

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
		float startGravity;
		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc entity) {
			ticks++;


			boolean waterSpear = false;
			BendingData data = null;
			AbilityData abilityData = null;
			int lvl = 0;

			Bender bender = Bender.get(entity.getOwner());
			if (bender != null) {
				data = bender.getData();
				abilityData = data.getAbilityData("water_arc");
				waterSpear = abilityData.isMasterPath(AbilityTreePath.SECOND);
				lvl = abilityData.getLevel();
			}
			if (lvl <= 0) {
				//Level I or in Creative Mode
				startGravity = 30;
				if (ticks >= 30) {
					entity.Splash();
					entity.setDead();
				}
			}
			if (lvl == 1) {
				//Level II.
				startGravity = 40;
				if (ticks >= 40) {
					entity.Splash();
					entity.setDead();
				}
			}
			if (lvl == 2) {
				//Level III
				startGravity = 50;
				if (ticks >= 50) {
					entity.Splash();
					entity.setDead();
				}
			}
			if (waterSpear) {
				//Level 4 Path Two
				startGravity = 80;
				if (ticks >= 80) {
					entity.Splash();
					entity.setDead();
				}
			}
			if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
				//Level 4 Path One
				startGravity = 60;
				if (ticks >= 60) {
					entity.Splash();
					entity.setDead();
				}
			}

			if (startGravity/ticks == 2) {
				entity.addVelocity(Vector.DOWN.times(9.81 / 30));
			}



			List<EntityLivingBase> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					EntityLivingBase.class, entity.getEntityBoundingBox().grow(0.9, 0.9, 0.9),
					collided -> collided != entity.getOwner());

			for (EntityLivingBase collided : collidedList) {
				if (collided == entity.getOwner()) return this;
				collided.addVelocity(entity.motionX/5, 0.1, entity.motionZ/5);
				entity.damageEntity(collided);

				if (!entity.world.isRemote && data != null) {

					abilityData.addXp(ConfigSkills.SKILLS_CONFIG.waterHit);

					if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
						entity.setBehavior(new PlayerControlled());
						data.addStatusControl(StatusControl.THROW_WATER);
					}
					if (!waterSpear) {
						entity.Splash();
						entity.setDead();
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
