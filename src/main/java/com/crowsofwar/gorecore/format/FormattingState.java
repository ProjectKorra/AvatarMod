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

package com.crowsofwar.gorecore.format;

import static com.crowsofwar.gorecore.format.FormattingState.Setting.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.util.text.TextFormatting;

/**
 * Represents the current formatting state.
 * <p>
 * Multiple ChatFormats are "overlayed" to provide an overall color, sort of
 * like how HTML tags are put on top of each other, but the topmost ones
 * determine the actual CSS.
 * 
 * @author CrowsOfWar
 */
public class FormattingState {
	
	private final Stack<ChatFormat> formats;
	
	public FormattingState() {
		formats = new Stack<>();
	}
	
	public void pushFormat(ChatFormat format) {
		formats.push(format);
	}
	
	public void popFormat() {
		formats.pop();
	}
	
	public ChatFormat topFormat() {
		return formats.peek();
	}
	
	public boolean hasFormat() {
		return !formats.isEmpty();
	}
	
	public boolean isBold() {
		boolean bold = false;
		for (int i = 0; i < formats.size(); i++) {
			ChatFormat format = formats.get(i);
			if (!format.isBold.isUnknown()) bold = format.isBold.value();
		}
		return bold;
	}
	
	public boolean isItalic() {
		boolean italic = false;
		for (int i = 0; i < formats.size(); i++) {
			ChatFormat format = formats.get(i);
			if (!format.isItalic.isUnknown()) italic = format.isItalic.value();
		}
		return italic;
	}
	
	public TextFormatting getColor() {
		TextFormatting color = TextFormatting.WHITE;
		for (int i = 0; i < formats.size(); i++) {
			ChatFormat format = formats.get(i);
			if (format.color != null) color = format.color;
		}
		return color;
	}
	
	@Override
	public String toString() {
		return "FormattingState " + formats;
	}
	
	/**
	 * Represents a set of chat formatting settings
	 * 
	 * @author CrowsOfWar
	 */
	static class ChatFormat {
		
		private final String name;
		private final Setting isBold;
		private final Setting isItalic;
		private final TextFormatting color;
		
		private ChatFormat(String name, Setting isBold, Setting isItalic, TextFormatting color) {
			this.name = name;
			this.isBold = isBold;
			this.isItalic = isItalic;
			this.color = color;
		}
		
		public String getName() {
			return name;
		}
		
	}
	
	/**
	 * A set of chat formats, which contains the default ones, but you can also
	 * add more.
	 * 
	 * @author CrowsOfWar
	 */
	static class ChatFormatSet {
		
		// set has better performance than list, and we don't need duplicate
		// entries anyways
		private final Set<ChatFormat> formats;
		
		public ChatFormatSet() {
			formats = new HashSet<>();
			
			// add colors
			for (TextFormatting tf : TextFormatting.values()) {
				if (tf.isColor()) addFormat(tf.getFriendlyName(), tf, UNKNOWN, UNKNOWN);
			}
			// add special
			addFormat("bold", null, TRUE, UNKNOWN);
			addFormat("italic", null, UNKNOWN, TRUE);
			addFormat("noformat", null, FALSE, FALSE);
			
		}
		
		public void addFormat(String name, TextFormatting color, Setting bold, Setting italic) {
			formats.add(new ChatFormat(name, bold, italic, color));
		}
		
		public ChatFormat lookup(String name) {
			for (ChatFormat format : formats) {
				if (format.name.equals(name)) {
					return format;
				}
			}
			return null;
		}
		
		public boolean isFormatFor(String name) {
			return lookup(name) != null;
		}
		
	}
	
	enum Setting {
		UNKNOWN,
		TRUE,
		FALSE;
		
		public boolean value() {
			return this == TRUE;
		}
		
		public boolean isUnknown() {
			return this == UNKNOWN;
		}
		
	}
	
}
