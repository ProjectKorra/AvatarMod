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

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_AIR_BUBBLE;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_SPACE_DOWN;

import java.util.List;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import com.crowsofwar.avatar.common.network.packets.PacketSWallJump;
import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AirbendingEvents {
	
	private AirbendingEvents() {}
	
	private void tick(EntityPlayer player, World world, AvatarPlayerData data) {
		if (player == GoreCore.proxy.getClientSidePlayer() && player.isCollidedHorizontally
				&& !player.isCollidedVertically && data.getTimeInAir() >= STATS_CONFIG.wallJumpDelay) {
			if (AvatarMod.proxy.getKeyHandler().isControlPressed(CONTROL_SPACE_DOWN)) {
				AvatarMod.network.sendToServer(new PacketSWallJump());
			}
		}
		if (player.onGround) {
			data.setWallJumping(false);
			data.setTimeInAir(0);
		} else {
			data.setTimeInAir(data.getTimeInAir() + 1);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		EntityPlayer player = e.player;
		World world = player.worldObj;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data.hasBending(BendingType.AIRBENDING)) {
			tick(player, world, data);
		}
	}
	
	@SubscribeEvent
	public void airBubbleShield(LivingAttackEvent e) {
		World world = e.getEntity().worldObj;
		if (!world.isRemote && e.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntity();
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			if (data.hasStatusControl(StatusControl.BUBBLE_CONTRACT)) {
				
				List<EntityAirBubble> entities = player.worldObj.getEntitiesWithinAABB(EntityAirBubble.class,
						player.getEntityBoundingBox(), bubble -> bubble.getOwner() == player);
				for (EntityAirBubble bubble : entities) {
					
					DamageSource source = e.getSource();
					Entity entity = source.getEntity();
					if (entity != null && (entity instanceof AvatarEntity || entity instanceof EntityArrow)) {
						entity.setDead();
						data.getAbilityData(ABILITY_AIR_BUBBLE).addXp(SKILLS_CONFIG.airbubbleProtect);
						bubble.setHealth(bubble.getHealth() - e.getAmount());
						e.setCanceled(true);
					}
					
				}
				
			}
		}
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AirbendingEvents());
	}
	
}
