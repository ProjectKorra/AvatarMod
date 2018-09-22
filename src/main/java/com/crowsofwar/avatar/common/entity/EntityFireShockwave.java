package com.crowsofwar.avatar.common.entity;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFireShockwave extends EntityShockwave {
	public EntityFireShockwave(World world) {
		super(world);
		this.particle = EnumParticleTypes.CLOUD;
		this.particleSpeed = 0.1F;
		this.particleAmount = 5;
		this.speed = 0.5;
		this.isFire = true;
		this.fireTime = 5;
		this.setSize(1, 1);
	}
}
