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
package com.crowsofwar.avatar.config;

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

/**
 * @author CrowsOfWar
 */
public class ConfigChi {

	public static final ConfigChi CHI_CONFIG = new ConfigChi();
	@Load
	public float regenPerSecond = 0.25f, regenInCombat = 0.5F, availablePerSecond = 1.5f,
			maxAvailableChi = 12f, regenInBed = 2f, regenInWater = 1f, regenOnEarth = 1f;
	@Load
	public float bonusLearnedBending = 16, bonusAbility = 6, bonusAbilityLevel = 3;
	@Load
	public float maxChiCap = 100;
	@Load
	public boolean infiniteInCreative = true;
	@Load
	public boolean lowChiDebuffs = false;

	private ConfigChi() {
	}

	public static void load() {
		ConfigLoader.load(CHI_CONFIG, "avatar/chi.yml");
	}

}
