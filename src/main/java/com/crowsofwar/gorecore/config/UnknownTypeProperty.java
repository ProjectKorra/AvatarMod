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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a property where the type is unknown. The type must be assumed based on the
 * configuration file. Use {@link #as(ObjectLoader)} or a similar method to find the actual value by
 * casting.
 * 
 * @author CrowsOfWar
 */
public class UnknownTypeProperty {
	
	private final Object object;
	private final String name;
	
	public UnknownTypeProperty(String name, Object obj) {
		this.name = name;
		this.object = obj;
	}
	
	public Object getObject() {
		return object;
	}
	
	public <T> T as(ObjectLoader<T> factory) {
		if (!(object instanceof Map)) throw new ConfigException(name + " isn't a Dictionary");
		return factory.load(new Configuration((Map) object));
	}
	
	public String asString() {
		return object.toString();
	}
	
	public int asInt() {
		try {
			return Integer.valueOf(object + "");
		} catch (NumberFormatException e) {
			throw new ConfigException(name + " isn't an integer");
		}
	}
	
	public boolean asBoolean() {
		try {
			return (boolean) object;
		} catch (ClassCastException e) {
			throw new ConfigException(name + " isn't a boolean");
		}
	}
	
	public double asDouble() {
		try {
			return Double.valueOf(object + "");
		} catch (NumberFormatException e) {
			throw new ConfigException(name + " isn't a double");
		}
	}
	
	public <T> List<T> asList(ListLoader<T> factory) {
		if (!(object instanceof List)) throw new ConfigException(name + " isn't a List");
		
		List<T> out = new ArrayList<>();
		
		List<?> list = (List<?>) object;
		for (Object obj : list) {
			out.add(factory.load(obj));
		}
		
		return out;
	}
	
	public List<String> asStringList() {
		return asList(obj -> (String) obj);
	}
	
	public Configuration asMapping() {
		return new Configuration((Map) object);
	}
	
}
