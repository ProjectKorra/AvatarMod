package com.crowsofwar.avatar.common.bending.air;

import static com.crowsofwar.avatar.common.particle.AvatarParticleType.AIR;

import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
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
			
			EntityPlayer player = e.player;
			
			particles.spawnParticles(player.worldObj, AIR, 1, 1, Vector.getEntityPos(player),
					new Vector(0.7, 0.2, 0.7));
			
			if (e.player.motionY < 0) {
				System.out.println("Remove particle spawner");
				MinecraftForge.EVENT_BUS.unregister(this);
			}
			
		}
	}
	
	public static void spawnParticles(EntityPlayer player) {
		new AirJumpParticleSpawner(player);
	}
	
}
