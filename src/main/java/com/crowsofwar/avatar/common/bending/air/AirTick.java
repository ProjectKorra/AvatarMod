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

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_SPACE_DOWN;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSWallJump;
import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AirTick {
	
	private AirTick() {}
	
	private void tick(EntityPlayer player, World world, AvatarPlayerData data) {
		if (player == GoreCore.proxy.getClientSidePlayer() && player.isCollidedHorizontally
				&& !player.isCollidedVertically && data.getTimeInAir() > 10) {
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
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AirTick());
	}
	
}
