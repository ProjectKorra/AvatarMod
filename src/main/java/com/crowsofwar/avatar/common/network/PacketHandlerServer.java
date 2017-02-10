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
import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.network.packets.PacketSSkillsMenu;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseStatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketSWallJump;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleType;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
					par.ability.execute(new AbilityContext(par.data, par.raytrace));
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
		
		AvatarLog.warn("Unknown packet recieved: " + packet.getClass().getName());
		return null;
	}
	
	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	private IMessage handleKeypress(PacketSUseAbility packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data != null) {
			
			BendingAbility ability = packet.getAbility();
			if (data.hasBending(ability.getBendingType())) {
				if (data.getAbilityCooldown() == 0) {
					ability.execute(new AbilityContext(data, packet.getRaytrace()));
					data.setAbilityCooldown(15);
				} else {
					unprocessedAbilityRequests.add(new ProcessAbilityRequest(data.getAbilityCooldown(),
							player, data, ability, packet.getRaytrace()));
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
		
		if (data != null) data.getNetworker().sendAll();
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
				if (sc.execute(new AbilityContext(data, packet.getRaytrace()))) {
					
					data.removeStatusControl(packet.getStatusControl());
					data.sync();
					
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
				
				new NetworkParticleSpawner().spawnParticles(world, ParticleType.AIR, 4, 10,
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
		player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_SKILLS, player.worldObj, 0, 0, 0);
		
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
