package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;

public class FireParticleSpawner extends TickHandler {
	private static final ParticleSpawner particles = new NetworkParticleSpawner();

	public FireParticleSpawner(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase target = ctx.getBenderEntity();
		Bender bender = ctx.getBender();

		Vector pos = Vector.getEntityPos(target).minusY(0.05);

		particles.spawnParticles(target.world, AvatarParticles.getParticleFlames(), 8, 20, pos,
				new Vector(0.7, 0.2, 0.7));

		return target.isInWater() || target.onGround || bender.isFlying();

	}

}

