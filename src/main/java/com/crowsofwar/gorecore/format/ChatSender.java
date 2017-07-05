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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChatSender {
	
	public static final ChatSender instance;
	
	private static final Map<String, FormattedMessage> referenceToChatMessage;
	static final Map<String, FormattedMessage> translateKeyToChatMessage;
	
	/**
	 * Cause static block to be called
	 */
	public static void load() {}
	
	static {
		instance = new ChatSender();
		MinecraftForge.EVENT_BUS.register(instance);
		referenceToChatMessage = new HashMap<>();
		translateKeyToChatMessage = new HashMap<>();
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
			
			// Create a list containing the message and its siblings
			// siblings are messages that appear next to it, but can have
			// different formatting
			List<ITextComponent> comps = new ArrayList();
			Object[] formatArgs = getFormatArgs(message);
			comps.add(new TextComponentTranslation(getKey(message), formatArgs));
			comps.addAll(e.getMessage().getSiblings());
			
			// Format each chat component
			
			String result = "";
			
			for (ITextComponent chat : comps) {
				String processed = formatChatComponent(chat);
				if (processed != null) {
					result += processed;
				}
			}
			if (!result.isEmpty()) e.setMessage(new TextComponentTranslation(result));
			
		}
		
	}
	
	/**
	 * Formats the chat component by interpreting the tags. This is only done if
	 * there is a registered FormattedMessage associated with the chat key.
	 */
	private String formatChatComponent(ITextComponent chat) {
		
		if (chat instanceof TextComponentTranslation) {
			TextComponentTranslation translate = (TextComponentTranslation) chat;
			String key = getKey(translate);
			FormattedMessage cm = translateKeyToChatMessage.get(key);
			
			if (cm != null) {
				
				try {
					return formatText(translate.getUnformattedText().replaceAll("%", "%%"), cm,
							translate.getFormatArgs());
				} catch (ProcessingException e) {
					e.printStackTrace();
					return "Error processing text; see log for details";
				}
				
			}
			
		}
		
		return result;
		
	}
	
	static void send(ICommandSender sender, FormattedMessage message, Object... args) {
		sender.addChatMessage(message.getChatMessage(args));
	}
	
	static class ProcessingException extends RuntimeException {
		
		public ProcessingException(String message) {
			super(message);
		}
		
	}
	
}
