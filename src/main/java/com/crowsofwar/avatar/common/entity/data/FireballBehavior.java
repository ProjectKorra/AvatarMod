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
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_FIREBALL;

/**
 * @author CrowsOfWar
 */
public abstract class FireballBehavior extends OffensiveBehaviour {

	public static final DataSerializer<FireballBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_FALL, ID_PICKUP, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(Idle.class);
		ID_PLAYER_CONTROL = registerBehavior(PlayerControlled.class);
		ID_THROWN = registerBehavior(Thrown.class);
	}

	public static class Idle extends FireballBehavior {

		@Override
		public FireballBehavior onUpdate(EntityOffensive entity) {
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

	public static class Thrown extends FireballBehavior {

		@Override
		public FireballBehavior onUpdate(EntityOffensive entity) {

			entity.addVelocity(Vector.DOWN.times(1F / 40));
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

	public static class PlayerControlled extends FireballBehavior {

		int ticks = 0;

		public PlayerControlled() {
		}

		@Override
		public FireballBehavior onUpdate(EntityOffensive entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null || !(entity instanceof EntityFireball)) return this;

			BendingData data = Objects.requireNonNull(Bender.get(owner)).getData();

			Vector look = Vector.getLookRectangular(owner);
			Vector target = Vector.getEyePos(owner).plus(look.times(2 + ((EntityFireball) entity).getSize() * 0.03125F));
			List<EntityFireball> fireballs = entity.world.getEntitiesWithinAABB(EntityFireball.class,
					owner.getEntityBoundingBox().grow(8, 8, 8));
			Vec3d motion = Objects.requireNonNull(target).minus(Vector.getEntityPos(entity)).toMinecraft();

			if (!fireballs.isEmpty() && fireballs.size() > 1) {
				int angle = entity.getOwner().ticksExisted % 360;
				angle *= 5;
				angle += entity.ticksExisted % 360;
				double radians = Math.toRadians(angle);
				double x = 1.75 * Math.cos(radians);
				double z = 1.75 * Math.sin(radians);
				Vec3d pos = new Vec3d(x, 0, z);
				pos = pos.add(owner.posX, owner.getEntityBoundingBox().minY + 1, owner.posZ);
				motion = pos.subtract(entity.getPositionVector()).scale(0.75);
			} else motion = motion.scale(0.75);
			entity.setVelocity(motion);

			data.addStatusControl(THROW_FIREBALL);

			if (entity.getAbility() instanceof AbilityFireball) {
				if (data.getAbilityData(new AbilityFireball().getName()).isMasterPath(AbilityTreePath.SECOND)) {
					int size = ((EntityFireball) entity).getSize();
					if (size < 60 && entity.ticksExisted % 4 == 0) {
						((EntityFireball) entity).setSize(size + 1);
						entity.setDamage(20 / 32F * (((EntityFireball) entity).getSize() * 0.03125F) * entity.getDamage());
					}
				}
			}

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

}
