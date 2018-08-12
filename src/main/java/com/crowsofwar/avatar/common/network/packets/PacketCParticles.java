/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import io.netty.buffer.ByteBuf;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class PacketCParticles extends AvatarPacket<PacketCParticles> {
	private EnumParticleTypes particle;
	private int minimum, maximum;
	private double x, y, z;
	private double maxVelocityX, maxVelocityY, maxVelocityZ;

	public PacketCParticles() {
	}

	public PacketCParticles(EnumParticleTypes particle, int minimum, int maximum, double x, double y, double z, double maxVelocityX,
					double maxVelocityY, double maxVelocityZ) {
		this.particle = particle;
		this.minimum = minimum;
		this.maximum = maximum;
		this.x = x;
		this.y = y;
		this.z = z;
		this.maxVelocityX = maxVelocityX;
		this.maxVelocityY = maxVelocityY;
		this.maxVelocityZ = maxVelocityZ;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		particle = EnumParticleTypes.values()[buf.readInt()];
		minimum = buf.readInt();
		maximum = buf.readInt();
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		maxVelocityX = buf.readDouble();
		maxVelocityY = buf.readDouble();
		maxVelocityZ = buf.readDouble();
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(particle.ordinal());
		buf.writeInt(minimum);
		buf.writeInt(maximum);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(maxVelocityX);
		buf.writeDouble(maxVelocityY);
		buf.writeDouble(maxVelocityZ);
	}

	public EnumParticleTypes getParticle() {
		return particle;
	}

	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getMaxVelocityX() {
		return maxVelocityX;
	}

	public double getMaxVelocityY() {
		return maxVelocityY;
	}

	public double getMaxVelocityZ() {
		return maxVelocityZ;
	}

	public static class Handler extends AvatarPacketHandler<PacketCParticles, IMessage> {

		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketCParticles message, MessageContext ctx) {
			EnumParticleTypes particle = message.getParticle();
			if (particle == null) {
				AvatarLog.warn(WarningType.WEIRD_PACKET, "Unknown particle received from server");
				return null;
			}
			Random random = new Random();
			int particles = random.nextInt(message.getMaximum() - message.getMinimum() + 1) + message.getMinimum();
			for (int i = 0; i < particles; i++) {
				Minecraft.getMinecraft().world.spawnParticle(particle, message.getX(), message.getY(), message.getZ(),
															 message.getMaxVelocityX() * random.nextGaussian(),
															 message.getMaxVelocityY() * random.nextGaussian(),
															 message.getMaxVelocityZ() * random.nextGaussian());
			}
			return null;
		}
	}
}
