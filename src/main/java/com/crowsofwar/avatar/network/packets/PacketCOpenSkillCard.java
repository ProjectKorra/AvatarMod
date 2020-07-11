package com.crowsofwar.avatar.network.packets;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author CrowsOfWar
 */
public class PacketCOpenSkillCard extends AvatarPacket<PacketCOpenSkillCard> {

	private int abilityId;

	public PacketCOpenSkillCard() {
	}

	public PacketCOpenSkillCard(Ability ability) {
		this.abilityId = Abilities.all().indexOf(ability);
	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {
		abilityId = buf.readInt();
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		buf.writeInt(abilityId);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.CLIENT;
	}

	@Override
	protected Handler<PacketCOpenSkillCard> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public Ability getAbility() {
		return Abilities.all().get(abilityId);
	}

}
