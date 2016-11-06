package com.crowsofwar.avatar.common.config;

import static net.minecraft.init.Blocks.*;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarConfig {
	
	public static final AvatarConfig CONFIG = new AvatarConfig();
	
	@Load
	public AttackSettings floatingBlockSettings = new AttackSettings(0.25f, 1),
			ravineSettings = new AttackSettings(7, 0.25), //
			waveSettings = new AttackSettings(9, 6);
	
	@Load
	public List<String> bendableBlocksNames;
	
	public List<Block> bendableBlocks;
	
	private AvatarConfig() {
		bendableBlocksNames = new ArrayList<String>();
		bendableBlocksNames.add(Blocks.STONE.toString());
		addBendableBlock(STONE, SAND, SANDSTONE, COBBLESTONE, DIRT, GRAVEL, BRICK_BLOCK, MOSSY_COBBLESTONE,
				STONEBRICK, CLAY, HARDENED_CLAY, STAINED_HARDENED_CLAY, COAL_ORE, IRON_ORE, EMERALD_ORE,
				GOLD_ORE, LAPIS_ORE, REDSTONE_ORE, RED_SANDSTONE);
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
		ConfigLoader.load(CONFIG, "avatar/balance.cfg");
		CONFIG.loadBendableBlocks();
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
	
}
