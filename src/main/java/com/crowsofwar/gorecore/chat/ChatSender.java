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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crowsofwar.gorecore.chat.FormattingState.ChatFormatSet;
import com.crowsofwar.gorecore.chat.FormattingState.Setting;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChatSender {
	
	public static final ChatSender instance;
	
	private static final Map<String, ChatMessage> referenceToChatMessage;
	static final Map<String, ChatMessage> translateKeyToChatMessage;
	
	/**
	 * Cause static block to be called
	 */
	public static void load() {}
	
	static {
		instance = new ChatSender();
		MinecraftForge.EVENT_BUS.register(instance);
		referenceToChatMessage = new HashMap<String, ChatMessage>();
		translateKeyToChatMessage = new HashMap<String, ChatMessage>();
	}
	
	private ChatSender() {}
	
	private Object[] getFormatArgs(TextComponentTranslation message) {
		return ObfuscationReflectionHelper.getPrivateValue(TextComponentTranslation.class, message, 1);
	}
	
	private String getKey(TextComponentTranslation message) {
		return ObfuscationReflectionHelper.getPrivateValue(TextComponentTranslation.class, message, 0);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void processClientChat(ClientChatReceivedEvent e) {
		if (e.getMessage() instanceof TextComponentTranslation) {
			TextComponentTranslation message = (TextComponentTranslation) e.getMessage();
			
			String result = "";
			
			List<ITextComponent> comps = new ArrayList();
			
			Object[] cloneFormatArgs = getFormatArgs(message);
			comps.add(new TextComponentTranslation(getKey(message), cloneFormatArgs));
			
			comps.addAll(e.getMessage().getSiblings());
			boolean changed = false;
			
			for (ITextComponent chat : comps) {
				String processed = processChatComponent(chat);
				if (processed != null) {
					changed = true;
					result += processed;
				}
			}
			if (changed) e.setMessage(new TextComponentTranslation(result));
		}
		
	}
	
	private String processChatComponent(ITextComponent chat) {
		String result = null;
		if (chat instanceof TextComponentTranslation) {
			TextComponentTranslation translate = (TextComponentTranslation) chat;
			String key = (String) getKey(translate);
			ChatMessage cm = translateKeyToChatMessage.get(key);
			
			if (cm != null) {
				
				try {
					result = processText(translate.getUnformattedText(), cm, translate.getFormatArgs());
				} catch (ProcessingException e) {
					result = "Error processing text; see log for details";
					e.printStackTrace();
				}
				
			}
			
		}
		
		return result;
		
	}
	
	public String processText(String text, ChatMessage cm, Object... formatArgs) {
		MessageConfiguration cfg = cm.getConfig();
		ChatFormatSet formatSet = new ChatFormatSet();
		
		for (Map.Entry<String, TextFormatting> color : cfg.allColors().entrySet()) {
			formatSet.addFormat(color.getKey(), color.getValue(), Setting.UNKNOWN, Setting.UNKNOWN);
		}
		
		String[] translateArgs = cm.getTranslationArgs();
		for (int i = 0; i < translateArgs.length; i++) {
			text = text.replace("${" + translateArgs[i] + "}", formatArgs[i].toString());
		}
		
		Set<Map.Entry<String, String>> consts = cfg.getAllConstants();
		for (Map.Entry<String, String> entry : consts) {
			text = text.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		FormattingState format = new FormattingState();
		
		String newText = "";
		
		// Separate the text by square brackets
		// for demo, see http://regexr.com/, regex is: \\?\[?\/?[^\]\[\\]+\]?
		Matcher matcher = Pattern.compile("\\\\?\\[?\\/?[^\\]\\[\\\\]+\\]?").matcher(text);
		
		while (matcher.find()) {
			
			// Item may be a tag, may not be
			String item = matcher.group();
			
			boolean recievedFormatInstruction = false;
			
			if (item.equals("")) continue;
			
			if (item.startsWith("[") && item.endsWith("]")) {
				
				// Is a tag
				
				String tag = item.substring(1, item.length() - 1);
				recievedFormatInstruction = true;
				
				if (formatSet.isFormatFor(tag)) {
					
					format.pushFormat(formatSet.lookup(tag));
					recievedFormatInstruction = true;
					
				} else if (tag.startsWith("/")) {
					
					if (tag.substring(1).equals(format.topFormat().getName())) {
						
						format.popFormat();
						
					} else {
						throw new ProcessingException(
								"Error processing message; closing tag does not match last opened tag: "
										+ text);
					}
					
				} else if (tag.startsWith("translate=")) {
					
					String key = tag.substring("translate=".length());
					item = processText(I18n.format(key), cm, formatArgs);
					recievedFormatInstruction = false;
					
				} else {
					
					throw new ProcessingException("String has invalid tag: [" + item + "]; text is " + text);
					
				}
				
			}
			// remove backslash from escaped tags
			if (item.startsWith("\\")) item = item.substring(1);
			
			// If any formats changed, must re add all chat formats
			if (recievedFormatInstruction) {
				newText += TextFormatting.RESET;
				newText += format.getColor(); // For some reason, color must
												// come before bold
				newText += format.isBold() ? TextFormatting.BOLD : "";
				newText += format.isItalic() ? TextFormatting.ITALIC : "";
			} else {
				newText += item;
			}
			
		}
		
		if (format.hasFormat()) {
			throw new ProcessingException(
					"Unclosed tag [" + format.topFormat().getName() + "] in text " + text);
		}
		
		text = newText;
		
		return newText;
	}
	
	static void send(ICommandSender sender, ChatMessage message, Object... args) {
		sender.addChatMessage(message.getChatMessage(args));
	}
	
	static class ProcessingException extends RuntimeException {
		
		public ProcessingException(String message) {
			super(message);
		}
		
	}
	
}
