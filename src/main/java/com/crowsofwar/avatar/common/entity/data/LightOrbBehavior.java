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

import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.bending.fire.AbilityFlameStrike;
import com.crowsofwar.avatar.common.bending.fire.AbilityImmolate;
import com.crowsofwar.avatar.common.bending.fire.tickhandlers.FlamethrowerUpdateTick;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.MathHelper;

/**
 * @author Aang23
 * <p>
 * Any needed light behavior should be here (for EntityLightOrb!)
 */
public abstract class LightOrbBehavior extends Behavior<EntityLightOrb> {

	public static final DataSerializer<LightOrbBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public LightOrbBehavior() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(FollowEntity.class);
		registerBehavior(ShiftColourRandomly.class);
		registerBehavior(ShiftColour.class);
		registerBehavior(FollowPlayer.class);
		registerBehavior(AbilityFlameStrike.FlameStrikeLightOrb.class);
		registerBehavior(AbilityImmolate.ImmolateLightOrbBehaviour.class);
		registerBehavior(FlamethrowerUpdateTick.FlamethrowerBehaviour.class);
	}

	public static class Idle extends LightOrbBehavior {

		@Override
		public LightOrbBehavior onUpdate(EntityLightOrb entity) {
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buffer) {
		}

		@Override
		public void toBytes(PacketBuffer buffer) {
		}

		@Override
		public void load(NBTTagCompound nbt) {
		}

		@Override
		public void save(NBTTagCompound nbt) {
		}

	}

	public static class FollowEntity extends LightOrbBehavior {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			Entity emitter = entity.getEmittingEntity();
			if (emitter != null) {
				entity.posX = emitter.posX;
				entity.posY = emitter.posY + entity.height * 2;
				entity.posZ = emitter.posZ;
				if (emitter instanceof AvatarEntity) {
					entity.setVelocity((((AvatarEntity) emitter).velocity()));
				}
			} else if (entity.ticksExisted > 1) {
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

	public static class FollowPlayer extends LightOrbBehavior {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			Entity emitter = entity.getEmittingEntity();
			if (emitter != null) {
				entity.motionX = emitter.motionX;
				entity.motionY = emitter.motionY;
				entity.motionZ = emitter.motionZ;
				entity.posX = emitter.posX;
				entity.posY = emitter.posY + entity.height / 2;
				entity.posZ = emitter.posZ;
			} else if (entity.ticksExisted > 1) {
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

	public static class ShiftColourRandomly extends LightOrbBehavior {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			//TODO: Frequency
			if (entity.getColourShiftRange() != 0) {
				float range = entity.getColourShiftRange() / 2;
				float r = entity.getInitialColourR();
				float g = entity.getInitialColourG();
				float b = entity.getInitialColourB();
				float a = entity.getInitialColourA();
				for (int i = 0; i < 4; i++) {
					float red, green, blue, alpha;
					float rMin = r < range ? 0 : r - range;
					float gMin = g < range ? 0 : r - range;
					float bMin = b < range ? 0 : r - range;
					float aMin = a < range ? 0 : a - range;
					float rMax = r + range;
					float gMax = b + range;
					float bMax = g + range;
					float aMax = a + range;
					switch (i) {
						case 0:
							//By dividing 100 by the max then dividing that by 100, you get a huge range of numbers, to make colour shifting
							//more realistic.
							float amountR = AvatarUtils.getRandomNumberInRange(0,
									(int) (100 / rMax)) / 100F * entity.getColourShiftInterval();
							red = entity.world.rand.nextBoolean() ? r + amountR : r - amountR;
							red = MathHelper.clamp(red, rMin, rMax);
							entity.setColorR(red);
							break;

						case 1:
							float amountG = AvatarUtils.getRandomNumberInRange(0,
									(int) (100 / gMax)) / 100F * entity.getColourShiftInterval();
							green = entity.world.rand.nextBoolean() ? g + amountG : g - amountG;
							green = MathHelper.clamp(green, gMin, gMax);
							entity.setColorG(green);
							break;

						case 2:
							float amountB = AvatarUtils.getRandomNumberInRange(0,
									(int) (100 / bMax)) / 100F * entity.getColourShiftInterval();
							blue = entity.world.rand.nextBoolean() ? b + amountB : b - amountB;
							blue = MathHelper.clamp(blue, bMin, bMax);
							entity.setColorB(blue);
							break;

						case 3:
							float amountA = AvatarUtils.getRandomNumberInRange(0,
									(int) (100 / aMax)) / 100F * entity.getColourShiftInterval();
							alpha = entity.world.rand.nextBoolean() ? a + amountA : a - amountA;
							alpha = MathHelper.clamp(alpha, aMin, aMax);
							entity.setColorA(alpha);
							break;
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

	public static class ShiftColour extends LightOrbBehavior {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			return null;
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
