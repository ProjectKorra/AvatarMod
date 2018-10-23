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

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.*;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import java.util.*;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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
				Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw), Math.toRadians(owner.rotationPitch));
				target = Vector.getEyePos(owner).plus(look.times(3));
			}

			if (target != null) {
				Vector motion = target.minus(entity.position());
				motion = motion.times(0.5 * 20);
				entity.setVelocity(motion);
			}

			// Ensure that owner always has stat ctrl active
			if (entity.ticksExisted % 10 == 0) {
				BendingData.get(owner).addStatusControl(StatusControl.THROW_FIRE);
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

			List<Entity> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(Entity.class, entity.getEntityBoundingBox().grow(0.5, 0.5, 0.5),
																					  collided -> collided != entity.getOwner());

			for (Entity collided : collidedList) {
				if (collided == entity.getOwner()) return this;
				if (entity.canCollideWith(collided)) {

					double push = STATS_CONFIG.fireArcSettings.push;
					collided.addVelocity(entity.motionX * push, 0.4 * push, entity.motionZ * push);
					collided.setFire(3);

					if (entity.canDamageEntity(collided) || collided instanceof EntityPlayer) {
						if (collided.attackEntityFrom(AvatarDamageSource.causeFireDamage(collided, entity.getOwner()),
													  STATS_CONFIG.fireArcSettings.damage * entity.getDamageMult())) {
							BattlePerformanceScore.addMediumScore(entity.getOwner());
						}
					}
					entity.onCollideWithEntity(entity);
					if (!entity.world.isRemote) {
						BendingData data = Objects.requireNonNull(Bender.get(entity.getOwner())).getData();
						if (data != null) {
							data.getAbilityData("fire_arc").addXp(ConfigSkills.SKILLS_CONFIG.fireHit);
							AbilityData abilityData = data.getAbilityData("fire_arc");
							if (abilityData.isMasterPath(AbilityTreePath.SECOND) && entity.getOwner() != null) {
								data.addStatusControl(StatusControl.THROW_FIRE);
								return new FireArcBehavior.PlayerControlled();
							}
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
