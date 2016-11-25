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

package com.crowsofwar.avatar.common;

import java.util.Set;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;

/**
 * Allows calling of side-specific code by using a common base class and side-specific subclasses.
 * It can be referenced via {@link AvatarMod#proxy}. All classes or values accessed from here are
 * safe to use on either side. <br />
 * <br />
 * Is using Client proxy if running from a minecraft client. Uses server proxy is running from
 * server. <br />
 * <br />
 *
 */
public interface AvatarCommonProxy {
	
	/**
	 * Called from the main class, subclasses should initialize themselves here (fields, etc).
	 */
	public void preInit();
	
	public IControlsHandler getKeyHandler();
	
	/**
	 * Get a client-side packet handler safely. When the machine is running a minecraft client (even
	 * if in the integrated server thread), returns the packet handler for the client. Otherwise
	 * (this only happens on dedicated servers), returns null.
	 */
	public IPacketHandler getClientPacketHandler();
	
	/**
	 * Get client player's reach. Returns 0 on server.
	 */
	double getPlayerReach();
	
	/**
	 * Called during the FMLInitialization event
	 */
	void init();
	
	AvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z);
	
	PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher();
	
	/**
	 * Get client-side IThreadListener, null on server
	 * 
	 * @return
	 */
	IThreadListener getClientThreadListener();
	
	/**
	 * Get amount of particles. 0 = All, 1 = decreased, 2 = minimal
	 */
	int getParticleAmount();
	
	void addStatusControl(StatusControl control);
	
	void removeStatusControl(StatusControl control);
	
	Set<StatusControl> getAllStatusControls();
	
}
