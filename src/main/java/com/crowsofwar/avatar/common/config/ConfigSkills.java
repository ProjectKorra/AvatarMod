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
	public float blockPlaced = 1, blockThrowHit = 2, blockKill = 3, airJump = 0.5f, airGustHit = 1,
			ravineHit = 1.5f, waveHit = 1f, waterHit = 1f, fireHit = 2f, flamethrowerHit = 0.05f;
	
	public static void load() {
		ConfigLoader.load(SKILLS_CONFIG, "avatar/skills.yml");
	}
	
}
