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

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.network.PacketRedirector;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * Packet which tells the server that the client pressed a control. The control
 * is given to the player's active bending controller.
 *
 * @see AvatarControl
 */

//TODO: Make a client-side equivalent and call it as well instead of directly executing the ability
	//Allows for other players to see ability vfx
public class PacketSUseAbility extends AvatarPacket<PacketSUseAbility> {

	private Ability ability;
	private Raytrace.Result raytrace;
	private boolean switchPath;
	//Source bender of this
	private UUID bender;

	public PacketSUseAbility() {
	}

	public PacketSUseAbility(Ability ability, Raytrace.Result raytrace, boolean switchPath, UUID bender) {
		this.ability = ability;
		this.raytrace = raytrace;
		this.switchPath = switchPath;
		this.bender = bender;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		ability = Abilities.get(GoreCoreByteBufUtil.readString(buf));
		if (ability == null) {
			throw new NullPointerException("Server sent invalid ability over network: ID " + ability);
		}
		raytrace = Raytrace.Result.fromBytes(buf);
		switchPath = buf.readBoolean();
		bender = GoreCoreByteBufUtil.readUUID(buf);
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeString(buf, ability.getName());
		raytrace.toBytes(buf);
		buf.writeBoolean(switchPath);
		GoreCoreByteBufUtil.writeUUID(buf, bender);
	}

	@Override
	public Side getReceivedSide() {
		return Side.SERVER;
	}

	public Ability getAbility() {
		return ability;
	}

	public boolean getSwitchpath(){
		return switchPath;
	}

	public Raytrace.Result getRaytrace() {
		return raytrace;
	}

	public UUID getBender() {
		return bender;
	}

	@Override
	protected AvatarPacket.Handler<PacketSUseAbility> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

}
