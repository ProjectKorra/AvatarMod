package com.crowsofwar.avatar.common.config.annot;

import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SampleConfiguration {
	
	@Load
	public int settingA;
	
	@Load
	public double settingB;
	
	@Load
	public Animal bob;
	
	@Load
	public Animal fluffy;
	
	@Load
	public Animal notSoFluffy;
	
	@Load
	public List<String> people;
	
	public static void main(String[] args) {
		SampleConfiguration cfg = new SampleConfiguration();
		ConfigLoader.load(cfg, "annot-test.cfg");
		System.out.println("SettingA: " + cfg.settingA);
		System.out.println("SettingB: " + cfg.settingB);
		System.out.println("People: " + cfg.people);
	}
	
}
