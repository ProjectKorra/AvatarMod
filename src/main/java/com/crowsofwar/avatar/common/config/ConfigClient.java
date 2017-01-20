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
package com.crowsofwar.avatar.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigClient {
	
	public static ConfigClient CLIENT_CONFIG = new ConfigClient();
	
	@Load
	public float radialMenuAlpha = .75f;
	
	@Load
	private Map<String, Integer> nameKeymappings = new HashMap<>();
	
	public Map<BendingAbility, Integer> keymappings = new HashMap<>();
	
	public static void load() {
		ConfigLoader.load(CLIENT_CONFIG, "avatar/cosmetic.yml");
		
		CLIENT_CONFIG.keymappings.clear();
		Set<Map.Entry<String, Integer>> entries = CLIENT_CONFIG.nameKeymappings.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			BendingAbility ability = null;
			for (BendingAbility a : BendingManager.allAbilities()) {
				if (a.getName().equals(entry.getKey())) {
					ability = a;
					break;
				}
			}
			if (ability != null) {
				CLIENT_CONFIG.keymappings.put(ability, entry.getValue());
			}
		}
		
	}
	
}
