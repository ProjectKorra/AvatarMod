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
package com.crowsofwar.avatar.common.data;

import java.util.function.Function;

/**
 * 
 * 
 * @author CrowsOfWar
 */

public enum DataCategory {
	
	BENDING(data -> data.getAllBending()),
	STATUS_CONTROLS(data -> data.getAllStatusControls()),
	ABILITY_DATA(data -> data.getAbilityDataMap()),
	CHI(data -> data.chi()),
	MISC(data -> data.getMiscData()),
	TICK_HANDLERS(data -> data.getAllTickHandlers());
	
	private final Function<BendingData, Object> getter;
	
	private DataCategory(Function<BendingData, Object> getter) {
		this.getter = getter;
	}
	
	public Object get(BendingData data) {
		return getter.apply(data);
	}
	
}
