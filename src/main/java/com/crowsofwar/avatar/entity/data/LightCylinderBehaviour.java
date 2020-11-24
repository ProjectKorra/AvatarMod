package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.water.tickhandlers.WaterChargeHandler;
import com.crowsofwar.avatar.entity.EntityLightCylinder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class LightCylinderBehaviour extends Behavior<EntityLightCylinder> {
	public static final DataSerializer<LightCylinderBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public LightCylinderBehaviour() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(WaterChargeHandler.WaterCylinderBehaviour.class);
	}

	public static class Idle extends LightCylinderBehaviour {

		@Override
		public Behavior onUpdate(EntityLightCylinder entity) {
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
