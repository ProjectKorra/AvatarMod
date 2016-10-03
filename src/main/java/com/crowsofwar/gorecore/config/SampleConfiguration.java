package com.crowsofwar.gorecore.config;

import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SampleConfiguration {
	
	@Load
	public static int settingA;
	
	@Load
	public static double settingB;
	
	@Load
	public static Animal bob;
	
	@Load
	public static Animal fluffy = new Animal("Fluffers", "Fluff ball", 4, true);
	
	// @Load
	public static Animal notSoFluffy;
	
	@Load
	public static List<String> people;
	
	public static void main(String[] args) {
		ConfigLoader.load(SampleConfiguration.class, "annot-test.cfg");
		System.out.println("SettingA: " + settingA);
		System.out.println("SettingB: " + settingB);
		System.out.println("People: " + people);
		System.out.println("bob: " + bob);
		System.out.println("Fluffy: " + fluffy);
	}
	
}
