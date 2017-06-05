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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.TransferConfirmHandler;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.gui.ContainerGetBending;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.avatar.common.network.packets.PacketCErrorMessage;
import com.crowsofwar.avatar.common.network.packets.PacketSBisonInventory;
import com.crowsofwar.avatar.common.network.packets.PacketSConfirmTransfer;
import com.crowsofwar.avatar.common.network.packets.PacketSCycleBending;
import com.crowsofwar.avatar.common.network.packets.PacketSOpenUnlockGui;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSSkillsMenu;
import com.crowsofwar.avatar.common.network.packets.PacketSUnlockBending;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseScroll;
import com.crowsofwar.avatar.common.network.packets.PacketSUseStatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketSWallJump;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Implements IPacketHandler. Acts as a packet handler for integrated and
 * dedicated servers. Is a singleton and is accessible via {@link #instance}.
 *
 */
public class PacketHandlerServer implements IPacketHandler {
	
	public static final IPacketHandler instance;
	private List<ProcessAbilityRequest> unprocessedAbilityRequests;
	
	static {
		instance = new PacketHandlerServer();
	}
	
	private PacketHandlerServer() {
		unprocessedAbilityRequests = new ArrayList<>();
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	@SubscribeEvent
	public void tick(WorldTickEvent e) {
		World world = e.world;
		if (e.phase == TickEvent.Phase.START && !world.isRemote) {
			Iterator<ProcessAbilityRequest> iterator = unprocessedAbilityRequests.iterator();
			while (iterator.hasNext()) {
				ProcessAbilityRequest par = iterator.next();
				par.ticks--;
				if (par.ticks <= 0 && par.data.getAbilityCooldown() == 0) {
					par.ability.execute(new AbilityContext(par.data, par.raytrace, par.ability));
					iterator.remove();
				}
			}
		}
	}
	
	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		AvatarLog.debug("Server: Received a packet");
		
		if (packet instanceof PacketSUseAbility) return handleKeypress((PacketSUseAbility) packet, ctx);
		
		if (packet instanceof PacketSRequestData) return handleRequestData((PacketSRequestData) packet, ctx);
		
		if (packet instanceof PacketSUseStatusControl)
			return handleUseStatusControl((PacketSUseStatusControl) packet, ctx);
		
		if (packet instanceof PacketSWallJump) return handleWallJump((PacketSWallJump) packet, ctx);
		
		if (packet instanceof PacketSSkillsMenu) return handleSkillsMenu((PacketSSkillsMenu) packet, ctx);
		
		if (packet instanceof PacketSUseScroll) return handleUseScroll((PacketSUseScroll) packet, ctx);
		
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
		
		AvatarLog.warn("Unknown packet recieved: " + packet.getClass().getName());
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data != null) {
			
			BendingAbility ability = packet.getAbility();
			if (data.hasBending(ability.getBendingType())) {
				if (!data.getAbilityData(ability).isLocked()) {
					if (data.getAbilityCooldown() == 0) {
						AbilityContext abilityCtx = new AbilityContext(data, packet.getRaytrace(), ability);
						ability.execute(abilityCtx);
						data.setAbilityCooldown(ability.getCooldown(abilityCtx));
					} else {
						unprocessedAbilityRequests.add(new ProcessAbilityRequest(data.getAbilityCooldown(),
								player, data, ability, packet.getRaytrace()));
					}
				} else {
					AvatarMod.network.sendTo(new PacketCErrorMessage("avatar.abilityLocked"), player);
				}
			}
			
		}
		
		return null;
	}
	
	private IMessage handleRequestData(PacketSRequestData packet, MessageContext ctx) {
		
		UUID id = packet.getAskedPlayer();
		EntityPlayer player = AccountUUIDs.findEntityFromUUID(ctx.getServerHandler().playerEntity.worldObj,
				id);
		
		if (player == null) {
			
			AvatarLog.warnHacking(ctx.getServerHandler().playerEntity.getName(),
					"Sent request data for a player with account '" + id
							+ "', but that player is not in the world.");
			return null;
			
		}
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		
		if (data != null) data.saveAll();
		return null;
		
	}
	
	/**
	 * @param packet
	 * @param ctx
	 * @return
	 */
	private IMessage handleUseStatusControl(PacketSUseStatusControl packet, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		
		if (data != null) {
			StatusControl sc = packet.getStatusControl();
			if (data.hasStatusControl(sc)) {
				if (sc.execute(new BendingContext(data, packet.getRaytrace()))) {
					data.removeStatusControl(packet.getStatusControl());
				}
			}
			
		}
		
		return null;
	}
	
	private IMessage handleWallJump(PacketSWallJump packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data.hasBending(BendingType.AIRBENDING) && !data.isWallJumping()
				&& data.getTimeInAir() >= STATS_CONFIG.wallJumpDelay) {
			
			data.setWallJumping(true);
			
			// Detect direction to jump
			Vector normal = Vector.UP;
			Block block = null;
			{
				BlockPos pos = new BlockPos(player).north();
				if (!world.isAirBlock(pos)) {
					normal = Vector.NORTH;
					block = world.getBlockState(pos).getBlock();
				}
			}
			{
				BlockPos pos = new BlockPos(player).south();
				if (!world.isAirBlock(pos)) {
					normal = Vector.SOUTH;
					block = world.getBlockState(pos).getBlock();
				}
			}
			{
				BlockPos pos = new BlockPos(player).east();
				if (!world.isAirBlock(pos)) {
					normal = Vector.EAST;
					block = world.getBlockState(pos).getBlock();
				}
			}
			{
				BlockPos pos = new BlockPos(player).west();
				if (!world.isAirBlock(pos)) {
					normal = Vector.WEST;
					block = world.getBlockState(pos).getBlock();
				}
			}
			
			if (normal != Vector.UP) {
				
				Vector velocity = new Vector(player.motionX, player.motionY, player.motionZ);
				Vector n = velocity.reflect(normal).mul(4).subtract(normal.times(0.5)).setY(0.5);
				n.add(Vector.getLookRectangular(player).mul(.8));
				
				if (n.sqrMagnitude() > 1) {
					n.normalize().mul(1);
				}
				
				player.setVelocity(n.x(), n.y(), n.z());
				player.connection.sendPacket(new SPacketEntityVelocity(player));
				
				new NetworkParticleSpawner().spawnParticles(world, AvatarParticles.getParticleAir(), 4, 10,
						new Vector(player).plus(n), n.times(3));
				world.playSound(null, new BlockPos(player), block.getSoundType().getBreakSound(),
						SoundCategory.PLAYERS, 1, 0.6f);
				
				data.setFallAbsorption(3);
				
			}
			
		}
		
		return null;
	}
	
	private IMessage handleSkillsMenu(PacketSSkillsMenu packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		BendingData data = AvatarPlayerData.fetcher().fetch(player);
		int el = packet.getElement();
		
		if (el >= 1 && el <= 4) {
			if (data.hasBending(BendingType.find(el))) {
				player.openGui(AvatarMod.instance, el, player.worldObj, 0, 0, 0);
			}
		}
		
		return null;
	}
	
	private IMessage handleUseScroll(PacketSUseScroll packet, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
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
						ScrollType type = ScrollType.fromId(stack.getMetadata());
						if (type.accepts(packet.getAbility().getBendingType())) {
							
							activeSlot.putStack(ItemStack.field_190927_a);
							abilityData.addLevel();
							abilityData.setXp(0);
							
						}
						
					}
				}
				
			}
			
		}
		
		return null;
		
	}
	
	private IMessage handleInventory(PacketSBisonInventory packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		
		if (player.getRidingEntity() instanceof EntitySkyBison) {
			EntitySkyBison bison = (EntitySkyBison) player.getRidingEntity();
			if (bison.canPlayerViewInventory(player)) {
				player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_BISON_CHEST, player.worldObj,
						bison.getId(), 0, 0);
			}
		}
		
		return null;
	}
	
	private IMessage handleGetBending(PacketSOpenUnlockGui packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		BendingData data = AvatarPlayerData.fetcher().fetch(player);
		
		if (data.getAllBending().isEmpty()) {
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_GET_BENDING, player.worldObj, 0, 0, 0);
		}
		
		return null;
		
	}
	
	private IMessage handleUnlockBending(PacketSUnlockBending packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		BendingData data = AvatarPlayerData.fetcher().fetch(player);
		Container container = player.openContainer;
		
		if (container instanceof ContainerGetBending) {
			List<BendingType> eligible = ((ContainerGetBending) container).getEligibleTypes();
			
			BendingType desired = packet.getUnlockType();
			if (eligible.contains(desired)) {
				
				if (data.getAllBending().isEmpty()) {
					data.addBending(desired);
					
					for (int i = 0; i < ((ContainerGetBending) container).getSize(); i++) {
						container.getSlot(i).putStack(ItemStack.field_190927_a);
					}
					
					player.openGui(AvatarMod.instance, desired.id(), player.worldObj, 0, 0, 0);
					
				}
				
			}
			
		}
		
		return null;
	}
	
	private IMessage handleConfirmTransfer(PacketSConfirmTransfer packet, MessageContext ctx) {
		
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		TransferConfirmHandler.confirmTransfer(player);
		
		return null;
	}
	
	private IMessage handleCycleBending(PacketSCycleBending packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		BendingData data = Bender.getData(player);
		
		List<BendingController> controllers = data.getAllBending();
		if (controllers.size() > 1) {
			
			int index = controllers.indexOf(data.getActiveBending());
			index += packet.cycleRight() ? 1 : -1;
			
			if (index == -1) index = controllers.size() - 1;
			if (index == controllers.size()) index = 0;
			
			data.setActiveBending(controllers.get(index));
			
		}
		
		return null;
		
	}
	
	private static class ProcessAbilityRequest {
		
		private int ticks;
		private final EntityPlayer player;
		private final AvatarPlayerData data;
		private final BendingAbility ability;
		private final Raytrace.Result raytrace;
		
		public ProcessAbilityRequest(int ticks, EntityPlayer player, AvatarPlayerData data,
				BendingAbility ability, Raytrace.Result raytrace) {
			this.ticks = ticks;
			this.player = player;
			this.data = data;
			this.ability = ability;
			this.raytrace = raytrace;
		}
		
	}
	
}
