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

import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_FIRE;

/**
 * @author CrowsOfWar
 */
public abstract class FireArcBehavior extends Behavior<EntityFireArc> {

	public static final DataSerializer<FireArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);

		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
		registerBehavior(Idle.class);

	}

	public static class PlayerControlled extends FireArcBehavior {

		public PlayerControlled() {
		}

		@Override
		public FireArcBehavior onUpdate(EntityFireArc entity) {

			EntityLivingBase owner = entity.getOwner();
			if (owner == null) {
				return this;
			}
			Raytrace.Result res = Raytrace.getTargetBlock(owner, 3, false);

			Vector target;
			if (res.hitSomething()) {
				target = res.getPosPrecise();
			} else {
				Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw),
						Math.toRadians(owner.rotationPitch));
				target = Vector.getEyePos(owner).plus(look.times(3));
			}

			if (target != null) {
				Vector motion = target.minus(entity.position());
				motion = motion.times(0.5 * 20);
				entity.setVelocity(motion);
			}

			// Ensure that owner always has stat ctrl active
			if (entity.ticksExisted % 10 == 0) {
				BendingData.get(owner).addStatusControl(THROW_FIRE);
			}
			//If statement reduces lag

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

	public static class Thrown extends FireArcBehavior {

		@Override
		public FireArcBehavior onUpdate(EntityFireArc entity) {
			entity.addVelocity(Vector.DOWN.times(9.81 / 120));

			List<Entity> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					Entity.class, entity.getEntityBoundingBox().grow(0.5, 0.5, 0.5),
					collided -> collided != entity.getOwner());

			if (!collidedList.isEmpty()) {
				for (Entity collided : collidedList) {
					if (collided == entity.getOwner()) return this;
					if (entity.canCollideWith(collided) && collided != entity) {

						double push = STATS_CONFIG.fireBlastSettings.push;  // TODO - Fix this; Originally fireArcSettings
						collided.addVelocity(entity.motionX * push, 0.4 * push, entity.motionZ * push);
						collided.setFire(3);

						if (entity.canDamageEntity(collided) || collided instanceof EntityPlayer) {
							if (collided.attackEntityFrom(AvatarDamageSource.causeFireArcDamage(collided, entity.getOwner()),
									STATS_CONFIG.fireBlastSettings.damage * entity.getDamageMult())) { // TODO - Fix this; Originally fireArcSettings
								BattlePerformanceScore.addMediumScore(entity.getOwner());
							}
						}
						if (!entity.world.isRemote) {
							BendingData data = Objects.requireNonNull(Bender.get(entity.getOwner())).getData();
							if (data != null) {
							//	data.getAbilityData(entity.getAbility().getName())
							//			.addXp(ConfigSkills.SKILLS_CONFIG.fireBlastHit); // TODO - Fix this; Originally fireArcHit
								AbilityData abilityData = data.getAbilityData(entity.getAbility().getName());
								if (abilityData.isMasterPath(AbilityTreePath.SECOND) && entity.getOwner() != null) {
									data.addStatusControl(THROW_FIRE);
									return new FireArcBehavior.PlayerControlled();
								}
							}
						}
						entity.onCollideWithEntity(entity);

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

	public static class Idle extends FireArcBehavior {

		@Override
		public FireArcBehavior onUpdate(EntityFireArc entity) {
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
