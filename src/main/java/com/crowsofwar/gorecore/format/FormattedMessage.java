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

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.*;

public class FormattedMessage {

	private final String translateKey;
	private final String[] translateArgs;
	private final MessageConfiguration config;

	private FormattedMessage(MessageConfiguration config, String translateKey, String... translateArgs) {
		this.translateKey = translateKey;
		this.translateArgs = translateArgs;
		this.config = config;
	}

	/**
	 * Creates a new chat message with the given translation key and formatting
	 * names.
	 *
	 * @param translateKey
	 * @param translateArgs
	 * @return
	 */
	public static FormattedMessage newChatMessage(String translateKey, String... translateArgs) {
		return newChatMessage(MessageConfiguration.DEFAULT, translateKey, translateArgs);
	}

	/**
	 * Creates a new chat message with the given translation key, formatting
	 * names, and configurations.
	 *
	 * @param config
	 * @param translateKey
	 * @param translateArgs
	 * @return
	 */
	public static FormattedMessage newChatMessage(MessageConfiguration config, String translateKey, String... translateArgs) {
		FormattedMessage cm = new FormattedMessage(config, translateKey, translateArgs);
		ChatSender.translateKeyToChatMessage.put(translateKey, cm);
		return cm;
	}

	public String getTranslateKey() {
		return translateKey;
	}

	public String[] getTranslationArgs() {
		return translateArgs;
	}

	public MultiMessage chain() {
		return new MultiMessage().add(this);
	}

	public MessageConfiguration getConfig() {
		return config;
	}

	public ITextComponent getChatMessage(Object... formatValues) {
		return new TextComponentTranslation(translateKey, formatValues);

	}

	public void send(ICommandSender sender, Object... formatValues) {
		sender.sendMessage(getChatMessage(formatValues));
	}

}
