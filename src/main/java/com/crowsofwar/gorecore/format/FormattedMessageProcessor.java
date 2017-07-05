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

import static com.crowsofwar.gorecore.format.FormattedMessageProcessor.FormatSetting.TRUE;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.format.ChatSender.ProcessingException;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class FormattedMessageProcessor {
	
	/**
	 * Formats the chat message to apply colors and translation arguments.
	 * 
	 * @param msg
	 *            Information about how to format the message
	 * @param text
	 *            Text to format - should be already translated
	 * @param formatValues
	 *            Values of the formatting arguments to use
	 */
	public static String formatText(FormattedMessage msg, String text, Object... formatValues) {
		
		MessageConfiguration cfg = msg.getConfig();
		
		// Apply format arguments
		String[] translateArgs = msg.getTranslationArgs();
		for (int i = 0; i < translateArgs.length; i++) {
			text = text.replace("${" + translateArgs[i] + "}", formatValues[i].toString());
		}
		
		// Apply constants from MessageConfiguration
		Set<Map.Entry<String, String>> consts = cfg.getAllConstants();
		for (Map.Entry<String, String> entry : consts) {
			text = text.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		// Apply tags
		FormattingState format = new FormattingState();
		String newText = "";
		
		// Separate the text by square brackets
		// for demo, see http://regexr.com/, regex is: \\?\[?\/?[^\]\[\\]+\]?
		Matcher matcher = Pattern.compile("\\\\?\\[?\\/?[^\\]\\[\\\\]+\\]?").matcher(text);
		
		while (matcher.find()) {
			
			// Found a new item - can be a tag to interpret or text to render
			// E.g. the following text: [bold]Hello[/bold]
			// yields items [bold], Hello, and [/bold]
			String item = matcher.group();
			// Whether to re-apply formatting symbols
			// True if this is a tag that affects formatting
			// E.g. [bold] but not [translate]
			boolean refreshFormatting = false;
			
			// TODO see if this is really necessary
			if (item.equals("")) continue;
			
			if (item.startsWith("[") && item.endsWith("]")) {
				
				// Is a tag
				String tag = item.substring(1, item.length() - 1);
				
				if (tag.equals("bold")) {
					format.pushFormat(new ChatFormat("bold").setBold(TRUE));
					refreshFormatting = true;
				} else if (tag.equals("italic")) {
					format.pushFormat(new ChatFormat("italic").setItalic(TRUE));
					refreshFormatting = true;
				} else if (getTfColor(tag) != null) {
					format.pushFormat(new ChatFormat(tag).setColor(getTfColor(tag)));
					refreshFormatting = true;
				} else if (cfg.getColor(tag) != null) {
					format.pushFormat(new ChatFormat(tag).setColor(cfg.getColor(tag)));
					refreshFormatting = true;
				} else if (tag.startsWith("/")) {
					if (tag.substring(1).equals(format.topFormat().name)) {
						format.popFormat();
						refreshFormatting = true;
					} else {
						throw new ProcessingException(
								"Error processing message; closing tag does not match last opened tag: ["
										+ tag.substring(1) + "]; text is: " + text);
					}
				} else if (tag.startsWith("translate=")) {
					String key = tag.substring("translate=".length());
					item = formatText(msg, I18n.format(key), formatValues);
				} else if (tag.startsWith("keybinding=")) {
					String key = tag.substring("keybinding=".length());
					item = GoreCore.proxy.getKeybindingDisplayName(key);
				} else {
					throw new ProcessingException("String has invalid tag: [" + tag + "]; text is " + text);
				}
				
			}
			// remove backslash from escaped tags
			if (item.startsWith("\\[") && item.endsWith("]")) item = item.substring(1);
			
			// If any formats changed (i.e. refreshFormatting == true), then the
			// item was a tag and shouldn't be added
			if (refreshFormatting) {
				newText += format.apply();
			} else {
				newText += item;
			}
			
		}
		
		if (format.hasFormat()) {
			throw new ProcessingException("Unclosed tag [" + format.topFormat().name + "] in text " + text);
		}
		
		return newText;
		
	}
	
	/**
	 * Same as {@link #formatText(FormattedMessage, String, Object...)}, but
	 * does not apply chat styles (color, italic, bold)
	 */
	public static String formatPlaintext(FormattedMessage msg, String text, Object... formatValues) {
		
		MessageConfiguration cfg = msg.getConfig();
		
		// Apply format arguments
		String[] translateArgs = msg.getTranslationArgs();
		for (int i = 0; i < translateArgs.length; i++) {
			text = text.replace("${" + translateArgs[i] + "}", formatValues[i].toString());
		}
		
		// Apply constants from MessageConfiguration
		Set<Map.Entry<String, String>> consts = cfg.getAllConstants();
		for (Map.Entry<String, String> entry : consts) {
			text = text.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		// Apply tags
		String newText = "";
		
		// Separate the text by square brackets
		// for demo, see http://regexr.com/, regex is: \\?\[?\/?[^\]\[\\]+\]?
		Matcher matcher = Pattern.compile("\\\\?\\[?\\/?[^\\]\\[\\\\]+\\]?").matcher(text);
		
		while (matcher.find()) {
			
			// Found a new item - can be a tag to interpret or text to render
			// E.g. the following text: [bold]Hello[/bold]
			// yields items [bold], Hello, and [/bold]
			String item = matcher.group();
			// format tags are ignored
			boolean formattingTag = false;
			
			// TODO see if this is really necessary
			if (item.equals("")) continue;
			
			if (item.startsWith("[") && item.endsWith("]")) {
				
				// Is a tag
				String tag = item.substring(1, item.length() - 1);
				
				if (tag.startsWith("translate=")) {
					String key = tag.substring("translate=".length());
					item = formatPlaintext(msg, I18n.format(key), formatValues);
				} else if (tag.startsWith("keybinding=")) {
					String key = tag.substring("keybinding=".length());
					item = GoreCore.proxy.getKeybindingDisplayName(key);
				} else if (tag.equals("bold") || tag.equals("italic") || getTfColor(tag) != null
						|| cfg.hasColor(tag) || tag.startsWith("/")) {
					formattingTag = true;
				} else {
					throw new ProcessingException("String has invalid tag: [" + tag + "]; text is " + text);
				}
				
			}
			
			// remove backslash from escaped tags
			if (item.startsWith("\\[") && item.endsWith("]")) item = item.substring(1);
			
			System.out.println(item + " , " + formattingTag);
			if (!formattingTag) {
				System.out.println("Add item " + item);
				newText += item;
			}
			
		}
		
		return newText;
		
	}
	
	/**
	 * Returns the corresponding TextFormatting if the name refers to a color
	 * defined in TextFormatting, or null if there isn't one. Name is in
	 * lowercase.
	 * <p>
	 * "blue" returns TextFormatting.BLUE because there that TF has name "BLUE"
	 * <p>
	 * "dog" returns null because there is no TextFormatting for dog
	 * <p>
	 * "bold" returns null because although there is TextFormatting.BOLD, it is
	 * not a color
	 * 
	 */
	private static TextFormatting getTfColor(String name) {
		for (TextFormatting tf : TextFormatting.values()) {
			if (tf.isColor() && tf.name().toLowerCase().equals(name)) {
				return tf;
			}
		}
		return null;
	}
	
	/**
	 * Represents the current state while
	 * #{@link FormattedMessageProcessor#formatText(FormattedMessage, String, Object...)
	 * formatting text}.
	 * <p>
	 * The formatting state is really a stack of {@link ChatFormat} objects.
	 * 
	 * <p>
	 * Here is an example of stepping through the following text:<br />
	 * 
	 * <pre>
	 * Hello, [bold]my name is [blue]Joe[/blue]! Nice to meet you![/bold]
	 * </pre>
	 * 
	 * <pre>
	 * ^
	 * </pre>
	 * 
	 * At this point, the stack is empty and there are no formats applied.
	 * <p>
	 * 
	 * <pre>
	 * Hello, [bold]my name is [blue]Joe[/blue]! Nice to meet you![/bold]
	 * </pre>
	 * 
	 * <pre>
	 *              ^
	 * </pre>
	 * 
	 * TODO Finish documentation
	 * 
	 * @author CrowsOfWar
	 */
	private static class FormattingState {
		
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
				if (!format.bold.isUnknown()) bold = format.bold.value();
			}
			return bold;
		}
		
		public boolean isItalic() {
			boolean italic = false;
			for (int i = 0; i < formats.size(); i++) {
				ChatFormat format = formats.get(i);
				if (!format.italic.isUnknown()) italic = format.italic.value();
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
		
		/**
		 * Generates a string containing format instructions (symbol character)
		 * to use in a string in chat.
		 */
		public String apply() {
			String result = TextFormatting.RESET.toString();
			
			// for some reason, color must come before bold
			result += getColor().toString();
			if (isBold()) {
				result += TextFormatting.BOLD.toString();
			}
			if (isItalic()) {
				result += TextFormatting.ITALIC.toString();
			}
			
			return result;
		}
		
		@Override
		public String toString() {
			return "FormattingState " + formats;
		}
		
	}
	
	/**
	 * Represents the formatting state at a specific point in the string. It is
	 * a combination of color, bold, and italic.
	 * 
	 * @author CrowsOfWar
	 */
	private static class ChatFormat {
		
		private final String name;
		private FormatSetting bold;
		private FormatSetting italic;
		private TextFormatting color; // null if color should be
										// unaffected
		
		private ChatFormat(String name) {
			this.name = name;
			bold = FormatSetting.UNAFFECTED;
			italic = FormatSetting.UNAFFECTED;
			color = null;
		}
		
		public ChatFormat setBold(FormatSetting bold) {
			this.bold = bold;
			return this;
		}
		
		public ChatFormat setItalic(FormatSetting italic) {
			this.italic = italic;
			return this;
		}
		
		public ChatFormat setColor(TextFormatting color) {
			this.color = color;
			return this;
		}
		
	}
	
	/**
	 * The effect of a setting such as bolded on the {@link ChatFormat}:
	 * <ul>
	 * <li>True - set bolded
	 * <li>False - set not bolded
	 * <li>Unaffected - don't affect whether it is bolded - other ChatFormats in
	 * the {@link FormattingState} determine the setting
	 * 
	 * @author CrowsOfWar
	 */
	public enum FormatSetting {
		UNAFFECTED,
		TRUE,
		FALSE;
		
		public boolean value() {
			return this == TRUE;
		}
		
		public boolean isUnknown() {
			return this == UNAFFECTED;
		}
		
	}
	
}
