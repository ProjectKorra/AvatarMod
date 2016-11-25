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
