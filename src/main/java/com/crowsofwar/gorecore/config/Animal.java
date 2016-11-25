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
