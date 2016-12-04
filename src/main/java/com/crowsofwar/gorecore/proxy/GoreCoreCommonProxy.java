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

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public class GoreCoreCommonProxy {
	
	private File uuidCacheFile;
	private File minecraftDirectory;
	
	public GoreCoreCommonProxy() {
		uuidCacheFile = createUUIDCacheFile();
		minecraftDirectory = createMinecraftDir();
	}
	
	public final File getUUIDCacheFile() {
		return uuidCacheFile;
	}
	
	protected File createUUIDCacheFile() {
		return new File("GoreCore_ServerUUIDCache.txt");
	}
	
	public final File getMinecraftDir() {
		return minecraftDirectory;
	}
	
	protected File createMinecraftDir() {
		return new File(".");
	}
	
	/**
	 * Returns whether that person is currently walking. This only works for the
	 * person who is playing Minecraft.
	 */
	public boolean isPlayerWalking(EntityPlayer player) {
		return false;
	}
	
	public void sideSpecifics() {
		
	}
	
	public String translate(String key, Object... args) {
		// TODO [1.10] find out way to translate server side
		return I18n.format(key, args);
	}
	
	public EntityPlayer getClientSidePlayer() {
		return null;
	}
	
}
