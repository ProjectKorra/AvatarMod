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

package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.particle.AvatarParticleType;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Spawns particles after a player air-jumps. Used on the server thread.
 * 
 * @author CrowsOfWar
 */
public class AirJumpParticleSpawner {
	
	private final EntityPlayer target;
	private final ParticleSpawner particles;
	
	private AirJumpParticleSpawner(EntityPlayer target) {
		this.target = target;
		this.particles = new NetworkParticleSpawner();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent e) {
		if (e.side == Side.SERVER && e.player == target) {
			
			Vector pos = Vector.getEntityPos(target);
			pos.setY(pos.y() + 1.3);
			
			particles.spawnParticles(target.worldObj, AvatarParticleType.AIR, 1, 1, pos,
					new Vector(0.7, 0.2, 0.7));
			
			if (e.player.isInWater()) {
				MinecraftForge.EVENT_BUS.unregister(this);
			}
			if (e.player.capabilities.isCreativeMode && e.player.onGround) {
				MinecraftForge.EVENT_BUS.unregister(this);
			}
			
		}
	}
	
	// Note: not fired on survival mode
	@SubscribeEvent
	public void onFall(LivingFallEvent e) {
		if (e.getEntity() == target && !e.getEntity().worldObj.isRemote) {
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(target);
			float xp = data.getAbilityData(BendingAbility.ABILITY_AIR_JUMP).getXp();
			
			// Find approximate maximum distance. In actuality, a bit less, due
			// to max velocity and drag
			// Using kinematic equation, gravity for players is 32 m/s
			float maxDist;
			{
				float h = (5 + xp / 50) / 8f;
				float v = 20 * (1 + xp / 250f);
				maxDist = v * h - 16 * h * h;
			}
			maxDist -= 2; // compensate that it may be a bit extra
			
			e.setDistance(e.getDistance() - maxDist);
			if (e.getDistance() < 0) e.setDistance(0);
			System.out.println("Distance is now " + e.getDistance());
			
			MinecraftForge.EVENT_BUS.unregister(this);
			
		}
	}
	
	/**
	 * Creates a new particle spawner for the given player. It then is
	 * registered to the event bus to receive events. The particle spawner
	 * automatically unsubscribes itself when it detects that the player hit the
	 * ground.
	 * 
	 * @param player
	 *            Player to spawn particles for
	 */
	public static void spawnParticles(EntityPlayer player) {
		new AirJumpParticleSpawner(player);
	}
	
}
