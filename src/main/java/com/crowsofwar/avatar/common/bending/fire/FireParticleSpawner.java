package com.crowsofwar.avatar.common.bending.fire;

import net.minecraft.entity.EntityLivingBase;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.*;
import com.crowsofwar.gorecore.util.Vector;

public class FireParticleSpawner extends TickHandler {
	private static final ParticleSpawner particles = new NetworkParticleSpawner();

	public FireParticleSpawner(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase target = ctx.getBenderEntity();
		Bender bender = ctx.getBender();

		Vector pos = Vector.getEntityPos(target).plus(0, 1.3, 0);

		particles.spawnParticles(target.world, AvatarParticles.getParticleFlames(), 15, 20, pos, new Vector(0.7, 0.2, 0.7));

		return target.isInWater() || target.onGround || bender.isFlying();

	}

}

