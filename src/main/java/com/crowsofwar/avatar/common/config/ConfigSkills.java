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
	
	// @formatter:off
	@Load
	public float blockPlaced = 5f,
		blockThrowHit = 6.5f,
		blockKill = 4f,
		airJump = 2f,
		airGustHit = 4f,
		ravineHit = 3f,
		waveHit = 4f,
		waterHit = 3f,
		fireHit = 3f,
		flamethrowerHit = 0.75f,
		fireballHit = 4.5f,
		cloudburstHit = 5.0f,
		lightningspearHit = 6.0F,
		airbladeHit = 3f,
		miningUse = 10f,
		miningBreakOre = 5f,
		waterSkateOneSecond = 1.5f,
		wallRaised = 1f,
		wallBlockedAttack = 3f,
		airbubbleProtect = 6f,
		litFire = 20,
		createBubble = 15,
		madeLightning = 3,
		struckWithLightning = 5;
	// @formatter:on
	
	public static void load() {
		ConfigLoader.load(SKILLS_CONFIG, "avatar/skills.yml");
	}
	
}
