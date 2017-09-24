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

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.util.AvatarUtils.afterVelocityAdded;

public class EntityAirGust extends EntityArc<EntityAirGust.AirGustControlPoint> {
	
	public static final Vector ZERO = new Vector(0, 0, 0);
	
	private boolean airGrab, destroyProjectiles;
	
	public EntityAirGust(World world) {
		super(world);
		setSize(1.5f, 1.5f);
		putsOutFires = true;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		airGrab = nbt.getBoolean("AirGrab");
		destroyProjectiles = nbt.getBoolean("DestroyProjectiles");
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("AirGrab", airGrab);
		nbt.setBoolean("DestroyProjectiles", destroyProjectiles);
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
	protected void onCollideWithEntity(Entity entity) {
		EntityLivingBase owner = getOwner();
		if (!entity.world.isRemote && entity != owner) {
			
			BendingData data = Bender.get(owner).getData();
			float xp = 0;
			if (data != null) {
				AbilityData abilityData = data.getAbilityData("air_gust");
				xp = abilityData.getTotalXp();
				abilityData.addXp(SKILLS_CONFIG.airGustHit);
			}
			
			Vector velocity = velocity().times(0.15).times(1 + xp / 200.0);
			velocity = velocity.withY(airGrab ? -1 : 1).times(airGrab ? -0.8 : 1);

			entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
			afterVelocityAdded(entity);
			
			setDead();
			
			if (entity instanceof AvatarEntity) {
				((AvatarEntity) entity).onAirContact();
			}
			
		}
	}
	
	@Override
	protected boolean canCollideWith(Entity entity) {
		return true;
	}
	
	@Override
	public boolean onCollideWithSolid() {
		setDead();
		return true;
	}

	@Override
	protected AirGustControlPoint createControlPoint(float size, int index) {
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
	
	public boolean doesAirGrab() {
		return airGrab;
	}
	
	public void setAirGrab(boolean airGrab) {
		this.airGrab = airGrab;
	}
	
	public boolean doesDestroyProjectiles() {
		return destroyProjectiles;
	}
	
	public void setDestroyProjectiles(boolean destroyProjectiles) {
		this.destroyProjectiles = destroyProjectiles;
	}
	
	public static class AirGustControlPoint extends ControlPoint {
		
		public AirGustControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
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
