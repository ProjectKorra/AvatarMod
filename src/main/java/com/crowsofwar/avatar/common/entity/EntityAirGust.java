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

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.util.AvatarUtils.afterVelocityAdded;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityAirGust extends EntityArc {
	
	public static final Vector ZERO = new Vector(0, 0, 0);
	
	public EntityAirGust(World world) {
		super(world);
		setSize(0.5f, 0.5f);
		putsOutFires = true;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		ControlPoint first = getControlPoint(0);
		ControlPoint second = getControlPoint(1);
		if (first.position().sqrDist(second.position()) >= getControlPointMaxDistanceSq()
				|| ticksExisted > 80) {
			setDead();
		}
	}
	
	@Override
	protected void onCollideWithBlock() {
		
	}
	
	@Override
	protected Vector getGravityVector() {
		return ZERO;
	}
	
	@Override
	protected ControlPoint createControlPoint(float size) {
		return new AirGustControlPoint(this, 0.5f, 0, 0, 0);
	}
	
	@Override
	public int getAmountOfControlPoints() {
		return 2;
	}
	
	@Override
	protected double getControlPointMaxDistanceSq() {
		return 400; // 20
	}
	
	@Override
	protected double getControlPointTeleportDistanceSq() {
		// Note: Is not actually called.
		// Set dead as soon as reached sq-distance
		return 200;
	}
	
	public static class AirGustControlPoint extends ControlPoint {
		
		public AirGustControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {
			if (!entity.worldObj.isRemote && entity != owner
					&& entity != GoreCore.proxy.getClientSidePlayer()) {
				
				AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(owner);
				float xp = 0;
				if (data != null) {
					AbilityData abilityData = data.getAbilityData(BendingAbility.ABILITY_AIR_GUST);
					xp = abilityData.getXp();
					abilityData.addXp(SKILLS_CONFIG.airGustHit);
				}
				
				Vector velocity = velocity().times(0.3).times(1 + xp / 200.0);
				velocity.setY(1);
				
				entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
				afterVelocityAdded(entity);
				
				setDead();
				
			}
		}
		
		@Override
		public void onUpdate() {
			super.onUpdate();
			if (arc.getControlPoint(0) == this) {
				float expansionRate = 1f / 20;
				size += expansionRate;
			}
		}
		
	}
	
}
