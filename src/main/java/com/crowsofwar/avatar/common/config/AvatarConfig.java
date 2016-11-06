package com.crowsofwar.avatar.common.config;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

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
	public List<Block> bendableBlocks;
	
	private AvatarConfig() {
		bendableBlocks = new ArrayList<Block>();
		bendableBlocks.add(Blocks.STONE);
		bendableBlocks.add(Blocks.SAND);
		bendableBlocks.add(Blocks.SANDSTONE);
		bendableBlocks.add(Blocks.COBBLESTONE);
		bendableBlocks.add(Blocks.DIRT);
		bendableBlocks.add(Blocks.GRAVEL);
		bendableBlocks.add(Blocks.BRICK_BLOCK);
		bendableBlocks.add(Blocks.MOSSY_COBBLESTONE);
		bendableBlocks.add(Blocks.STONEBRICK);
		bendableBlocks.add(Blocks.CLAY);
		bendableBlocks.add(Blocks.HARDENED_CLAY);
		bendableBlocks.add(Blocks.STAINED_HARDENED_CLAY);
		bendableBlocks.add(Blocks.COAL_ORE);
		bendableBlocks.add(Blocks.IRON_ORE);
		bendableBlocks.add(Blocks.EMERALD_ORE);
		bendableBlocks.add(Blocks.GOLD_ORE);
		bendableBlocks.add(Blocks.LAPIS_ORE);
		bendableBlocks.add(Blocks.REDSTONE_ORE);
		bendableBlocks.add(Blocks.RED_SANDSTONE);
	}
	
	public static void load() {
		ConfigLoader.load(CONFIG, "avatar/balance.cfg");
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
