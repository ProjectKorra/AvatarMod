package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.entity.EntityRaytraceHandler;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.Vec3d;

public abstract class RaytraceHandlerBehaviour extends Behavior<EntityRaytraceHandler> {
	public static final DataSerializer<RaytraceHandlerBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public RaytraceHandlerBehaviour() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(ProvideEntityDetection.class);
		registerBehavior(DetectCollisionBoxes.class);
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


	public static class ProvideEntityDetection extends RaytraceHandlerBehaviour {

		@Override
		public Behavior onUpdate(EntityRaytraceHandler entity) {
			if (entity.getFollowingEntity() != null) {
				Vec3d pos = AvatarEntityUtils.getMiddleOfEntity(entity.getFollowingEntity());
				entity.setPositionAndUpdate(pos.x, pos.y, pos.z);
				entity.setPosition(pos);
				entity.setVelocity(Vec3d.ZERO);
				entity.setEntitySize(0.05F);
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
