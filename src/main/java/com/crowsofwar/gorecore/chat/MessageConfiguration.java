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

package com.crowsofwar.gorecore.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.text.TextFormatting;

public class MessageConfiguration {
	
	public static final MessageConfiguration DEFAULT = new MessageConfiguration();
	
	private final Map<String, TextFormatting> colors;
	/**
	 * Constants are variables which are applied to all ChatMessages using this
	 * MessageConfiguration. They are applied once to the MessageConfiguration,
	 * then every ChatMessage that uses this configuration will receive those
	 * constants as variables. Constants are used in the same way as
	 * message-specific formatting arguments:
	 * <code>Constant: ${const_name}</code>.
	 */
	private final Map<String, String> constants;
	
	public MessageConfiguration() {
		this.colors = new HashMap<String, TextFormatting>();
		this.constants = new HashMap<>();
	}
	
	public MessageConfiguration addColor(String reference, TextFormatting color) {
		if (!color.isColor()) throw new IllegalArgumentException("The chat formatting must be a color");
		this.colors.put(reference, color);
		return this;
	}
	
	public TextFormatting getColor(String reference) {
		return colors.get(reference);
	}
	
	public String getColorName(String reference) {
		if (hasColor(reference))
			return getColor(reference).name().toLowerCase();
		else
			return null;
	}
	
	public boolean hasColor(String reference) {
		return colors.containsKey(reference);
	}
	
	public Map<String, TextFormatting> allColors() {
		return new HashMap<>(colors);
	}
	
	/**
	 * Add a constant to this message configuration.
	 * <p>
	 * Constants are variables which are applied to all ChatMessages using this
	 * MessageConfiguration. They are applied once to the MessageConfiguration,
	 * then every ChatMessage that uses this configuration will receive those
	 * constants as variables. Constants are used in the same way as
	 * message-specific formatting arguments:
	 * <code>Constant: ${const_name}</code>.
	 * 
	 * @param name
	 *            The name of the constant
	 * @param value
	 *            The value assigned to the constant
	 * @return this
	 */
	public MessageConfiguration addConstant(String name, String value) {
		this.constants.put(name, value);
		return this;
	}
	
	/**
	 * Returns a set of the constants.
	 * <p>
	 * Each element in the set is an entry which maps constant name -> constant
	 * value.
	 */
	public Set<Map.Entry<String, String>> getAllConstants() {
		return Collections.unmodifiableSet(constants.entrySet());
	}
	
}
