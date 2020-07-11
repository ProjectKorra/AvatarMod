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

package com.crowsofwar.avatar.server;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;

import java.util.ArrayList;
import java.util.List;

public class AvatarKeybindingServer implements IControlsHandler {

	@Override
	public boolean isControlPressed(AvatarControl control) {
		return false;
	}

	@Override
	public boolean isControlDown(AvatarControl control) {
		return false;
	}

	@Override
	public int getKeyCode(AvatarControl control) {
		return -1;
	}

	@Override
	public String getDisplayName(AvatarControl control) {
		return null;
	}

	@Override
	public List<AvatarControl> getAllPressed() {
		return new ArrayList<AvatarControl>();
	}

}
