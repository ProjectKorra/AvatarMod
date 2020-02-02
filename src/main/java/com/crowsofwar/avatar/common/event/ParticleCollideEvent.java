package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import com.crowsofwar.avatar.common.bending.Ability;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.UUID;

//Bad practice, but allows for good beam collision (check to see if particles have collided with an entity).
//E.g. Flamethrower.
//Called server side through packets
public class ParticleCollideEvent extends EntityEvent {

	private ParticleAvatar particle;
	private Entity spawner;
	private UUID bendingID;

	public ParticleCollideEvent(Entity entity, ParticleAvatar particle, Entity spawner, UUID bendingID) {
		super(entity);
		this.particle = particle;
		this.spawner = spawner;
		this.bendingID = bendingID;
	}

	public ParticleAvatar getParticle() {
		return this.particle;
	}

	public Entity getSpawner() {
		return this.spawner;
	}

	public UUID getBendingID() {
		return bendingID;
	}
}
