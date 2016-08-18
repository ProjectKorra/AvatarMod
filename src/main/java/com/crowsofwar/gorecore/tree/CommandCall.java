package com.crowsofwar.gorecore.tree;

import java.util.Arrays;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;

public class CommandCall {
	
	private ICommandSender from;
	private boolean isOp;
	private String[] passedArgs;
	private int argumentIndex;
	
	public CommandCall(ICommandSender from, String[] passedArgs) {
		this.from = from;
		this.passedArgs = passedArgs;
		this.argumentIndex = 0;
		
		if (from instanceof CommandBlockLogic) {
			isOp = true;
		} else if (from instanceof MinecraftServer) {
			isOp = true;
		} else if (from instanceof RConConsoleSource) {
			isOp = true;
		} else if (from instanceof EntityPlayer) {
			isOp = false;
			if (from instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) from; // TODO needs testing
				isOp = player.mcServer.getConfigurationManager().func_152596_g(player.getGameProfile());
			}
		}
		
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
