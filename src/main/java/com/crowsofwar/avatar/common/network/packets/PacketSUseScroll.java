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
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.common.analytics.*;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

/**
 * @author CrowsOfWar
 */
public class PacketSUseScroll extends AvatarPacket<PacketSUseScroll> {

	private Ability ability;

	public PacketSUseScroll() {
	}

	public PacketSUseScroll(Ability ability) {
		this.ability = ability;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		ability = Abilities.get(GoreCoreByteBufUtil.readString(buf));
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeString(buf, ability.getName());
	}

	public Ability getAbility() {
		return ability;
	}

	public static class Handler extends AvatarPacketHandler<PacketSUseScroll, IMessage> {

		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSUseScroll message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			AbilityData abilityData = BendingData.get(player).getAbilityData(message.getAbility());

			if (!abilityData.isMaxLevel() && (abilityData.getXp() == 100 || abilityData.isLocked())) {
				Container container = player.openContainer;
				if (container instanceof ContainerSkillsGui) {
					ContainerSkillsGui skills = (ContainerSkillsGui) container;
					Slot slot1 = skills.getSlot(0);
					Slot slot2 = skills.getSlot(1);
					Slot activeSlot = null;
					if (slot1.getHasStack()) {
						activeSlot = slot1;
						abilityData.setPath(AbilityTreePath.FIRST);
					} else if (slot2.getHasStack()) {
						activeSlot = slot2;
						abilityData.setPath(AbilityTreePath.SECOND);
					}
					if (activeSlot != null) {
						ItemStack stack = activeSlot.getStack();
						if (stack.getItem() == AvatarItems.itemScroll) {
							// Try to use this scroll
							if (Objects.requireNonNull(ScrollType.get(stack.getMetadata())).accepts(message.getAbility().getBendingId())) {
								activeSlot.putStack(ItemStack.EMPTY);
								abilityData.addLevel();
								abilityData.setXp(0);

								// Send analytics
								AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.getAbilityUpgradeEvent(abilityData.getAbilityName(),
																										 abilityData.getLevelDesc()));
							}
						}
					}
				}
			}
			return null;
		}
	}
}
