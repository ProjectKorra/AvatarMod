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

package com.crowsofwar.gorecore.tree;

import java.util.Arrays;

import net.minecraft.command.ICommandSender;

public class CommandCall {
	
	private ICommandSender from;
	private boolean isOp;
	private String[] passedArgs;
	private int argumentIndex;
	
	public CommandCall(ICommandSender from, String[] passedArgs) {
		this.from = from;
		this.passedArgs = passedArgs;
		this.argumentIndex = 0;
		
		// TODO find a better way to find out if Command sender is operator
		isOp = from.canCommandSenderUseCommand(2, "gamemode");
		
	}
	
	public ArgumentList popArguments(IArgument<?>... arguments) {
		String[] poppedArray = Arrays.copyOfRange(passedArgs, argumentIndex, passedArgs.length);
		argumentIndex += arguments.length;
		return new ArgumentList(poppedArray, arguments);
	}
	
	public int getArgumentsLeft() {
		return passedArgs.length - argumentIndex;
	}
	
	public boolean isOpped() {
		return isOp;
	}
	
	public ICommandSender getFrom() {
		return from;
	}
	
}
