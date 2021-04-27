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
public class ConfigSkills {

	public static final ConfigSkills SKILLS_CONFIG = new ConfigSkills();
	// @formatter:off

	@Load
	public boolean startWithRandomBending = true;

	@Load
	public boolean multipleElements = true;

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

	@Load
	public AbilitySettings abilitySettings = new AbilitySettings();



	public static class AbilitySettings {

		@Load
		public boolean overrideAbilities = true;

		@Load
		public boolean generateAbilities = true;

		//Don't disable both of these unless you don't want to play the game lol
		@Load
		public boolean useRadialMouse = true;

		@Load
		public boolean useRadialNumbers = true;

		@Load
		public boolean infiniteScaling = false;

		@Load
		public float maxScaleLevel = 1F;

		//Adds a power level config option. Divides inhibitors by and multiplies effects by this value.
		@Load
		public float powerLevel = 1;

		@Load
		public float damageMult = 1;

		@Load
		public float speedMult  = 1;

		@Load
		public float chiMult = 1;

		@Load
		public float chiHitMult = 1;

		@Load
		public float cooldownMult = 1;

		@Load
		public float exhaustionMult = 1;

		@Load
		public float burnoutMult = 1;

		@Load
		public float burnoutRecoverMult = 1;
	}

	private ConfigSkills() {
	}
	// @formatter:on

	public static void load() {
		ConfigLoader.load(SKILLS_CONFIG, "avatar/skills.yml");
	}

}
