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
package com.crowsofwar.avatar.client;

import net.minecraft.client.settings.KeyBinding;

import com.crowsofwar.avatar.client.controls.KeybindingWrapper;

import javax.annotation.Nonnull;

/**
 * @author CrowsOfWar
 */
public class ClientKeybindWrapper extends KeybindingWrapper {
	@Nonnull
	private final KeyBinding kb;

	public ClientKeybindWrapper(@Nonnull KeyBinding kb) {
		this.kb = kb;
	}

	@Override
	public String getKeyDescription() {
		return kb.getKeyDescription();
	}

	@Override
	public boolean isPressed() {
		return kb.isPressed();
	}

	@Override
	public boolean isDown() {
		return kb.isKeyDown();
	}

}
