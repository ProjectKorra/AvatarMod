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

package com.crowsofwar.avatar.network;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.util.helper.GliderHelper;
import com.crowsofwar.avatar.util.TransferConfirmHandler;
import com.crowsofwar.avatar.util.analytics.AnalyticEvent;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.WallJumpManager;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.util.event.AbilityLevelEvent;
import com.crowsofwar.avatar.util.event.AbilityUnlockEvent;
import com.crowsofwar.avatar.util.event.ElementUnlockEvent;
import com.crowsofwar.avatar.util.event.ParticleCollideEvent;
import com.crowsofwar.avatar.client.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.client.gui.ContainerGetBending;
import com.crowsofwar.avatar.client.gui.ContainerSkillsGui;
import com.crowsofwar.avatar.item.scroll.ItemScroll;
import com.crowsofwar.avatar.item.scroll.Scrolls;
import com.crowsofwar.avatar.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.avatar.network.packets.*;
import com.crowsofwar.avatar.network.packets.glider.PacketSServerGliding;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.network.AvatarChatMessages.*;
import static com.crowsofwar.avatar.util.analytics.AnalyticEvents.getAbilityExecutionEvent;
import static com.crowsofwar.avatar.network.packets.glider.PacketSServerGliding.IS_GLIDING;

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

		if (packet instanceof PacketSUseAbility)
			return handleKeypress((PacketSUseAbility) packet, ctx);

		if (packet instanceof PacketSRequestData)
			return handleRequestData((PacketSRequestData) packet, ctx);

		if (packet instanceof PacketSUseStatusControl)
			return handleUseStatusControl((PacketSUseStatusControl) packet, ctx);

		if (packet instanceof PacketSWallJump)
			return handleWallJump((PacketSWallJump) packet, ctx);

		if (packet instanceof PacketSSkillsMenu)
			return handleSkillsMenu((PacketSSkillsMenu) packet, ctx);

		if (packet instanceof PacketSUseScroll)
			return handleUseScroll((PacketSUseScroll) packet, ctx);

		if (packet instanceof PacketSBisonInventory)
			return handleInventory((PacketSBisonInventory) packet, ctx);

		if (packet instanceof PacketSOpenUnlockGui)
			return handleGetBending((PacketSOpenUnlockGui) packet, ctx);

		if (packet instanceof PacketSUnlockBending)
			return handleUnlockBending((PacketSUnlockBending) packet, ctx);

		if (packet instanceof PacketSConfirmTransfer)
			return handleConfirmTransfer((PacketSConfirmTransfer) packet, ctx);

		if (packet instanceof PacketSCycleBending)
			return handleCycleBending((PacketSCycleBending) packet, ctx);

		if (packet instanceof PacketSSendViewStatus)
			return handleViewUpdate((PacketSSendViewStatus) packet, ctx);

		if (packet instanceof PacketSServerGliding)
			return handleServerGliding((PacketSServerGliding) packet, ctx);

		if (packet instanceof PacketSParticleCollideEvent) {
			MinecraftForge.EVENT_BUS.post(new ParticleCollideEvent(((PacketSParticleCollideEvent) packet).getEntity(),
					((PacketSParticleCollideEvent) packet).getSpawnerEntity(), ((PacketSParticleCollideEvent) packet).getAbility(),
					((PacketSParticleCollideEvent) packet).getVelocity()));
			return null;
		}

		AvatarLog.warn("Unknown packet recieved: " + packet.getClass().getName());
		return null;
	}

	@Override
	public Side getSide() {
		return Side.SERVER;
	}

	private IMessage handleViewUpdate(PacketSSendViewStatus packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		if (player != null) {
			PlayerViewRegistry.setPlayerViewInRegistry(player.getUniqueID(), packet.getMode());
		}

		return null;
	}

	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		Bender bender = Bender.get(player);
		if (bender != null) {

			bender.executeAbility(packet.getAbility(), packet.getRaytrace(), packet.getSwitchpath());

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

			player = AvatarEntityUtils.getPlayerFromStringID(id.toString());
			if (player == null) {

				AvatarLog.warnHacking(ctx.getServerHandler().player.getName(),
						"Sent request data for a player with account '" + id
								+ "', but that player is not in the world.");
				return null;
			}
		}

		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);

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

		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);


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

			//noinspection ConstantConditions
			if (jumpManager.canWallJump()) {
				jumpManager.doWallJump(jumpManager.getWallJumpParticleType());
			}

		}

		return null;
	}

	private IMessage handleSkillsMenu(PacketSSkillsMenu packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);

		UUID element = packet.getElement();

		if (BendingStyles.has(element) && data != null) {
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

		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);

		if (data != null) {
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
						if (stack.getItem() instanceof ItemScroll) {
							// Try to use this scroll
							ScrollType type = Scrolls.getTypeForStack(stack);
							assert type != null;
							AbilityContext aCtx = new AbilityContext(data, player, Bender.get(player), new Raytrace.Result(), packet.getAbility(),
									Bender.get(player).calcPowerRating(packet.getAbility().getBendingId()), false);
							if (type.accepts(packet.getAbility().getBendingId()) && packet.getAbility().isCompatibleScroll(stack, aCtx)) {
								if (abilityData.getLevel() < 0 && !MinecraftForge.EVENT_BUS.post(new AbilityUnlockEvent(player, abilityData.getAbility()))
										|| !MinecraftForge.EVENT_BUS.post(new AbilityLevelEvent(player, abilityData.getAbility(), abilityData.getLevel() + 1, abilityData.getLevel() + 2))) {
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

			}
		}

		return null;

	}

	private IMessage handleInventory(PacketSBisonInventory packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		if (player.getRidingEntity() instanceof EntitySkyBison) {
			EntitySkyBison bison = (EntitySkyBison) player.getRidingEntity();
			if (bison.canPlayerViewInventory(player)) {
				player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_BISON_CHEST, player.world,
						bison.getId(), 0, 0);
			}
		}

		return null;
	}

	private IMessage handleGetBending(PacketSOpenUnlockGui packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);


		if (data != null && data.getAllBending().isEmpty()) {
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_GET_BENDING, player.world, 0, 0, 0);
		}

		return null;

	}

	private IMessage handleUnlockBending(PacketSUnlockBending packet, MessageContext ctx) {

		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);

		Container container = player.openContainer;

		if (container instanceof ContainerGetBending) {
			List<UUID> eligible = ((ContainerGetBending) container).getEligibleBending();

			UUID bending = packet.getUnlockType();
			if (eligible.contains(bending)) {
				if (data != null && data.getAllBending().isEmpty()) {
					if (!MinecraftForge.EVENT_BUS.post(new ElementUnlockEvent(player, BendingStyles.get(bending)))) {
						data.addBendingId(bending);

						// Unlock first ability
						//noinspection ConstantConditions - can safely assume bending is present if
						// the ID is in use to unlock it
						Ability ability = Objects.requireNonNull(BendingStyles.get(bending)).getAllAbilities().get(0);
						if (!MinecraftForge.EVENT_BUS.post(new AbilityUnlockEvent(player, ability)))
							data.getAbilityData(ability).unlockAbility();

						for (int i = 0; i < ((ContainerGetBending) container).getSize(); i++) {
							container.getSlot(i).putStack(ItemStack.EMPTY);
						}

						int guiId = AvatarGuiHandler.getGuiId(bending);
						player.openGui(AvatarMod.instance, guiId, player.world, 0, 0, 0);

					}
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
		BendingData data = null;
		if (Bender.isBenderSupported(player))
			data = BendingData.get(player);

		if (data != null) {

			List<BendingStyle> controllers = data.getAllBending();
			controllers.sort(Comparator.comparing(BendingStyle::getName));
			if (controllers.size() > 1) {

				int index = controllers.indexOf(data.getActiveBending());
				index += packet.cycleRight() ? 1 : -1;

				if (index == -1) index = controllers.size() - 1;
				if (index == controllers.size()) index = 0;

				data.setActiveBending(controllers.get(index));

			}
		}
		return null;

	}

	private IMessage handleServerGliding(PacketSServerGliding packet, MessageContext ctx) {

		GliderHelper.setIsGliderDeployed(ctx.getServerHandler().player, packet.isGliding == IS_GLIDING);

		return null; //no return message

	}

}
