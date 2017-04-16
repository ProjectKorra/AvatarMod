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
package com.crowsofwar.avatar.client.gui.skills;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.ComponentImage;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.network.packets.PacketSSkillsMenu;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentBendingTab extends ComponentImage {
	
	private final BendingType type;
	
	public ComponentBendingTab(BendingType type, boolean isFullTab, boolean isSelected) {
		super(AvatarUiTextures.skillsGui, !isFullTab && isSelected ? 216 : 236, type.ordinal() * 20, 20,
				isFullTab || isSelected ? 20 : 17);
		this.type = type;
	}
	
	@Override
	protected void click(int button) {
		System.out.println("Clicked " + type);
		AvatarMod.network.sendToServer(new PacketSSkillsMenu(type));
	}
	
}
