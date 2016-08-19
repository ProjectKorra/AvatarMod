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
