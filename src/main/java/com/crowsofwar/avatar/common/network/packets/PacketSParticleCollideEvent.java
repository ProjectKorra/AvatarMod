package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import com.crowsofwar.avatar.client.particles.oldsystem.AvatarParticle;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

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
		//First 36 characters is the first uuid
		entity = AvatarEntityUtils.getEntityFromStringID(ByteBufUtils.readUTF8String(buf.readBytes(36)));
		//I regret nothing
		particle = (ParticleAvatar) AvatarUtils.getAliveParticles().stream().filter(particle1 -> particle1 instanceof AvatarParticle &&
				((ParticleAvatar) particle1).getUUID().equals(UUID.fromString(ByteBufUtils.readUTF8String(buf.readBytes(36)))))
				.collect(Collectors.toList()).get(0);
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		buf.writeBytes(entity.getUniqueID().toString().getBytes());
		buf.writeBytes(particle.getUUID().toString().getBytes());
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
