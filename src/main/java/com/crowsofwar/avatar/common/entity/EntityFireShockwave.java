package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFireShockwave extends EntityShockwave {
	private NetworkParticleSpawner particles;

	public EntityFireShockwave(World world) {
		super(world);
		this.particle = EnumParticleTypes.FLAME;
		this.particleSpeed = 0.1F;
		this.particleAmount = 1;
		this.speed = 0.5;
		this.isFire = true;
		this.fireTime = 5;
		this.setSize(1, 1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		for (int j = 0; j < 360/getParticleAmount(); j++) {
			Vector lookPos = Vector.toRectangular(Math.toRadians(rotationYaw +
					j * getParticleAmount()), 0).times(0.5);
			particles.spawnParticles(world, particle, 10/getParticleAmount() >= 1 ? 10/getParticleAmount() : 1, 20/getParticleAmount() >= 2 ? 20/getParticleAmount() : 2,
					lookPos, lookPos.times(ticksExisted * speed));
		}
	}
}
