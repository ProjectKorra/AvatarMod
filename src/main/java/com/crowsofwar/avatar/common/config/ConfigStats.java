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
	public AttackSettings floatingBlockSettings = new AttackSettings(2f, 1),
			ravineSettings = new AttackSettings(3.5F, 0.25), //
			waveSettings = new AttackSettings(0.25F, 2), //
			airbladeSettings = new AttackSettings(3.5F, 0.5), //
			fireBlastSettings = new AttackSettings(2.5F, 1),   //
			flamethrowerSettings = new AttackSettings(0.75F, 0.5),
			waterArcSettings = new AttackSettings(3, 1),
			boulderSettings = new AttackSettings(0.1F, 0.1),
			fireShotSetttings = new AttackSettings(1F, 0.5),
			lightningSpearSettings = new AttackSettings(4F, 2);

	@Load
	public double wallWaitTime = 10, wallMomentum = 10;

	@Load
	public int wallJumpDelay = 10;

	@Load
	public float waterArcTicks = 120;
	//Has to be a float so I can times it by a fraction; there aren't any partial ticks, though.

	@Load
	public AirBurstSettings airBurstSettings = new AirBurstSettings();

	@Load
	public CloudburstSettings cloudburstSettings = new CloudburstSettings();

	@Load
	public ExplosionSettings explosionSettings = new ExplosionSettings();

	@Load
	public EarthspikeSettings earthspikeSettings = new EarthspikeSettings();

	@Load
	public FlameStrikeSettings flameStrikeSettings = new FlameStrikeSettings();

	@Load
	public FireballSettings fireballSettings = new FireballSettings();

	@Load
	public LightningRazeSettings lightningRazeSettings = new LightningRazeSettings();

	@Load
	public PassiveSettings passiveSettings = new PassiveSettings();

	@Load
	public WaterCannonSettings waterCannonSettings = new WaterCannonSettings();

	// @formatter:off
	@Load
	public float chiAirblade = 1f,
			chiAirGust = 0.5f,
			chiAirJump = 0.4f,
			chiAirBubble = 1.5f,
			chiAirBurst = 4F,
			chiAirBubbleTakeDamage = 0.25f,
			chiAirBubbleOneSecond = 0.125f,
			chiBuff = 2F,
			chiBuffLvl2 = 3f,
			chiBuffLvl3 = 3.5f,
			chiBuffLvl4 = 4f,
			chiBoulderRing = 3F,
			chiCloudburst = 2.5F,
			chiEarthspike = 3.5F,
			chiExplosion = 3F,
			chiExplosionUpgraded = 4.5f,
			chiExplosivePillar = 4F,
			chiRavine = 1.5f,
			chiRavineLvl4_1 = 2.25f,
			chiWall = 2.5f,
			chiPushWall = 4f,
			chiPickUpBlock = 1.25f,
			chiMining = 2f,
			chiMiningMaster = 0.5f,
			chiFlameStrike = 2f,
			chiFireball = 2f,
			chiFlamethrowerSecond = 2.5f,
			chiFlamethrowerSecondLvl4_1 = 3.75f,
			chiFlamethrowerSecondLvl4_2 = 4.25f,
			chiFireShot = 2.5f,
			chiWave = 2f,
			chiWaterArc = 1f,
			chiWaterBubble = 1.25f,
			chiWaterSkateSecond = 1.5f,
			chiWallOneSecond = 0.125f,
			chiPrison = 5,
			chiSandPrison = 3,
			chiLightning = 6,
			chiLightningSpear = 4,
			chiLightningRaze = 5,
			chiIceShieldCreate = 4,
			chiIceShieldProtect = 0.15f,
			chiInfernoPunch = 3F,
			chiSmallInfernoPunch = 2F,
			chiLargeInfernoPunch = 6F,
			chiSandstorm = 3f,
			chiShockwave = 6F,
			chiWaterCannon = 5f,
			chiFireJump = 2f;
	// @formatter:on

	@Load
	public float icePrisonDamage = 2;

	@Load
	public float sleepChiRegen = 99999;

	@Load
	public boolean allowAirBubbleElytra = false;

	@Load
	public double waterArcSearchRadius = 4, waterArcAngles = 8;

	@Load
	public double waterBubbleSearchRadius = 5, waterBubbleAngles = 8;

	@Load
	public double cleanseSearchRadius = 5, cleanseAngles = 10;

	@Load
	public double fireSearchRadius = 4, fireAngles = 8;

	@Load
	public boolean shiftActivateFireDevour = true;

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
			"minecraft:stone_slab",
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
	public List<String> iceBendableBlockNames = Arrays.asList(
			"minecraft:snow",
			"minecraft:snow_layer",
			"minecraft:ice",
			"minecraft:packed_ice",
			"minecraft:frosted_ice"
	);

	@Load
	public List<String> plantBendableBlockNames = Arrays.asList(
			"minecraft:tallgrass",
			"minecraft:wheat",
			"minecraft:double_grass",
			"minecraft:waterlily",
			"minecraft:red_flower",
			"minecraft:double_plant",
			//For some reason, most of the plants in minecraft are tallgrass, double_grass, double_plant, or red_flowers. Weird.
			"minecraft:leaves",
			"minecraft:yellow_flower",
			"minecraft:red_mushroom",
			"minecraft:brown_mushroom",
			"minecraft:vine"

	);

	@Load
	public List<String> airBladeBreakableBlockNames = Arrays.asList(
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
			"minecraft:red_sandstone",
			"minecraft:grass",
			"minecraft:grass_path"

	);

	@Load
	public List<String> waterArcBreakableBlockNames = Arrays.asList(
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
			"minecraft:red_sandstone",
			"minecraft:grass",
			"minecraft:grass_path"
	);

	public List<Block> plantBendableBlocks;
	public List<Block> waterBendableBlocks;
	public List<Block> bendableBlocks;
	public List<Block> sandBlocks;
	public List<Block> airBladeBreakableBlocks;
	public List<Block> iceBendableBlocks;
	public List<Block> waterArcBreakableBlocks;

	private ConfigStats() {
	}

	public static void load() {
		ConfigLoader.load(STATS_CONFIG, "avatar/stats.yml");
	}

	public void loadBlocks() {
		bendableBlocks = STATS_CONFIG.loadBlocksList(bendableBlocksNames);
		airBladeBreakableBlocks = STATS_CONFIG.loadBlocksList(airBladeBreakableBlockNames);
		sandBlocks = STATS_CONFIG.loadBlocksList(sandBlocksNames);
		waterBendableBlocks = STATS_CONFIG.loadBlocksList(waterBendableBlockNames);
		plantBendableBlocks = STATS_CONFIG.loadBlocksList(plantBendableBlockNames);
		iceBendableBlocks = STATS_CONFIG.loadBlocksList(iceBendableBlockNames);
		waterArcBreakableBlocks = STATS_CONFIG.loadBlocksList(waterArcBreakableBlockNames);
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

	public static class FlameStrikeSettings {

		@Load
		public int strikeNumber = 2;

		@Load
		public float size = 1.75f;

		@Load
		public int chargeDuration = 40;

		@Load
		public float damage = 2;

		@Load
		public float knockback = 2;

		@Load
		public int performanceAmount = 6;

		@Load
		public int fireTime = 3;

		@Load
		public float maxDistance = 3;

	}


	public static class FireballSettings {

		@Load
		public float damage = 3.5F;

		@Load
		public int fireTime = 6;

		@Load
		public float explosionSize = 1f;

		@Load
		public boolean damageBlocks = false;

		@Load
		public double push = 1.25;

	}
	public static class AirBurstSettings {

		@Load
		public float damage = 4F;

		@Load
		public float push = 2;

		@Load
		public float radius = 4;

		@Load
		public float durationToFire = 40;
		//How long it takes to shoot the air burst, in ticks

		@Load
		public int performanceAmount = 12;
		//How much performance is added to the player's performance score upon a hit

		@Load
		public float beamDamage = 6F;

		@Load
		public float beamPush = 20F;

		@Load
		public float beamSize = 1F;

		@Load
		public int beamRange = 10;

	}

	public static class CloudburstSettings {

		@Load
		public double damage = 3;

		@Load
		public double push = 1.5;

	}

	public static class EarthspikeSettings {

		@Load
		public double damage = 3;

		@Load
		public float frequency = 4;

		@Load
		public float size = 1;

		@Load
		public double push = 0.5;

	}

	public static class WaterCannonSettings {

		@Load
		public boolean useWaterCannonParticles = true;

		@Load
		public double waterCannonSearchRadius = 3;

		@Load
		public double waterCannonAngles = 8;

		@Load
		public double waterCannonDamage = 1;

		@Load
		public double waterCannonKnockbackMult = 1;
	}

	public static class LightningRazeSettings {

		@Load
		public float accuracy = 1;
		//0 is the most accurate; the bigger the number, the worse the accuracy

		@Load
		public float frequency = 5;
		//How many ticks pass per lightning bolt spawned- 20 ticks per second.
		//In this case, the lightning bolts spawn 4 times per second.

		@Load
		public int bolts = 1;
		//The number of  bolts the lightning spawner spawns per amount of ticks
		//Ticks determined by frequency

		@Load
		public int ticks = 20;
		//How long the spawner stays alive- in this case, one second.

		@Load
		public float damage = 2;
		//The amount of damage dealt per lightning bolt.

		@Load
		public double speed = 5;
		//Speed of the spawner



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

	public static class PassiveSettings {
		@Load
		public boolean waterBreathing = true;

		@Load
		public boolean hasteInWater = true;

		@Load
		public boolean fireResistance = true;

		@Load
		public boolean noFallDamageAir = true;

		@Load
		public boolean knockbackResistanceEarth = true;

	}

}
