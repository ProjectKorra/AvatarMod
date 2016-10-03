package com.crowsofwar.avatar.common.config.annot;

/**
 * 
 * 
 * @author CrowsOfWar
 */
@HasCustomLoader(loaderClass = Animal.Loader.class)
public class Animal {
	
	public static final String DEFAULT_ANIMAL = ConfigLoader.yaml(new Animal("Bob", "Builder", 4, false));
	
	@Load
	public String name;
	
	@Load
	public String species;
	
	@Load
	public int age;
	
	// can't be configured
	public boolean isAwesome;
	
	public Animal() {}
	
	public Animal(String name, String species, int age, boolean isAwesome) {
		this.name = name;
		this.species = species;
		this.age = age;
		this.isAwesome = isAwesome;
	}
	
	public static class Loader implements CustomObjectLoader<Animal> {
		
		@Override
		public void load(Object relevantConfigInfoWillGoHere, Animal obj) {
			obj.isAwesome = obj.species.equals("Crow");
		}
		
	}
	
	@Override
	public String toString() {
		return "Animal [name=" + name + ", species=" + species + ", age=" + age + ", isAwesome=" + isAwesome
				+ "]";
	}
	
}
