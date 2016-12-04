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

package com.crowsofwar.avatar.common.entityproperty;

import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;

/**
 * An entity property which allows simple access to the entity's motion vector
 * by manipulating the motionX, motionY, and motionZ fields. All methods for
 * velocity are in m/s.
 *
 */
public class EntityPropertyMotion implements IEntityProperty<Vector> {
	
	private final Vector internalVelocity;
	private final Entity entity;
	
	public EntityPropertyMotion(Entity entity) {
		this.internalVelocity = new Vector(0, 0, 0);
		this.entity = entity;
	}
	
	@Override
	public Vector getValue() {
		internalVelocity.setX(entity.motionX * 20);
		internalVelocity.setY(entity.motionY * 20);
		internalVelocity.setZ(entity.motionZ * 20);
		return internalVelocity;
	}
	
	@Override
	public void setValue(Vector value) {
		entity.motionX = value.x() / 20;
		entity.motionY = value.y() / 20;
		entity.motionZ = value.z() / 20;
	}
	
}
