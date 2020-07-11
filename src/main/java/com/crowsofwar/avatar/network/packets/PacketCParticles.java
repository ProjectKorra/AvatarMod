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

package com.crowsofwar.avatar.network.packets;

import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author CrowsOfWar
 */
public class PacketCParticles extends AvatarPacket<PacketCParticles> {

	private EnumParticleTypes particle;
	private int minimum, maximum;
	private double x, y, z;
	private double maxVelocityX, maxVelocityY, maxVelocityZ;
	private boolean velIsMagnitude;

	public PacketCParticles() {
	}

	/**
	 * @param particle
	 * @param minimum
	 * @param maximum
	 * @param x
	 * @param y
	 * @param z
	 * @param maxVelocityX
	 * @param maxVelocityY
	 * @param maxVelocityZ
	 */
	public PacketCParticles(EnumParticleTypes particle, int minimum, int maximum, double x, double y,
							double z, double maxVelocityX, double maxVelocityY, double maxVelocityZ, boolean velIsMagnitude) {
		this.particle = particle;
		this.minimum = minimum;
		this.maximum = maximum;
		this.x = x;
		this.y = y;
		this.z = z;
		this.maxVelocityX = maxVelocityX;
		this.maxVelocityY = maxVelocityY;
		this.maxVelocityZ = maxVelocityZ;
		this.velIsMagnitude = velIsMagnitude;
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
		velIsMagnitude = buf.readBoolean();
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
		buf.writeBoolean(velIsMagnitude);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.CLIENT;
	}

	@Override
	protected com.crowsofwar.avatar.network.packets.AvatarPacket.Handler<PacketCParticles> getPacketHandler() {
		return PacketRedirector::redirectMessage;
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

	public boolean getVelIsMagnitude() {
		return velIsMagnitude;
	}

}
