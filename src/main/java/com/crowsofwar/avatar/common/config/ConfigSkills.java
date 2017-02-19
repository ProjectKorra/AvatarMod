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

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigSkills {
	
	public static final ConfigSkills SKILLS_CONFIG = new ConfigSkills();
	
	private ConfigSkills() {}
	
	@Load
	public float blockPlaced = .9f, blockThrowHit = 1.5f, blockKill = 4.5f, airJump = 0.75f,
			airGustHit = .75f, ravineHit = .3f, waveHit = 1.2f, waterHit = 2f, fireHit = 2f,
			flamethrowerHit = 0.15f, fireballHit = 4.5f, airbladeHit = 1.2f, miningUse = 1f,
			waterSkateOneSecond = 1.2f, wallRaised = 1.5f, wallBlockedAttack = .45f, airbubbleProtect = 1.5f;
	
	public static void load() {
		ConfigLoader.load(SKILLS_CONFIG, "avatar/skills.yml");
	}
	
}
