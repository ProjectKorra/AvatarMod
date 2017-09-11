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
import java.util.List;

import static net.minecraft.init.Blocks.*;

/**
 * 
 * 
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
	public List<String> bendableBlocksNames;
	
	@Load
	public double wallWaitTime = 10, wallWaitTime2 = 60, wallMomentum = 10;
	
	@Load
	public int wallJumpDelay = 10;
	
	@Load
	public FireballSettings fireballSettings = new FireballSettings();
	
	// @formatter:off
	@Load
	public float chiAirblade = 1f,
			chiAirGust = 0.5f,
			chiAirJump = 0.4f,
			chiAirBubble = 1.5f,
			chiAirBubbleTakeDamage = 0.25f,
			chiAirBubbleOneSecond = 0.125f,
			chiRavine = 1.5f,
			chiWall = 2.5f,
			chiPickUpBlock = 1.25f,
			chiMining = 2f,
			chiMiningMaster = 0.5f,
			chiFireArc = 1f,
			chiFireball = 2f,
			chiFlamethrowerSecond = 1.75f,
			chiLightFire = 2f,
			chiWave = 2f,
			chiWaterArc = 1f,
			chiWaterBubble = 1.25f,
			chiWaterSkateSecond = 0.5f,
			chiWallOneSecond = 0.125f,
			chiPrison = 5,
			chiSandPrison = 3,
			chiLightning = 6;
	// @formatter:on
	
	@Load
	public float sleepChiRegen = 99999;
	
	@Load
	public boolean allowAirBubbleElytra = false;
	
	@Load
	public double waterArcSearchRadius = 4, waterArcAngles = 8;
	
	@Load
	public boolean addDungeonLoot = true;
	
	@Load
	public boolean preventPickupBlockGriefing = false;
	
	public List<Block> bendableBlocks;
	
	private ConfigStats() {
		bendableBlocksNames = new ArrayList<>();
		addBendableBlock(STONE, SAND, SANDSTONE, COBBLESTONE, DIRT, GRAVEL, BRICK_BLOCK, MOSSY_COBBLESTONE,
				STONEBRICK, CLAY, HARDENED_CLAY, STAINED_HARDENED_CLAY, COAL_ORE, IRON_ORE, EMERALD_ORE,
				GOLD_ORE, LAPIS_ORE, REDSTONE_ORE, RED_SANDSTONE, GRASS, GRASS_PATH);
		
	}
	
	private void addBendableBlock(Block... blocks) {
		for (Block block : blocks)
			bendableBlocksNames.add(Block.REGISTRY.getNameForObject(block).toString());
	}
	
	private void loadBendableBlocks() {
		bendableBlocks = new ArrayList<>();
		
		for (String blockName : bendableBlocksNames) {
			Block b = Block.REGISTRY.getObject(new ResourceLocation(blockName));
			if (b == null) {
				AvatarLog.warn(WarningType.CONFIGURATION,
						"Invalid bendable blocks entry: " + blockName + "; does not exist");
			} else {
				bendableBlocks.add(b);
			}
		}
		
	}
	
	public static void load() {
		ConfigLoader.load(STATS_CONFIG, "avatar/stats.yml");
	}
	
	public void loadBlocks() {
		STATS_CONFIG.loadBendableBlocks();
	}
	
	public static class AttackSettings {
		
		@Load
		public float damage;
		
		@Load
		public double push;
		
		public AttackSettings() {}
		
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
	
}
