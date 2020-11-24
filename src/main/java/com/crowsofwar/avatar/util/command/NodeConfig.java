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

package com.crowsofwar.avatar.util.command;

import com.crowsofwar.avatar.config.*;
import com.crowsofwar.gorecore.config.ConfigurationException;
import com.crowsofwar.gorecore.tree.*;
import net.minecraft.command.ICommandSender;

import java.util.List;

import static com.crowsofwar.avatar.network.AvatarChatMessages.*;

/**
 * @author CrowsOfWar
 */
public class NodeConfig extends NodeFunctional {

	private final IArgument<String> argKey;
	private final IArgument<String> argVal;

	public NodeConfig() {
		super("config", true);
		this.argKey = addArgument(new ArgumentDirect<>("key", ITypeConverter.CONVERTER_STRING, ""));
		this.argVal = addArgument(new ArgumentDirect<>("value", ITypeConverter.CONVERTER_STRING, ""));
	}

	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {

		ArgumentList list = call.popArguments(this);
		String key = list.get(argKey);
		String val = list.get(argVal);
		if (key.equals("") || val.equals("")) {

			ICommandSender from = call.getFrom();
			boolean exception = false;

			try {

				ConfigStats.load();
				ConfigSkills.load();
				ConfigClient.load();
				ConfigChi.load();
				ConfigMobs.load();
				ConfigStats.STATS_CONFIG.loadBlocks();
				ConfigAnalytics.load();

			} catch (ConfigurationException e) {

				exception = true;
				MSG_CONFIG_EXCEPTION_1.send(from);
				MSG_CONFIG_EXCEPTION_2.send(from, e.getCause().toString());
				e.printStackTrace();

			}

			if (!exception) {

				MSG_CONFIG_SUCCESS.send(from);

			}

		} else {
			// AvatarConfig.set(key, new Yaml().load(val));
			// AvatarConfig.save();
		}

		return null;
	}

}
