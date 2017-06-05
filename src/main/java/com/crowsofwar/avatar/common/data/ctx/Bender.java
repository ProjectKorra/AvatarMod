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
package com.crowsofwar.avatar.common.data.ctx;

import com.crowsofwar.avatar.common.data.BendingData;

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
	
	default BenderInfo getInfo() {
		return new BenderInfo(this);
	}
	
	BendingData getData();
	
	boolean isCreativeMode();
	
	boolean isFlying();
	
	/**
	 * If any water pouches are in the inventory, checks if there is enough
	 * water. If there is, consumes the total amount of water in those pouches
	 * and returns true.
	 */
	boolean consumeWaterLevel(int amount);
	
	/**
	 * Creates an appropriate Bender instance for that entity
	 */
	public static Bender create(EntityLivingBase entity) {
		if (entity == null) {
			return null;
		} else if (entity instanceof Bender) {
			return (Bender) entity;
		} else if (entity instanceof EntityPlayer) {
			return new PlayerBender((EntityPlayer) entity);
		} else {
			throw new IllegalArgumentException("Unsure how to create bender for entity " + entity);
		}
	}
	
	public static BendingData getData(EntityLivingBase entity) {
		Bender bender = create(entity);
		return bender == null ? null : bender.getData();
	}
	
	public static boolean isBenderSupported(EntityLivingBase entity) {
		return entity == null || entity instanceof EntityPlayer || entity instanceof Bender;
	}
	
}
