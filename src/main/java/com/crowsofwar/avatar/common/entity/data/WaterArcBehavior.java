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

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.Vec3d;

import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_WATER;

/**
 * @author CrowsOfWar
 */
public abstract class WaterArcBehavior extends OffensiveBehaviour {

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
		public WaterArcBehavior onUpdate(EntityOffensive water) {

			EntityLivingBase owner = water.getOwner();
			if (owner == null || !(water instanceof EntityWaterArc)) return this;

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
			water.rotationYaw = owner.rotationYaw;
			water.rotationPitch = owner.rotationPitch;

			if (water.world.isRemote && ((EntityWaterArc) water).canPlaySplash()) {
				if (motion.sqrMagnitude() >= 0.004) ((EntityWaterArc) water).playSplash();
			}

			// Ensure that owner always has stat ctrl active
			if (water.ticksExisted % 10 == 0) {
				BendingData.get(owner).addStatusControl(THROW_WATER);
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
		public WaterArcBehavior onUpdate(EntityOffensive entity) {
			ticks++;


		/*	if (entity.world.isRemote && entity.getOwner() != null) {
				Vec3d pos = AvatarEntityUtils.getMiddleOfEntity(entity);
				for (int h = 0; h < 4; h++)
					ParticleBuilder.create(ParticleBuilder.Type.WATER).pos(pos).entity(entity).vel(entity.world.rand.nextGaussian() / 80 + entity.motionX,
							entity.world.rand.nextGaussian() / 80 + entity.motionY, entity.world.rand.nextGaussian() / 80 + entity.motionZ).clr(0, 102, 255, 255)
							.time(8 + AvatarUtils.getRandomNumberInRange(0, 8)).collide(true).spawn(entity.world);

			}**/

			entity.addVelocity(0, -1F / 120, 0);
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
		public WaterArcBehavior onUpdate(EntityOffensive entity) {
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
