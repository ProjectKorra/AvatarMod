package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.bending.fire.AbilityFireBlast;
import com.crowsofwar.avatar.common.entity.EntityFireShooter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class FireShooterBehaviour extends Behavior<EntityFireShooter> {
	public static final DataSerializer<FireShooterBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public FireShooterBehaviour() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(AbilityFireBlast.FireBlastBehaviour.class);
	}

	public static class Idle extends FireShooterBehaviour {

		@Override
		public Behavior onUpdate(EntityFireShooter entity) {
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
