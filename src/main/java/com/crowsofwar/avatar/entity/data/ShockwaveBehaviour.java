package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.SmashGroundHandler;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireShot;
import com.crowsofwar.avatar.entity.EntityShockwave;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class ShockwaveBehaviour extends Behavior<EntityShockwave> {

	public static final DataSerializer<ShockwaveBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public ShockwaveBehaviour() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(AbilityFireShot.FireShockwaveBehaviour.class);
		registerBehavior(AirBurstHandler.AirburstShockwave.class);
		registerBehavior(SmashGroundHandler.AirGroundPoundShockwave.class);
	}

	public static class Idle extends ShockwaveBehaviour {

		@Override
		public Behavior onUpdate(EntityShockwave entity) {
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
