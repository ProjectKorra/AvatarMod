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

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;
import static com.crowsofwar.avatar.common.analytics.AnalyticEvents.getAbilityExecutionEvent;

/**
 * Packet which tells the server that the client pressed a control. The control
 * is given to the player's active bending controller.
 *
 * @see AvatarControl
 */
public class PacketSUseAbility extends AvatarPacket<PacketSUseAbility> {
	private Ability ability;
	private Raytrace.Result raytrace;

	public PacketSUseAbility() {
	}

	public PacketSUseAbility(Ability ability, Raytrace.Result raytrace) {
		this.ability = ability;
		this.raytrace = raytrace;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		String abilityName = GoreCoreByteBufUtil.readString(buf);
		ability = Abilities.get(abilityName);
		if (ability == null) {
			throw new NullPointerException("Server sent invalid ability over network: ID " + abilityName);
		}
		raytrace = Raytrace.Result.fromBytes(buf);
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeString(buf, ability.getName());
		raytrace.toBytes(buf);
	}

	public Ability getAbility() {
		return ability;
	}

	public Raytrace.Result getRaytrace() {
		return raytrace;
	}

	public static class Handler extends AvatarPacketHandler<PacketSUseAbility, IMessage> {
		@Override
		public IMessage avatarOnMessage(PacketSUseAbility message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			Bender bender = Bender.get(player);
			if (bender != null) {
				bender.executeAbility(message.getAbility(), message.getRaytrace());
				// Send analytics
				String abilityName = message.getAbility().getName();
				String level = AbilityData.get(player, abilityName).getLevelDesc();
				AvatarAnalytics.INSTANCE.pushEvent(getAbilityExecutionEvent(abilityName, level));

				// If player just got to 100% XP so they can upgrade, send them a message
				AbilityData abilityData = AbilityData.get(player, abilityName);
				boolean notLevel4 = abilityData.getLevel() < 3;
				if (abilityData.getXp() == 100 && abilityData.getLastXp() < 100 && notLevel4) {

					UUID bendingId = message.getAbility().getBendingId();

					MSG_CAN_UPGRADE_ABILITY.send(player, abilityName, abilityData.getLevel() + 2);
					MSG_CAN_UPGRADE_ABILITY_2.send(player);
					MSG_CAN_UPGRADE_ABILITY_3.send(player, BendingStyles.getName(bendingId));

					// Prevent this message from appearing again by updating lastXp to show current Xp
					abilityData.resetLastXp();
				}
			}
			return null;
		}
	}
}
