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

package com.crowsofwar.gorecore.settings;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class GoreCoreModConfig extends GoreCoreConfig {
	
	public int MAX_UUID_CACHE_SIZE;
	
	public GoreCoreModConfig(FMLPreInitializationEvent event) {
		super(event);
	}
	
	@Override
	protected void loadValues(Configuration config) {
		MAX_UUID_CACHE_SIZE = config.getInt("Max UUID Cache Size", "misc", 200, 5, 100000,
				"The maximum amount of UUIDs that can be stored in the UUID cache file");
	}
	
}
