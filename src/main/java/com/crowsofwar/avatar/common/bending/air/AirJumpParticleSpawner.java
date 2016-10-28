package com.crowsofwar.avatar.common.bending.air;

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
			
			e.setDamageMultiplier(e.getDamageMultiplier() / 3);
			if (e.getDamageMultiplier() <= 0.5f) e.setCanceled(true);
			
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
