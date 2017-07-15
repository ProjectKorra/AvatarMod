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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import com.crowsofwar.avatar.common.network.packets.PacketSWallJump;
import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
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
			if (AvatarControl.CONTROL_JUMP.isPressed()) {
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
		World world = player.world;
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
		if (data.hasBending(Airbending.ID)) {
			tick(player, world, data);
		}
	}
	
	@SubscribeEvent
	public void airBubbleShield(LivingAttackEvent e) {
		World world = e.getEntity().world;
		
		EntityLivingBase attacked = (EntityLivingBase) e.getEntity();
		
		if (Bender.isBenderSupported(attacked)) {
			BendingData data = Bender.create(attacked).getData();
			if (data.hasStatusControl(StatusControl.BUBBLE_CONTRACT)) {
				EntityAirBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityAirBubble.class,
						attacked);
				if (bubble != null) {
					if (bubble.attackEntityFrom(e.getSource(), e.getAmount())) {
						e.setCanceled(true);
						world.playSound(null, attacked.getPosition(), SoundEvents.BLOCK_CLOTH_HIT,
								SoundCategory.PLAYERS, 1, 1);
					}
				}
			}
		}
		
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AirbendingEvents());
	}
	
}
