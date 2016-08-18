package com.crowsofwar.gorecore.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageConfiguration {
	
	public static final MessageConfiguration DEFAULT = new MessageConfiguration();
	
	private final Map<String, EnumChatFormatting> colors;
	/**
	 * Constants are variables which are applied to all ChatMessages using this
	 * MessageConfiguration. They are applied once to the MessageConfiguration, then every
	 * ChatMessage that uses this configuration will receive those constants as variables. Constants
	 * are used in the same way as message-specific formatting arguments:
	 * <code>Constant: ${const_name}</code>.
	 */
	private final Map<String, String> constants;
	
	public MessageConfiguration() {
		this.colors = new HashMap<String, EnumChatFormatting>();
		this.constants = new HashMap<>();
	}
	
	public MessageConfiguration addColor(String reference, EnumChatFormatting color) {
		if (!color.isColor()) throw new IllegalArgumentException("The chat formatting must be a color");
		this.colors.put(reference, color);
		return this;
	}
	
	public EnumChatFormatting getColor(String reference) {
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
	
	/**
	 * Add a constant to this message configuration.
	 * <p>
	 * Constants are variables which are applied to all ChatMessages using this
	 * MessageConfiguration. They are applied once to the MessageConfiguration, then every
	 * ChatMessage that uses this configuration will receive those constants as variables. Constants
	 * are used in the same way as message-specific formatting arguments:
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
	 * Each element in the set is an entry which maps constant name -> constant value.
	 */
	public Set<Map.Entry<String, String>> getAllConstants() {
		return Collections.unmodifiableSet(constants.entrySet());
	}
	
}
