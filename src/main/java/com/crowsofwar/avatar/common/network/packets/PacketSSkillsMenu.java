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

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class PacketSSkillsMenu extends AvatarPacket<PacketSSkillsMenu> {

	private byte element;
	private int abilityId;

	public PacketSSkillsMenu() {
	}

	public PacketSSkillsMenu(UUID element) {
		this(element, null);
	}

	public PacketSSkillsMenu(UUID element, @Nullable Ability ability) {
		this.element = BendingStyles.getNetworkId(element);
		if (ability == null) {
			abilityId = -1;
		} else {
			abilityId = Abilities.all().indexOf(ability);
		}
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		element = buf.readByte();
		abilityId = buf.readInt();
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeByte(element);
		buf.writeInt(abilityId);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected com.crowsofwar.avatar.common.network.packets.AvatarPacket.Handler<PacketSSkillsMenu> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public UUID getElement() {
		return BendingStyles.get(element).getId();
	}

	@Nullable
	public Ability getAbility() {
		if (abilityId == -1) {
			return null;
		}
		return Abilities.all().get(abilityId);
	}

}
