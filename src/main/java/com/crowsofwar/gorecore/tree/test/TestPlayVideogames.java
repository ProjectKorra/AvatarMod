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

package com.crowsofwar.gorecore.tree.test;

import java.util.List;

import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.tree.CommandCall;
import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeFunctional;

import net.minecraft.util.text.TextComponentTranslation;

public class TestPlayVideogames extends NodeFunctional {
	
	public TestPlayVideogames() {
		super("play", false);
	}
	
	@Override
	protected ICommandNode doFunction(CommandCall call, List<String> options) {
		String videogame = options.isEmpty() ? "" : options.get(0);
		String send = options.isEmpty() ? "test.videogames.none" : "test.videogames";
		call.getFrom().sendMessage(new TextComponentTranslation(send, videogame));
		return null;
	}
	
	@Override
	public FormattedMessage getInfoMessage() {
		return TestMessages.MSG_PLAYVIDEOGAMES_HELP;
	}
	
}
