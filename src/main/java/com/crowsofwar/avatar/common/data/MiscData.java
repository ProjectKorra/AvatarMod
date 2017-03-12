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

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class MiscData {
	
	private final Runnable save;
	
	private float fallAbsorption;
	private boolean skating;
	private int timeInAir;
	private int abilityCooldown;
	private boolean wallJumping;
	
	public MiscData(Runnable save) {
		this.save = save;
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(fallAbsorption);
		buf.writeBoolean(skating);
		buf.writeInt(timeInAir);
		buf.writeInt(abilityCooldown);
		buf.writeBoolean(wallJumping);
	}
	
	public void fromBytes(ByteBuf buf) {
		fallAbsorption = buf.readFloat();
		skating = buf.readBoolean();
		timeInAir = buf.readInt();
		abilityCooldown = buf.readInt();
		wallJumping = buf.readBoolean();
	}
	
	public boolean isSkating() {
		return skating;
	}
	
	public void setSkating(boolean skating) {
		this.skating = skating;
	}
	
	public float getFallAbsorption() {
		return fallAbsorption;
	}
	
	public void setFallAbsorption(float fallAbsorption) {
		if (fallAbsorption == 0 || fallAbsorption > this.fallAbsorption) this.fallAbsorption = fallAbsorption;
	}
	
	public int getTimeInAir() {
		return timeInAir;
	}
	
	public void setTimeInAir(int time) {
		this.timeInAir = time;
	}
	
	public int getAbilityCooldown() {
		return abilityCooldown;
	}
	
	public void setAbilityCooldown(int cooldown) {
		if (cooldown < 0) cooldown = 0;
		this.abilityCooldown = cooldown;
		save.run();
	}
	
	public void decrementCooldown() {
		if (abilityCooldown > 0) {
			abilityCooldown--;
			save.run();
		}
	}
	
	public boolean isWallJumping() {
		return wallJumping;
	}
	
	public void setWallJumping(boolean wallJumping) {
		this.wallJumping = wallJumping;
	}
	
}
