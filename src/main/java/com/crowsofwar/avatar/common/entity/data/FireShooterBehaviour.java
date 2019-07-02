package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.entity.EntityFireShooter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public abstract class FireShooterBehaviour extends Behavior<EntityFireShooter> {
	public static final DataSerializer<FireShooterBehaviour> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public FireShooterBehaviour() {
	}

	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
	}
}
