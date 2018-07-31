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

package com.crowsofwar.avatar.common.network;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

import com.crowsofwar.avatar.*;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.TransferConfirmHandler;
import com.crowsofwar.avatar.common.analytics.*;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.*;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.avatar.common.network.packets.*;
import com.crowsofwar.gorecore.util.AccountUUIDs;

import java.util.*;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;
import static com.crowsofwar.avatar.common.analytics.AnalyticEvents.getAbilityExecutionEvent;

/**
 * Implements IPacketHandler. Acts as a packet handler for integrated and
 * dedicated servers. Is a singleton and is accessible via {@link #instance}.
 */
public class PacketHandlerServer implements IPacketHandler {

	public static final IPacketHandler instance;

	static {
		instance = new PacketHandlerServer();
	}

	private PacketHandlerServer() {
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(instance);
	}

	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		AvatarLog.debug("Server: Received a packet");

		if (packet instanceof PacketSUseAbility) return handleKeypress((PacketSUseAbility) packet, ctx);

		if (packet instanceof PacketSRequestData) return handleRequestData((PacketSRequestData) packet, ctx);

		if (packet instanceof PacketSUseStatusControl) return handleUseStatusControl((PacketSUseStatusControl) packet, ctx);

		if (packet instanceof PacketSWallJump) return handleWallJump((PacketSWallJump) packet, ctx);

		if (packet instanceof PacketSSkillsMenu) return handleSkillsMenu((PacketSSkillsMenu) packet, ctx);

		if (packet instanceof PacketSUseScroll) return handleUseScroll((PacketSUseScroll) packet, ctx);

		if (packet instanceof PacketSBisonInventory) return handleInventory((PacketSBisonInventory) packet, ctx);

		if (packet instanceof PacketSOpenUnlockGui) return handleGetBending((PacketSOpenUnlockGui) packet, ctx);

		if (packet instanceof PacketSUnlockBending) return handleUnlockBending((PacketSUnlockBending) packet, ctx);

		if (packet instanceof PacketSConfirmTransfer) return handleConfirmTransfer((PacketSConfirmTransfer) packet, ctx);

		if (packet instanceof PacketSCycleBending) return handleCycleBending((PacketSCycleBending) packet, ctx);

		AvatarLog.warn(WarningType.BAD_CLIENT_PACKET, "Unknown packet received: " + packet.getClass().getName());
		return null;
	}

	@Override
	public Side getSide() {
		return Side.SERVER;
	}

	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		Bender bender = Bender.get(player);
		if (bender != null) {

			bender.executeAbility(packet.getAbility(), packet.getRaytrace());

			// Send analytics
			String abilityName = packet.getAbility().getName();
			String level = AbilityData.get(player, abilityName).getLevelDesc();
			AvatarAnalytics.INSTANCE.pushEvent(getAbilityExecutionEvent(abilityName, level));

			// If player just got to 100% XP so they can upgrade, send them a message
			AbilityData abilityData = AbilityData.get(player, abilityName);
			boolean notLevel4 = abilityData.getLevel() < 3;
			if (abilityData.getXp() == 100 && abilityData.getLastXp() < 100 && notLevel4) {

				UUID bendingId = packet.getAbility().getBendingId();

				MSG_CAN_UPGRADE_ABILITY.send(player, abilityName, abilityData.getLevel() + 2);
				MSG_CAN_UPGRADE_ABILITY_2.send(player);
				MSG_CAN_UPGRADE_ABILITY_3.send(player, BendingStyles.getName(bendingId));

				// Prevent this message from appearing again by updating lastXp to show current Xp
				abilityData.resetLastXp();

			}

		}

		return null;
	}

	private IMessage handleRequestData(PacketSRequestData packet, MessageContext ctx) {

		UUID id = packet.getAskedPlayer();
		EntityPlayer player = AccountUUIDs.findEntityFromUUID(ctx.getServerHandler().player.world, id);

		if (player == null) {

			AvatarLog.warnHacking(ctx.getServerHandler().player.getName(),
								  "Sent request data for a player with account '" + id + "', but that player is not in the world.");
			return null;

		}

		BendingData data = BendingData.get(player);

		if (data != null) data.saveAll();
		return null;

	}

	/**
	 * @param packet
	 * @param ctx
	 * @return
	 */
	private IMessage handleUseStatusControl(PacketSUseStatusControl packet, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;

		BendingData data = BendingData.get(player);

		if (data != null) {
			StatusControl sc = packet.getStatusControl();
			if (data.hasStatusControl(sc)) {
				if (sc.execute(new BendingContext(data, player, packet.getRaytrace()))) {
					data.removeStatusControl(packet.getStatusControl());
				}
			}

		}

		return null;
	}

	private IMessage handleWallJump(PacketSWallJump packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		Bender bender = Bender.get(player);
		WallJumpManager jumpManager = bender.getWallJumpManager();

		if (jumpManager.knowsWallJump()) {

			if (jumpManager.canWallJump()) {
				jumpManager.doWallJump(jumpManager.getWallJumpParticleType());
			}

		}

		return null;
	}

	private IMessage handleSkillsMenu(PacketSSkillsMenu packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = BendingData.get(player);
		UUID element = packet.getElement();

		if (BendingStyles.has(element)) {
			if (data.hasBendingId(element)) {
				int guiId = AvatarGuiHandler.getGuiId(element);
				player.openGui(AvatarMod.instance, guiId, player.world, 0, 0, 0);

				if (packet.getAbility() != null) {
					return new PacketCOpenSkillCard(packet.getAbility());
				}

			}
		}

		return null;
	}

	private IMessage handleUseScroll(PacketSUseScroll packet, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;

		BendingData data = BendingData.get(player);
		AbilityData abilityData = data.getAbilityData(packet.getAbility());

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
						ScrollType type = ScrollType.get(stack.getMetadata());
						if (type.accepts(packet.getAbility().getBendingId())) {

							activeSlot.putStack(ItemStack.EMPTY);
							abilityData.addLevel();
							abilityData.setXp(0);

							// Send analytics
							String name = abilityData.getAbilityName();
							String desc = abilityData.getLevelDesc();
							AnalyticEvent e = AnalyticEvents.getAbilityUpgradeEvent(name, desc);
							AvatarAnalytics.INSTANCE.pushEvent(e);

						}

					}
				}

			}

		}

		return null;

	}

	private IMessage handleInventory(PacketSBisonInventory packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		if (player.getRidingEntity() instanceof EntitySkyBison) {
			EntitySkyBison bison = (EntitySkyBison) player.getRidingEntity();
			if (bison.canPlayerViewInventory(player)) {
				player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_BISON_CHEST, player.world, bison.getId(), 0, 0);
			}
		}

		return null;
	}

	private IMessage handleGetBending(PacketSOpenUnlockGui packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = BendingData.get(player);

		if (data.getAllBending().isEmpty()) {
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_GET_BENDING, player.world, 0, 0, 0);
		}

		return null;

	}

	private IMessage handleUnlockBending(PacketSUnlockBending packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = BendingData.get(player);
		Container container = player.openContainer;

		if (container instanceof ContainerGetBending) {
			List<UUID> eligible = ((ContainerGetBending) container).getEligibleBending();

			UUID bending = packet.getUnlockType();
			if (eligible.contains(bending)) {

				if (data.getAllBending().isEmpty()) {
					data.addBendingId(bending);

					// Unlock first ability
					// the ID is in use to unlock it
					Ability ability = BendingStyles.get(bending).getAllAbilities().get(0);
					data.getAbilityData(ability).unlockAbility();

					for (int i = 0; i < ((ContainerGetBending) container).getSize(); i++) {
						container.getSlot(i).putStack(ItemStack.EMPTY);
					}

					int guiId = AvatarGuiHandler.getGuiId(bending);
					player.openGui(AvatarMod.instance, guiId, player.world, 0, 0, 0);

				}

			}

		}

		return null;
	}

	private IMessage handleConfirmTransfer(PacketSConfirmTransfer packet, MessageContext ctx) {

		EntityPlayer player = ctx.getServerHandler().player;
		TransferConfirmHandler.confirmTransfer(player);

		return null;
	}

	private IMessage handleCycleBending(PacketSCycleBending packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = BendingData.get(player);

		List<BendingStyle> controllers = data.getAllBending();
		controllers.sort(Comparator.comparing(BendingStyle::getName));
		if (controllers.size() > 1) {

			int index = controllers.indexOf(data.getActiveBending());
			index += packet.cycleRight() ? 1 : -1;

			if (index == -1) index = controllers.size() - 1;
			if (index == controllers.size()) index = 0;

			data.setActiveBending(controllers.get(index));

		}

		return null;

	}

}
