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
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.util.Objects;

/**
 * @author CrowsOfWar
 */
public abstract class FireballBehavior extends Behavior<EntityFireball> {

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
		public FireballBehavior onUpdate(EntityFireball entity) {
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

		int time = 0;

		@Override
		public FireballBehavior onUpdate(EntityFireball entity) {

			time++;

			if (entity.collided || (!entity.world.isRemote && time > 100)) {
				entity.setDead();
				entity.onCollideWithSolid();
			}

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

		public PlayerControlled() {
		}

		@Override
		public FireballBehavior onUpdate(EntityFireball entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;

			BendingData data = Objects.requireNonNull(Bender.get(owner)).getData();

			if (!entity.world.isRemote) {
				Raytrace.Result res = Raytrace.getTargetBlock(owner, 3, false);

				Vector target;
				if (res.hitSomething()) {
					target = res.getPosPrecise();
				} else {
					Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw),
							Math.toRadians(owner.rotationPitch));
					target = Vector.getEyePos(owner).plus(look.times(3));
				}

				Vector motion = Objects.requireNonNull(target).minus(entity.position());
				motion = motion.times(5);
				entity.setVelocity(motion);
				entity.rotationYaw = owner.rotationYaw;
				entity.rotationPitch = owner.rotationPitch;
			}

			if (entity.getAbility() instanceof AbilityFireball) {
				if (data.getAbilityData(new AbilityFireball().getName()).isMasterPath(AbilityTreePath.SECOND)) {
					int size = entity.getSize();
					if (size < 60 && entity.ticksExisted % 4 == 0) {
						entity.setSize(size + 1);
						entity.setDamage(1 / (20 * 0.03125F) * entity.getDamage());
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

}
