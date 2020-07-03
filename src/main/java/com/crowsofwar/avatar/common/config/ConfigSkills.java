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
 * @author CrowsOfWar
 */
public class ConfigSkills {

	public static final ConfigSkills SKILLS_CONFIG = new ConfigSkills();
	// @formatter:off

	@Load
	public boolean startWithRandomBending = true;

	@Load
	public boolean MultipleElements = true;

	@Load
	public boolean setKeepInventory = true;

	@Load
	public float blockPlaced = 5f,
			airBurstHit = 4F,
			airBladeHit = 3f,
			airGustHit = 3f,
			airJump = 2f,
			blockThrowHit = 6.5f,
			blockKill = 4f,
			buffUsed = 4f,
			earthspikeHit = 3.0F,
			ravineHit = 3f,
			waveHit = 4f,
			waterHit = 3f,
			fireballHit = 4.5f,
			fireShotHit = 3F,
			flamethrowerHit = 0.75f,
			flameStrikeHit = 3,
			cloudburstHit = 5.0f,
			lightningspearHit = 6.0F,
			miningUse = 2.5f,
			miningBreakOre = 3.5f,
			waterSkateOneSecond = 1.5f,
			wallRaised = 1f,
			wallReach = 2,
			wallBlockedAttack = 3f,
			airbubbleProtect = 6f,
			createBubble = 15,
			madeLightning = 3,
			struckWithLightning = 5,
			sandPrisoned = 9,
			iceShieldCreated = 2,
			iceShieldProtected = 6,
			icePrisoned = 10,
			sandstormPickedUp = 5,
			fireJump = 5;

	private ConfigSkills() {
	}
	// @formatter:on

	public static void load() {
		ConfigLoader.load(SKILLS_CONFIG, "avatar/skills.yml");
	}

}
