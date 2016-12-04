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

package com.crowsofwar.gorecore.proxy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;

public class GoreCoreClientProxy extends GoreCoreCommonProxy {
	
	@Override
	protected File createUUIDCacheFile() {
		return new File(Minecraft.getMinecraft().mcDataDir, "GoreCore_ClientUUIDCache.txt");
	}
	
	@Override
	protected File createMinecraftDir() {
		return new File(Minecraft.getMinecraft().mcDataDir, ".");
	}
	
	@Override
	public boolean isPlayerWalking(EntityPlayer player) {
		if (player == Minecraft.getMinecraft().thePlayer) {
			GameSettings gs = Minecraft.getMinecraft().gameSettings;
			return gs.keyBindForward.isKeyDown() || gs.keyBindBack.isKeyDown() || gs.keyBindLeft.isKeyDown()
					|| gs.keyBindRight.isKeyDown();
		}
		
		return false;
	}
	
	@Override
	public void sideSpecifics() {}
	
	@Override
	public String translate(String key, Object... args) {
		return I18n.format(key, args);
	}
	
	@Override
	public EntityPlayer getClientSidePlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
}
