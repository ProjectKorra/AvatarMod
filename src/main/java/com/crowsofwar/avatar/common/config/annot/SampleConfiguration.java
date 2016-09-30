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
	
}
