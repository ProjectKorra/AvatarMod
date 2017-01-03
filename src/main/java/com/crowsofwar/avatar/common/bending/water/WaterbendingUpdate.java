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
package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.toRadians;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleType;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class WaterbendingUpdate {
	
	private final ParticleSpawner particles;
	
	private WaterbendingUpdate() {
		particles = new NetworkParticleSpawner();
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		EntityPlayer player = e.player;
		World world = player.worldObj;
		if (!world.isRemote) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			tryStartSkating(data, player);
			skate(data, player);
		}
	}
	
	private void tryStartSkating(AvatarPlayerData data, EntityPlayer player) {
		if (data.getSkateTime() > 0) {
			data.setSkateTime(data.getSkateTime() - 1);
			if (player.isInWater()) {
				data.setSkateTime(0);
				data.setSkating(true);
			}
		}
	}
	
	private void skate(AvatarPlayerData data, EntityPlayer player) {
		if (data.isSkating()) {
			
			int yPos = player.getPosition().getY();
			IBlockState below = player.worldObj.getBlockState(player.getPosition().down());
			
			if (player.isSneaking() || player.onGround || below.getBlock() != Blocks.WATER) {
				data.setSkating(false);
			} else {
				player.setPosition(player.posX, yPos + .2, player.posZ);
				Vector velocity = toRectangular(toRadians(player.rotationYaw), 0).dividedBy(2);
				player.motionX = velocity.x();
				player.motionY = 0;
				player.motionZ = velocity.z();
				
				if (player.ticksExisted % 3 == 0) {
					player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_SPLASH,
							SoundCategory.PLAYERS, 1, 1);
					particles.spawnParticles(player.worldObj, ParticleType.SPLASH, 2, 4,
							Vector.getEntityPos(player), new Vector(.2, .2, .2));
				}
				
				((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityTeleport(player));
				((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(player));
			}
			
		}
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new WaterbendingUpdate());
	}
	
}
