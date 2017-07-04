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

import com.crowsofwar.avatar.common.bending.Ability;
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
	public float radialMenuAlpha = 0.75f;
	
	@Load
	public float chiBarAlpha = 0.5f;
	
	@Load
	public float bendingCycleAlpha = 0.5f;
	
	@Load
	public boolean useCustomParticles = true;
	
	@Load
	private Map<String, Integer> nameKeymappings = new HashMap<>();
	public Map<Ability, Integer> keymappings = new HashMap<>();
	
	@Load
	private Map<String, Boolean> nameConflicts = new HashMap<>();
	public Map<Ability, Boolean> conflicts = new HashMap<>();
	
	public static void load() {
		ConfigLoader.load(CLIENT_CONFIG, "avatar/cosmetic.yml");
		
		CLIENT_CONFIG.keymappings.clear();
		Set<Map.Entry<String, Integer>> entries = CLIENT_CONFIG.nameKeymappings.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			Ability ability = null;
			for (Ability a : BendingManager.allAbilities()) {
				if (a.getName().equals(entry.getKey())) {
					ability = a;
					break;
				}
			}
			if (ability != null) {
				CLIENT_CONFIG.keymappings.put(ability, entry.getValue());
			}
		}
		CLIENT_CONFIG.conflicts.clear();
		Set<Map.Entry<String, Boolean>> entries2 = CLIENT_CONFIG.nameConflicts.entrySet();
		for (Map.Entry<String, Boolean> entry : entries2) {
			Ability ability = null;
			for (Ability a : BendingManager.allAbilities()) {
				if (a.getName().equals(entry.getKey())) {
					ability = a;
					break;
				}
			}
			if (ability != null) {
				CLIENT_CONFIG.conflicts.put(ability, entry.getValue());
			}
		}
		
	}
	
	public static void save() {
		
		CLIENT_CONFIG.nameKeymappings.clear();
		Set<Map.Entry<Ability, Integer>> entries = CLIENT_CONFIG.keymappings.entrySet();
		for (Map.Entry<Ability, Integer> entry : entries) {
			CLIENT_CONFIG.nameKeymappings.put(entry.getKey().getName(), entry.getValue());
		}
		CLIENT_CONFIG.nameConflicts.clear();
		Set<Map.Entry<Ability, Boolean>> entries2 = CLIENT_CONFIG.conflicts.entrySet();
		for (Map.Entry<Ability, Boolean> entry : entries2) {
			CLIENT_CONFIG.nameConflicts.put(entry.getKey().getName(), entry.getValue());
		}
		
		ConfigLoader.save(CLIENT_CONFIG, "avatar/cosmetic.yml");
	}
	
}
