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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.TransferConfirmHandler;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.gui.ContainerGetBending;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.avatar.common.network.packets.*;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * Implements IPacketHandler. Acts as a packet handler for integrated and
 * dedicated servers. Is a singleton and is accessible via {@link #instance}.
 *
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

		EntityPlayerMP player = ctx.getServerHandler().player;
		Bender bender = Bender.get(player);
		if (bender != null) {
			bender.executeAbility(packet.getAbility(), packet.getRaytrace());
		}

		return null;
	}
	
	private IMessage handleRequestData(PacketSRequestData packet, MessageContext ctx) {
		
		UUID id = packet.getAskedPlayer();
		EntityPlayer player = AccountUUIDs.findEntityFromUUID(ctx.getServerHandler().player.world,
				id);
		
		if (player == null) {
			
			AvatarLog.warnHacking(ctx.getServerHandler().player.getName(),
					"Sent request data for a player with account '" + id
							+ "', but that player is not in the world.");
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
		World world = player.world;
		
		BendingData data = BendingData.get(player);
		if (data.hasBendingId(Airbending.ID) && !data.getMiscData().isWallJumping()
				&& data.getMiscData().getTimeInAir() >= STATS_CONFIG.wallJumpDelay) {
			
			data.getMiscData().setWallJumping(true);
			
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
				Vector n = velocity.reflect(normal).times(4).minus(normal.times(0.5)).withY(0.5);
				n = n.plus(Vector.getLookRectangular(player).times(.8));
				
				if (n.sqrMagnitude() > 1) {
					n = n.normalize().times(1);
				}

				// can't use setVelocity since that is Client SideOnly
				player.motionX = n.x();
				player.motionY = n.y();
				player.motionZ = n.z();
				player.connection.sendPacket(new SPacketEntityVelocity(player));
				
				new NetworkParticleSpawner().spawnParticles(world, AvatarParticles.getParticleAir(), 4, 10,
						new Vector(player).plus(n), n.times(3));
				world.playSound(null, new BlockPos(player), block.getSoundType().getBreakSound(),
						SoundCategory.PLAYERS, 1, 0.6f);
				
				data.getMiscData().setFallAbsorption(3);
				
			}
			
		}
		
		return null;
	}
	
	private IMessage handleSkillsMenu(PacketSSkillsMenu packet, MessageContext ctx) {
		
		EntityPlayerMP player = ctx.getServerHandler().player;
		BendingData data = BendingData.get(player);
		UUID element = packet.getElement();

		System.out.println(element);

		if (BendingStyles.has(element)) {
			if (data.hasBendingId(element)) {
				int guiId = AvatarGuiHandler.getGuiId(element);
				player.openGui(AvatarMod.instance, guiId, player.world, 0, 0, 0);
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
