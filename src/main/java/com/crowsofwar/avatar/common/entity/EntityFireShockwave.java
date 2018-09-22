package com.crowsofwar.avatar.common.entity;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFireShockwave extends EntityShockwave {
	public EntityFireShockwave(World world) {
		super(world);
		this.particle = EnumParticleTypes.FLAME;
		this.isFire = true;
		this.fireTime = 5;
	}
}
