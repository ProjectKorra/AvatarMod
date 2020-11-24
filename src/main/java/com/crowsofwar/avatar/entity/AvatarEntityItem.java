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
package com.crowsofwar.avatar.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class AvatarEntityItem extends EntityItem {

	private static final DataParameter<Boolean> SYNC_RESIST_FIRE = EntityDataManager
			.createKey(AvatarEntityItem.class, DataSerializers.BOOLEAN);

	public AvatarEntityItem(World world) {
		super(world);
	}

	public AvatarEntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_RESIST_FIRE, false);
	}

	public boolean resistsFire() {
		return dataManager.get(SYNC_RESIST_FIRE);
	}

	public void setResistFire(boolean resistFire) {
		dataManager.set(SYNC_RESIST_FIRE, resistFire);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (resistsFire() && source.isFireDamage()) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

}
