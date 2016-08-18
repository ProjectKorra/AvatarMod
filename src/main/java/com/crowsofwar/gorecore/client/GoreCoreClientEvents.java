package com.crowsofwar.gorecore.client;

import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GoreCoreClientEvents {
	
	/**
	 * Gets the occurence of symbol sign that is the last one before a normal character. If the
	 * symbol is not present, etc, this returns 0.
	 */
	private int getSymbolOccurence(String str) {
		char[] chs = str.toCharArray();
		boolean wasLastCharSymbol = false;
		
		for (int i = 0; i < chs.length; i++) {
			char ch = chs[i];
			if (ch == '\u00a7') {
				wasLastCharSymbol = true;
			} else {
				// Stop??
				if (wasLastCharSymbol) {
					// Just a format code - keep going
					wasLastCharSymbol = false;
				} else {
					// Stop!
					return i;
				}
			}
		}
		
		return 0;
		
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientChat(ClientChatReceivedEvent event) {
		if (event.message.getUnformattedText().startsWith("[gc-format]")) {
			IChatComponent old = event.message;
			
			// Adjust the chat text.
			String nooText = old.getUnformattedText();
			nooText = nooText.replace('`', '\u00a7'); // add colors
			nooText = nooText.replace("[gc-format]", ""); // remove [gc-format] part
			
			// Find %{n}VAR% in string, replace with %{n}$s
			Pattern pattern = Pattern.compile("\\|(.*?)\\|");
			Matcher matcher = pattern.matcher(nooText);
			while (matcher.find()) {
				String var = matcher.group(1);
				String onlyNumbers = var.replaceAll("[^\\d]", "");
				if (!onlyNumbers.equals("")) {
					int formatArgRepresenting = Integer.parseInt(onlyNumbers);
					nooText = nooText.replace("|" + var + "|", "%" + formatArgRepresenting + "$s");
				}
			}
			
			// Re-format the text
			try {
				if (event.message instanceof ChatComponentTranslation) {
					nooText = nooText.format(nooText, ((ChatComponentTranslation) event.message).getFormatArgs());
				}
			} catch (IllegalFormatException e) {
				FMLLog.warning(
						"GoreCore> Caught a formatting error while using multicommand. " + "Check your lang file if you're localizing!");
				e.printStackTrace();
			}
			
			// Make sure that each word has a \u00a7{code} before it to prevent losing colors on new
			// line
			String[] split = nooText.split(" "); // nooText split between spaces
			String[] splitResult = new String[split.length]; // split but every word has
																// \u00a7{code} before it
			String prevCode = ""; // the formatting code(s) of the previous word
			for (int i = 0; i < split.length; i++) {
				String word = split[i];
				
				// prevCode before new stuff was added to the word
				String codeBeforeAddingToWord = prevCode;
				// Store new codes in word
				boolean isNextFormatCode = false;
				char[] chsInWord = word.toCharArray();
				for (int j = 0; j < chsInWord.length; j++) {
					char ch = chsInWord[j];
					
					if (isNextFormatCode) {
						isNextFormatCode = false;
						prevCode += "\u00a7" + ch;
						if (ch == 'r') prevCode = ""; // clean out codes because of the reset
					}
					
					if (ch == '\u00a7') isNextFormatCode = true;
				}
				// Add previous codes to word
				word = codeBeforeAddingToWord + word;
				
				// Store the modified word into splitResult
				splitResult[i] = word;
			}
			
			// Re-compile splitResult back into nooText
			nooText = "";
			for (String str : splitResult)
				nooText += str + " ";
			
			// Make fake pipes ("`\" display normally
			nooText = nooText.replace("\u00a7\\", "|");
			
			IChatComponent noo = new ChatComponentText(nooText);
			event.message = noo;
			
		}
		
		// Combining chat messages! Well, sort of.
		// [gc-format]my.chat.1,my.chat.2,my.chat.3 results in:
		// sending my.chat.1, formatted with TRANSLATED arguments my.chat.2 and my.chat.3
		// When my.chat.2 and my.chat.3 are translated
		// To get GoreCore formatting, apply [gc-format] to my.chat.1, but not my.chat.2 or
		// my.chat.3
		String text = event.message.getUnformattedText();
		if (text.startsWith("[gc-combine]")) {
			event.setCanceled(true);
			String[] keysToSend = text.substring("[gc-combine]".length()).split(",");
			if (keysToSend.length >= 1) {
				
				// Translation parameters
				Object[] translateParams = event.message instanceof ChatComponentTranslation
						? translateParams = ((ChatComponentTranslation) event.message).getFormatArgs() : new Object[0];
				
				// Parameters to add to
				Object[] params = new Object[keysToSend.length - 1];
				for (int i = 0; i < params.length; i++)
					params[i] = GoreCore.proxy.translate(keysToSend[i + 1], translateParams);
				
				// Step 1: Translate the basic thingy
				String newText = I18n.format(keysToSend[0]);
				
				// Step 2: GoreCore Formatting Variables
				Pattern pattern = Pattern.compile("\\|(.*?)\\|");
				Matcher matcher = pattern.matcher(newText);
				while (matcher.find()) {
					String var = matcher.group(1);
					String onlyNumbers = var.replaceAll("[^\\d]", "");
					if (!onlyNumbers.equals("")) {
						int formatArgRepresenting = Integer.parseInt(onlyNumbers);
						newText = newText.replace("|" + var + "|", "%" + formatArgRepresenting + "$s");
					}
				}
				
				// Step 3: Arguments
				newText = I18n.format(newText, params);
				
				// Step 4: Send & post event
				IChatComponent chatToSend = new ChatComponentText(newText);
				ClientChatReceivedEvent newEvent = new ClientChatReceivedEvent(chatToSend);
				if (!MinecraftForge.EVENT_BUS.post(newEvent) && newEvent.message != null)
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(newEvent.message);
				
			}
		}
	}
	
}
