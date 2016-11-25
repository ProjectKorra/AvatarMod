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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.VectorI;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PacketSUseStatusControl extends AvatarPacket<PacketSUseStatusControl> {
	
	private StatusControl statusControl;
	private VectorI lookPos;
	private EnumFacing lookSide;
	
	public PacketSUseStatusControl() {}
	
	public PacketSUseStatusControl(StatusControl control, Raytrace.Result raytrace) {
		this.statusControl = control;
		this.lookPos = raytrace.getPos();
		this.lookSide = raytrace.getSide();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int id = buf.readInt();
		statusControl = StatusControl.lookup(id);
		if (statusControl == null) {
			AvatarLog
					.warn("Player trying to crash the server?? While sending UseStatusControl packet, sent invalid id "
							+ id);
			return; // TODO Cancel packet processing
		}
		
		VectorI readPos = VectorI.fromBytes(buf);
		int readSide = buf.readInt();
		
		if (readSide == -1) {
			lookPos = null;
			lookSide = null;
		} else {
			lookPos = readPos;
			lookSide = EnumFacing.values()[readSide];
		}
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(statusControl.id());
		if (lookPos == null) {
			new VectorI(0, 0, 0).toBytes(buf);
			buf.writeInt(-1);
		} else {
			lookPos.toBytes(buf);
			buf.writeInt(lookSide.ordinal());
		}
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.SERVER;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketSUseStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public StatusControl getStatusControl() {
		return statusControl;
	}
	
	public VectorI getLookPos() {
		return lookPos;
	}
	
	public EnumFacing getLookSide() {
		return lookSide;
	}
	
}
