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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CrowsOfWar
 */
public class ConfigStats {

	public static final ConfigStats STATS_CONFIG = new ConfigStats();

	@Load
	public AttackSettings floatingBlockSettings = new AttackSettings(0.45f, 1),
			ravineSettings = new AttackSettings(7, 0.25), //
			waveSettings = new AttackSettings(6, 6), //
			airbladeSettings = new AttackSettings(4, .03), //
			fireArcSettings = new AttackSettings(4, 1);

	@Load
	public double wallWaitTime = 10, wallWaitTime2 = 60, wallMomentum = 10;

	@Load
	public int wallJumpDelay = 10;

	@Load
	public FireballSettings fireballSettings = new FireballSettings();

	@Load
	public ExplosionSettings explosionSettings = new ExplosionSettings();

	// @formatter:off
	@Load
	public float chiAirblade = 1f,
			chiAirGust = 0.5f,
			chiAirJump = 0.4f,
			chiAirBubble = 1.5f,
			chiAirBubbleTakeDamage = 0.25f,
			chiAirBubbleOneSecond = 0.125f,
			chiBuff = 2F,
			chiBuffLvl2 = 3f,
			chiBuffLvl3 = 3.5f,
			chiBuffLvl4 = 4f,
			chiBoulderRing = 3F,
			chiCloudburst = 2.5F,
			chiEarthspike = 5F,
			chiExplosion = 3F,
			chiExplosionUpgraded = 4.5f,
			chiExplosivePillar = 4F,
			chiRavine = 1.5f,
			chiRavineLvl4_1 = 2.25f,
			chiWall = 2.5f,
			chiPickUpBlock = 1.25f,
			chiMining = 2f,
			chiMiningMaster = 0.5f,
			chiFireArc = 1f,
			chiFireball = 2f,
			chiFlamethrowerSecond = 1.75f,
			chiFlamethrowerSecondLvl4_1 = 2.625f,
			chiFlamethrowerSecondLvl4_2 = 3.5f,
			chiLightFire = 2f,
			chiWave = 2f,
			chiWaterArc = 1f,
			chiWaterBubble = 1.25f,
			chiWaterSkateSecond = 0.5f,
			chiWallOneSecond = 0.125f,
			chiPrison = 5,
			chiSandPrison = 3,
			chiLightning = 6,
			chiIceShieldCreate = 4,
			chiIceShieldProtect = 0.15f,
			chiInfernoPunch = 3F,
			chiSmallInfernoPunch = 1F,
			chiLargeInfernoPunch = 6F,
			chiSandstorm = 3f,
			chiWaterCannon = 5f,
			chiFireJump = 2f;
	// @formatter:on

	@Load
	public float icePrisonDamage = 2;

	@Load
	public float sleepChiRegen = 99999;

	@Load
	public float InfernoPunchDamage = 2F;

	@Load
	public boolean allowAirBubbleElytra = false;

	@Load
	public double waterArcSearchRadius = 4, waterArcAngles = 8;

	@Load
	public double waterCannonSearchRadius = 3, waterCannonAngles = 8;

	@Load
	public boolean addDungeonLoot = true;

	@Load
	public boolean preventPickupBlockGriefing = false;

	@Load
	public float cleanseChiGroupBonus = 2f;

	@Load
	public boolean allowMultiAirbendingWalljump = false;

	@Load
	public List<String> sandBlocksNames = Arrays.asList(
			"minecraft:sand",
			"minecraft:gravel");
	@Load
	public List<String> bendableBlocksNames = Arrays.asList(
			"minecraft:stone",
			"minecraft:sand",
			"minecraft:sandstone",
			"minecraft:cobblestone",
			"minecraft:dirt",
			"minecraft:gravel",
			"minecraft:brick_block",
			"minecraft:mossy_cobblestone",
			"minecraft:stonebrick",
			"minecraft:clay",
			"minecraft:hardened_clay",
			"minecraft:stained_hardened_clay",
			"minecraft:coal_ore",
			"minecraft:iron_ore",
			"minecraft:emerald_ore",
			"minecraft:gold_ore",
			"minecraft:lapis_ore",
			"minecraft:redstone_ore",
			"minecraft:red_sandstone",
			"minecraft:grass",
			"minecraft:grass_path"
	);

	@Load
	public List<String> waterBendableBlockNames = Arrays.asList(
			"minecraft:snow",
			"minecraft:snow_layer",
			"minecraft:ice",
			"minecraft:packed_ice",
			"minecraft:frosted_ice",
			"minecraft:water",
			"minecraft:flowing_water"
	);

	@Load
	List<String> plantBendableBlockNames = Arrays.asList(
			"minecraft:tallgrass",
			"minecraft:wheat",
			"minecraft:double_grass",
			"minecraft:waterlily",
			"minecraft:red_flower",
			//For some reason, most of the plants in minecraft are tallgrass, double_grass, or red_flowers. Weird.
			"minecraft:leaves",
			"minecraft:yellow_flower",
			"minecraft:red_mushroom",
			"minecraft:brown_mushroom",
			"minecraft:vine"

	);

	public List<Block> plantBendableBlocks;
	public List<Block> waterBendableBlocks;
	public List<Block> bendableBlocks;
	public List<Block> sandBlocks;

	private ConfigStats() {
	}

	public static void load() {
		ConfigLoader.load(STATS_CONFIG, "avatar/stats.yml");
	}

	public void loadBlocks() {
		bendableBlocks = STATS_CONFIG.loadBlocksList(bendableBlocksNames);
		sandBlocks = STATS_CONFIG.loadBlocksList(sandBlocksNames);
		waterBendableBlocks = STATS_CONFIG.loadBlocksList(waterBendableBlockNames);
		plantBendableBlocks = STATS_CONFIG.loadBlocksList(plantBendableBlockNames);
	}

	/**
	 * Converts a list of block names to the Block instances.
	 */
	private List<Block> loadBlocksList(List<String> blocksNames) {
		List<Block> blocks = new ArrayList<>();

		for (String blockName : blocksNames) {
			Block b = Block.REGISTRY.getObject(new ResourceLocation(blockName));
			if (b == null) {
				AvatarLog.warn(WarningType.CONFIGURATION,
						"Invalid blocks entry: " + blockName + "; this block does not exist");
			} else {
				blocks.add(b);
			}
		}

		return blocks;

	}

	public static class AttackSettings {

		@Load
		public float damage;

		@Load
		public double push;

		public AttackSettings() {
		}

		private AttackSettings(float damage, double push) {
			this.damage = damage;
			this.push = push;
		}

	}

	public static class FireballSettings {

		@Load
		public float damage = 6;

		@Load
		public int fireTime = 6;

		@Load
		public float explosionSize = 1.5f;

		@Load
		public boolean damageBlocks = false;

		@Load
		public double push = .75;

	}

	public static class ExplosionSettings {
		@Load
		public float damage = 6;

		@Load
		public int fireTime = 2;

		@Load
		public float explosionSize = 1.0f;

		@Load
		public boolean damageBlocks = false;

		@Load
		public double push = 1;

	}

}
