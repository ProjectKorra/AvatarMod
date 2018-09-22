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
		this.particles = new NetworkParticleSpawner();
		this.setSize(1, 1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (getRange() * 10 * 1.5)) {
			double x = posX + (ticksExisted * getSpeed()) * Math.sin(angle);
			double y = posY + 1;
			double z = posZ + (ticksExisted * getSpeed()) * Math.cos(angle);
			particles.spawnParticles(world, getParticle(), 1, 2, x, y, z, 0.1, 0.1, 0.1);
		}
	}
}
