package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class PacketSParticleCollideEvent extends AvatarPacket<PacketSParticleCollideEvent> {

	//REFLECTION AH
	//private ParticleAvatar particle;
	private Entity entity;
	private UUID bendingID;
	private Entity spawner;
	private Ability ability;
	private Vec3d velocity;

	public PacketSParticleCollideEvent(Entity entity, Vec3d velocity, Entity spawner, UUID bendingID) {
		this.entity = entity;
	//	this.particle = particle;
		this.bendingID = bendingID;
		this.spawner = spawner;
		this.velocity = velocity;
	}

	public PacketSParticleCollideEvent(Entity entity, Vec3d velocity, Entity spawner, Ability ability) {
		this.entity = entity;
		//this.particle = particle;
		this.ability = ability;
		this.bendingID = ability.getBendingId() == null || ability == null ? Airbending.ID : ability.getBendingId();
		this.spawner = spawner;
		this.velocity = velocity;
	}

	public PacketSParticleCollideEvent() {

	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {

		//TODO: Read and write abilities!

		PacketBuffer buffer = new PacketBuffer(buf);
		entity = AvatarEntityUtils.getEntityFromStringID(buffer.readUniqueId().toString());
		//I regret nothing
		//particle = AvatarUtils.getParticleFromUUID(buffer.readUniqueId());
		spawner = AvatarEntityUtils.getEntityFromStringID(buffer.readUniqueId().toString());
		bendingID = buffer.readUniqueId();
		ability = Abilities.get(buffer.readString(buffer.readVarInt()));
		velocity = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());

	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeUniqueId(entity.getUniqueID());
		//buffer.writeUniqueId(particle.getUUID());
		buffer.writeUniqueId(spawner.getUniqueID());
		buffer.writeUniqueId(bendingID);
		buffer.writeVarInt(ability.getName().length());
		buffer.writeString(ability.getName());
		buffer.writeDouble(velocity.x);
		buffer.writeDouble(velocity.y);
		buffer.writeDouble(velocity.z);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected Handler<PacketSParticleCollideEvent> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public Entity getEntity() {
		return this.entity;
	}

	/*public ParticleAvatar getParticle() {
		return this.particle;
	}**/

	public Entity getSpawnerEntity() {
		return this.spawner;
	}

	public UUID getBendingID() {
		return this.bendingID;
	}

	public Ability getAbility() {
		return this.ability;
	}

	public Vec3d getVelocity() {
		return this.velocity;
	}
}
