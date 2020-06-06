package com.crowsofwar.avatar.client.particles.newparticles.behaviour;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;

public abstract class ParticleAvatarBehaviour extends ParticleBehaviour<ParticleAvatar> {

	public static void register() {

	}

	public static class WaterBlastBehaviour extends ParticleAvatarBehaviour {

		@Nonnull
		@Override
		public ParticleAvatarBehaviour onUpdate(ParticleAvatar particle) {
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {

		}

		@Override
		public void toBytes(PacketBuffer buf) {

		}

		@Override
		public void load(NBTTagCompound nbt) {

		}

		@Override
		public void save(NBTTagCompound nbt) {

		}
	}
}
