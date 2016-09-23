package com.crowsofwar.gorecore.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Animal {
	
	public static ConfigurableFactory<Animal> ANIMAL = (Configuration cfg) -> new Animal(
			cfg.load("species").asString(), cfg.load("name").asString(), cfg.load("eats").asString(),
			cfg.load("age").asInt());
	
	private final String species, name, diet;
	private final int age;
	
	public Animal(String species, String name, String diet, int age) {
		this.species = species;
		this.name = name;
		this.diet = diet;
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "Animal [species=" + species + ", name=" + name + ", diet=" + diet + ", age=" + age + "]";
	}
	
}
