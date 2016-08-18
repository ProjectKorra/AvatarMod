package com.crowsofwar.gorecore.tree.test;

import com.crowsofwar.gorecore.tree.ICommandNode;
import com.crowsofwar.gorecore.tree.NodeBranch;
import com.crowsofwar.gorecore.tree.TreeCommand;

public class TreeTest extends TreeCommand {
	
	@Override
	public String getCommandName() {
		return "test";
	}
	
	@Override
	protected ICommandNode[] addCommands() {
		ICommandNode cakeFrost = new TestCakeFrost();
		ICommandNode cakeLick = new TestCakeLick();
		ICommandNode branchCake = new NodeBranch(branchHelpDefault, "cake", cakeFrost, cakeLick);
		
		ICommandNode videogamesPlay = new TestPlayVideogames();
		ICommandNode videogamesBuy = new TestBuyVideogames();
		ICommandNode branchVideogames = new NodeBranch(TestMessages.MSG_VIDEOGAME_BRANCH_HELP, "videogames", videogamesPlay, videogamesBuy);
		
		return new ICommandNode[] { branchCake, branchVideogames, new TestUseChatSender(),
				new TestSendMessage("const", TestMessages.MSG_CONST) };
		
	}
}
