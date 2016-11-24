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

package com.crowsofwar.gorecore.config;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Animal {
	
	public static ObjectLoader<Animal> ANIMAL = (Configuration cfg) -> new Animal(
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
