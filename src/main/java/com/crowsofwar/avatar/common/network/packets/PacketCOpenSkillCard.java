package com.crowsofwar.avatar.common.network.packets;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.client.gui.skills.SkillsGui;
import com.crowsofwar.avatar.common.bending.*;
import io.netty.buffer.ByteBuf;

/**
 * @author CrowsOfWar
 */
public class PacketCOpenSkillCard extends AvatarPacket<PacketCOpenSkillCard> {

	private int abilityId;

	public PacketCOpenSkillCard() {
	}

	public PacketCOpenSkillCard(Ability ability) {
		abilityId = Abilities.all().indexOf(ability);
	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {
		abilityId = buf.readInt();
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		buf.writeInt(abilityId);
	}

	public Ability getAbility() {
		return Abilities.all().get(abilityId);
	}

	public static class Handler extends AvatarPacketHandler<PacketCOpenSkillCard, IMessage> {
		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketCOpenSkillCard message, MessageContext ctx) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen instanceof SkillsGui) {
				((SkillsGui) mc.currentScreen).openWindow(message.getAbility());
			}
			return null;
		}
	}
}
