package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.ShootAirBurstHandler;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.SmashGroundHandler;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireShot;
import com.crowsofwar.avatar.entity.EntityOffensive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class OffensiveBehaviour extends Behavior<EntityOffensive> {
	public static final DataSerializer<OffensiveBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();


	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Idle.class);
		registerBehavior(AbilityFlameStrike.FireblastBehaviour.class);
		registerBehavior(AirBurstHandler.AirburstShockwave.class);
		registerBehavior(AbilityFireShot.FireShockwaveBehaviour.class);
		registerBehavior(SmashGroundHandler.AirGroundPoundShockwave.class);
		registerBehavior(ShootAirBurstHandler.AirBurstBeamBehaviour.class);
		registerBehavior(AbilityAirGust.AirGustBehaviour.class);
		registerBehavior(AbilityAirblade.AirBladeBehaviour.class);
	}

	public static class Idle extends OffensiveBehaviour {

		@Override
		public OffensiveBehaviour onUpdate(EntityOffensive entity) {
			return this;
		}

		@Override
		public void renderUpdate(EntityOffensive entity) {

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
