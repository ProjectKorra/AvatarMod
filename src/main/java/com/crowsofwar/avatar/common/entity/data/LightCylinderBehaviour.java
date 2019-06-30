package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.entity.EntityLightCylinder;
import net.minecraft.network.datasync.DataSerializer;

public abstract class LightCylinderBehaviour extends Behavior<EntityLightCylinder> {
	public static final DataSerializer<LightOrbBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();

	public LightCylinderBehaviour() {
	}

	public static void register() {
	}

}
