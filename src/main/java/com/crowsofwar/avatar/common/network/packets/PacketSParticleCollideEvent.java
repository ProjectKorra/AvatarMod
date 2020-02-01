package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PacketSParticleCollideEvent extends AvatarPacket<PacketSParticleCollideEvent> {

	//REFLECTION AH
	private ParticleAvatar particle;
	private Entity entity;

	public PacketSParticleCollideEvent(Entity entity, ParticleAvatar particle) {
		this.entity = entity;
		this.particle = particle;
	}

	public PacketSParticleCollideEvent() {

	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {

		PacketBuffer buffer = new PacketBuffer(buf);
		entity = AvatarEntityUtils.getEntityFromStringID(buffer.readUniqueId().toString());
		//I regret nothing
		particle = AvatarUtils.getParticleFromUUID(buffer.readUniqueId());
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeUniqueId(entity.getUniqueID());
		buffer.writeUniqueId(particle.getUUID());
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

	public ParticleAvatar getParticle() {
		return this.particle;
	}
}
