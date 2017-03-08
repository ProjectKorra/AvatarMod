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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * A wrapper for any mob/player that can bend to provide greater abstraction
 * over useful methods.
 * 
 * @author CrowsOfWar
 */
public interface Bender {
	
	/**
	 * For players, returns the username. For mobs, returns the mob's name (e.g.
	 * Chicken).
	 */
	default String getName() {
		return getEntity().getName();
	}
	
	/**
	 * Return this bender in entity form
	 */
	EntityLivingBase getEntity();
	
	/**
	 * Get the world this entity is currently in
	 */
	default World getWorld() {
		return getEntity().worldObj;
	}
	
	/**
	 * Returns whether this bender is a player
	 */
	default boolean isPlayer() {
		return getEntity() instanceof EntityPlayer;
	}
	
	default boolean isNull() {
		return false;
	}
	
	default BenderInfo getInfo() {
		return new BenderInfo(this);
	}
	
	/**
	 * Creates an appropriate Bender instance for that entity
	 */
	public static Bender create(EntityLivingBase entity) {
		return new PlayerBender((EntityPlayer) entity);
	}
	
}
