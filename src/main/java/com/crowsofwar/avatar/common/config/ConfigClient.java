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

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author CrowsOfWar
 */
public class ConfigClient {

	public static ConfigClient CLIENT_CONFIG = new ConfigClient();

	@Load
	public float radialMenuAlpha = 0.75f;

	@Load
	public float chiBarAlpha = 0.5f;

	@Load
	public final float bendingCycleAlpha = 0.75f;

	@Load
	public final boolean displayGetBendingMessage = true;
	//For some reason if it's not final it won't work
	//Controls whether or not to show the get bending message
	//when you press the use bending key


	@Load
	public boolean useCustomParticles = true;
	public Map<Ability, Integer> keymappings = new HashMap<>();
	public Map<Ability, Boolean> conflicts = new HashMap<>();
	@Load
	private Map<String, Integer> nameKeymappings = new HashMap<>();
	@Load
	private Map<String, Boolean> nameConflicts = new HashMap<>();

	@Load
	public ShaderSettings shaderSettings = new ShaderSettings();

	@Load
	public ActiveBendingSettings activeBendingSettings = new ActiveBendingSettings();

	@Load
	public ChiBarSettings chiBarSettings = new ChiBarSettings();

	@Load
	public FireballRenderSettings fireballRenderSettings = new FireballRenderSettings();

	public static void load() {
		ConfigLoader.load(CLIENT_CONFIG, "avatar/cosmetic.yml");

		CLIENT_CONFIG.keymappings.clear();
		Set<Map.Entry<String, Integer>> entries = CLIENT_CONFIG.nameKeymappings.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			Ability ability = null;
			for (Ability a : Abilities.all()) {
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
			for (Ability a : Abilities.all()) {
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

	public static class ShaderSettings {

		@Load
		public boolean useSlipstreamShaders = false;

		@Load
		public boolean useCleanseShaders = false;

		@Load
		public boolean useRestoreShaders = false;

		@Load
		public boolean usePurifyShaders = false;
	}

	public static class ActiveBendingSettings {

		@Load
		public final boolean shouldBendingMenuRender = true;
		//For some reason if it's not final it won't work
		//Determines if element menu should render at all

		@Load
		public final boolean shouldBendingMenuDisappear = false;
		//For some reason if it's not final it won't work
		//Makes the menu disappear after the duration

		@Load
		public final int bendingMenuDuration = 200;
		//If the menu should disappear, how long it should take before disappearing


	}

	public static class ChiBarSettings {
		@Load
		public final boolean shouldChibarRender = true;

		@Load
		public final boolean shouldChiNumbersRender = true;

		@Load
		public final boolean shouldChiMenuDisappear = false;

		@Load
		public final int chibarDuration = 200;

	}

	public static class FireballRenderSettings {
		@Load
		public final boolean isSphere = false;
	}

}
