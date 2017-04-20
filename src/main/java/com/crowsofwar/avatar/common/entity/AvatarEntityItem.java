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
package com.crowsofwar.avatar.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarEntityItem extends EntityItem {
	
	private boolean resistFire;
	
	public AvatarEntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}
	
	public void setResistFire(boolean resistFire) {
		this.resistFire = resistFire;
	}
	
	@Override
	protected void dealFireDamage(int amount) {
		if (!resistFire) {
			super.dealFireDamage(amount);
		}
	}
	
}
