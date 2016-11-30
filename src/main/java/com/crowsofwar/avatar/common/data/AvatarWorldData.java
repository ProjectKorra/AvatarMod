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

package com.crowsofwar.avatar.common.data;

import com.crowsofwar.gorecore.data.PlayerData;
import com.crowsofwar.gorecore.data.WorldDataPlayers;

import net.minecraft.world.World;

public class AvatarWorldData extends WorldDataPlayers<AvatarPlayerData> {
	
	public static final String WORLD_DATA_KEY = "Avatar";
	
	public AvatarWorldData() {
		super(WORLD_DATA_KEY);
		// TODO Auto-generated constructor stub
	}
	
	public AvatarWorldData(String key) {
		super(WORLD_DATA_KEY);
	}
	
	@Override
	public Class<? extends PlayerData> playerDataClass() {
		return AvatarPlayerData.class;
	}
	
	public static AvatarWorldData getDataFromWorld(World world) {
		return getDataForWorld(AvatarWorldData.class, WORLD_DATA_KEY, world, false);
	}
	
}
