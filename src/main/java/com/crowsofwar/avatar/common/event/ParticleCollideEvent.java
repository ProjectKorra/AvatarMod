package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

//Bad practice, but allows for good beam collision (check to see if particles have collided with an entity).
//E.g. Flamethrower.
//Called server side through packets
public class ParticleCollideEvent extends EntityEvent {

	private ParticleAvatar particle;

	public ParticleCollideEvent(Entity entity, ParticleAvatar particle) {
		super(entity);
		this.particle = particle;
	}

	public ParticleAvatar getParticle() {
		return this.particle;
	}
}
