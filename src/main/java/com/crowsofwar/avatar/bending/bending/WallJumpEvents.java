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
package com.crowsofwar.avatar.bending.bending;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.MiscData;
import com.crowsofwar.avatar.network.packets.PacketSWallJump;
import com.crowsofwar.gorecore.GoreCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class WallJumpEvents {

	private static void tick(EntityPlayer player, World world, BendingData data) {
		MiscData miscData = data.getMiscData();
		Bender bender = Bender.get(player);
	//	GameSettings settings = Minecraft.getMinecraft().gameSettings;
		if (player == GoreCore.proxy.getClientSidePlayer() && bender.getWallJumpManager()
				.canWallJump()) {
			if (AvatarControl.CONTROL_JUMP.isPressed()) {
				AvatarMod.network.sendToServer(new PacketSWallJump());
			}
		}
		if (player.onGround) {
			miscData.setWallJumping(false);
			miscData.setTimeInAir(0);
		} else {
			miscData.setTimeInAir(miscData.getTimeInAir() + 1);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent e) {
		EntityPlayer player = e.player;
		World world = player.world;
		BendingData data = BendingData.get(player);
		tick(player, world, data);
	}

}
