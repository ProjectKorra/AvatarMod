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

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.VectorI;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet which tells the server that the client pressed a control. The control
 * is given to the player's active bending controller.
 * 
 * @see AvatarControl
 *
 */
public class PacketSUseAbility extends AvatarPacket<PacketSUseAbility> {
	
	private BendingAbility ability;
	private VectorI target;
	/** ID of EnumFacing of the side of the block player is looking at */
	private EnumFacing side;
	
	public PacketSUseAbility() {}
	
	public PacketSUseAbility(BendingAbility ability, VectorI target, EnumFacing side) {
		this.ability = ability;
		this.target = target;
		this.side = side;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		ability = BendingManager.getAbility(buf.readInt());
		if (ability == null) {
			throw new NullPointerException("Server sent invalid ability over network: ID " + ability);
		}
		target = buf.readBoolean() ? VectorI.fromBytes(buf) : null;
		side = EnumFacing.getFront(buf.readInt());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ability.getId());
		buf.writeBoolean(target != null);
		if (target != null) {
			target.toBytes(buf);
		}
		buf.writeInt(side == null ? -1 : side.ordinal());
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
	public VectorI getTargetPos() {
		return target;
	}
	
	public EnumFacing getSideHit() {
		return side;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketSUseAbility> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
}
