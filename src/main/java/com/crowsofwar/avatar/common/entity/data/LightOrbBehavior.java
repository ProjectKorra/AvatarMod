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

import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * @author Aang23
 * 
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
			if (entity.getEmittingEntity() != null) {
				entity.posX = entity.getEmittingEntity().posX;
				entity.posY = entity.getEmittingEntity().posY + entity.height;
				entity.posZ = entity.getEmittingEntity().posZ;
				if (entity.getEmittingEntity() instanceof AvatarEntity) {
					entity.setVelocity(((AvatarEntity) entity.getEmittingEntity()).velocity());
				}
			}
			else if (entity.ticksExisted > 1) {
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

}
