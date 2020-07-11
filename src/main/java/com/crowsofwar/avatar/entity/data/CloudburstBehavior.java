package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.air.AbilityCloudBurst;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.entity.EntityCloudBall;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.util.Objects;

public abstract class CloudburstBehavior extends Behavior<EntityCloudBall> {
	public static final DataSerializer<CloudburstBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static int ID_NOTHING, ID_PLAYER_CONTROL, ID_THROWN;

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		ID_NOTHING = registerBehavior(CloudburstBehavior.Idle.class);
		ID_PLAYER_CONTROL = registerBehavior(CloudburstBehavior.PlayerControlled.class);
		ID_THROWN = registerBehavior(CloudburstBehavior.Thrown.class);
	}

	public static class Idle extends CloudburstBehavior {

		@Override
		public CloudburstBehavior onUpdate(EntityCloudBall entity) {
			return this;
		}

		@Override
		public void renderUpdate(EntityCloudBall entity) {

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

	public static class Thrown extends CloudburstBehavior {

		int time = 0;

		@Override
		public CloudburstBehavior onUpdate(EntityCloudBall entity) {

			time++;
			entity.addVelocity(0, -1F / 120, 0);

			return this;

		}

		@Override
		public void renderUpdate(EntityCloudBall entity) {

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

	public static class PlayerControlled extends CloudburstBehavior {

		public PlayerControlled() {
		}

		@Override
		public CloudburstBehavior onUpdate(EntityCloudBall entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null) return this;
			BendingData data = Objects.requireNonNull(Bender.get(owner)).getData();

			Vector forward = Vector.getLookRectangular(owner);
			Vector eye = Vector.getEyePos(owner).minusY(0.5);
			Vector target = forward.times(1.5).plus(eye);
			Vector motion = target.minus(Vector.getEntityPos(entity)).times(6);
			entity.setVelocity(motion);

			if (entity.getAbility() instanceof AbilityCloudBurst && !entity.world.isRemote) {
				if (data.getAbilityData("cloudburst").isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					int size = entity.getSize();
					if (size < 45 && entity.ticksExisted % 4 == 0) {
						entity.setSize(size + 1);
					}
				}
			}
			return this;
		}

		@Override
		public void renderUpdate(EntityCloudBall entity) {

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
