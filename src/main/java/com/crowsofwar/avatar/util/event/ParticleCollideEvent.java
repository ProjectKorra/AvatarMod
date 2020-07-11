package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.UUID;

//Bad practice, but allows for good beam collision (check to see if particles have collided with an entity).
//E.g. Flamethrower.
//Called server side through packets
public class ParticleCollideEvent extends EntityEvent {

//	private ParticleAvatar particle;
	private Entity spawner;
	private UUID bendingID;
	private Ability ability;
	private Vec3d velocity;

	public ParticleCollideEvent(Entity entity, Entity spawner, UUID bendingID, Vec3d velocity) {
		super(entity);
		//this.particle = particle;
		this.spawner = spawner;
		this.bendingID = bendingID;
		this.velocity = velocity;
	}

	public ParticleCollideEvent(Entity entity, Entity spawner, Ability ability, Vec3d velocity) {
		super(entity);
	//	this.particle = particle;
		this.spawner = spawner;
		this.ability = ability;
		this.bendingID = ability == null ? Airbending.ID : ability.getBendingId();
		this.velocity = velocity;
	}

	/*public ParticleAvatar getParticle() {
		return this.particle;
	}**/

	public Entity getSpawner() {
		return this.spawner;
	}

	public UUID getBendingID() {
		return bendingID;
	}

	public Ability getAbility() {
		return this.ability;
	}

	public Vec3d getVelocity() {
		return this.velocity;
	}
}
