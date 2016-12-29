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

import java.util.List;

/**
 * Allows quick creation of a NodeFunctional at runtime via the builder pattern
 * and lambdas.
 * 
 * @author CrowsOfWar
 */
public class NodeBuilder {
	
	private final String name;
	private boolean op;
	
	public NodeBuilder(String name) {
		this.name = name;
	}
	
	public NodeFunctional build() {
		NodeFunctional node = new NodeFunctional(name, op) {
			@Override
			protected ICommandNode doFunction(CommandCall call, List<String> options) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		return node;
	}
	
}
