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
package com.crowsofwar.avatar.entity.mob;

import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

/**
 * @author CrowsOfWar
 */
public class SkyBisonMoveHelper extends EntityMoveHelper {

	private final EntitySkyBison entity;
	private int courseChangeCooldown;

	public SkyBisonMoveHelper(EntitySkyBison entity) {
		super(entity);
		this.entity = entity;
	}

	@Override
	public void onUpdateMoveHelper() {
		if (this.action == EntityMoveHelper.Action.MOVE_TO) {

			if (this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.entity.getRNG().nextInt(5) + 2;
				double x = this.posX - this.entity.posX;
				double y = this.posY - this.entity.posY;
				double z = this.posZ - this.entity.posZ;
				double distance = MathHelper.sqrt(x * x + y * y + z * z);

				if (entity.isSitting() || isNotColliding(this.posX, this.posY, this.posZ, distance)) {
					double mult = entity.getSpeedMultiplier();
					this.entity.motionX += x / distance * 0.1 * mult;
					this.entity.motionY += y / distance * 0.1 * mult;
					this.entity.motionZ += z / distance * 0.1 * mult;

					float f9 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
					this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f9, 90.0F);

				} else {
					this.action = EntityMoveHelper.Action.WAIT;
				}
			}
		}
	}

	/**
	 * Checks if entity bounding box is not colliding with terrain
	 */
	private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
		double d0 = (x - this.entity.posX) / p_179926_7_;
		double d1 = (y - this.entity.posY) / p_179926_7_;
		double d2 = (z - this.entity.posZ) / p_179926_7_;
		AxisAlignedBB axisalignedbb = this.entity.getEntityBoundingBox();

		for (int i = 1; i < p_179926_7_; ++i) {
			axisalignedbb = axisalignedbb.offset(d0, d1, d2);

			if (!this.entity.world.getCollisionBoxes(this.entity, axisalignedbb).isEmpty()) {
				return false;
			}
		}

		return true;
	}

}
