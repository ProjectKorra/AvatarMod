package com.crowsofwar.avatar.common.config.annot;

/**
 * 
 * 
 * @author CrowsOfWar
 */
@HasCustomLoader(loaderClass = Animal.Loader.class)
public class Animal {
	
	@Load
	public String name;
	
	@Load
	public String species;
	
	@Load
	public int age;
	
	// can't be configured
	public boolean isAwesome;
	
	public static class Loader implements CustomObjectLoader<Animal> {
		
		@Override
		public void load(Object relevantConfigInfoWillGoHere, Animal obj) {
			obj.isAwesome = obj.species.equals("Crow");
		}
		
	}
	
}
