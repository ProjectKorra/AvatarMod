package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.bending.fire.tickhandlers.FlamethrowerUpdateTick;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class OffensiveBehaviour extends Behavior<EntityOffensive> {
	public static final DataSerializer<OffensiveBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();


	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(FlamethrowerUpdateTick.FlamethrowerBehaviour.class);
	}

	public static class Idle extends OffensiveBehaviour {

		@Override
		public OffensiveBehaviour onUpdate(EntityOffensive entity) {
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
