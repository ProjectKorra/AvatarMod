package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.entity.EntityRaytraceHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class RaytraceHandlerBehaviour extends Behavior<EntityRaytraceHandler> {
	public static final DataSerializer<RaytraceHandlerBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public RaytraceHandlerBehaviour() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
	}

	public static class Idle extends RaytraceHandlerBehaviour {

		@Override
		public Behavior onUpdate(EntityRaytraceHandler entity) {
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

	public static class DetectCollisionBoxes extends RaytraceHandlerBehaviour {

		@Override
		public Behavior onUpdate(EntityRaytraceHandler entity) {
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
