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
package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.bending.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_LEFT_CLICK_DOWN;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowFireball extends StatusControl {

	public StatCtrlThrowFireball() {
		super(10, CONTROL_LEFT_CLICK_DOWN, LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 4F, 0.8F);

		EntityFireball fireball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, entity);
		List<EntityFireball> fireballs = world.getEntitiesWithinAABB(EntityFireball.class,
				entity.getEntityBoundingBox().grow(3.5, 3, 3.5));

		if (fireball != null) {
			AbilityData abilityData = ctx.getData().getAbilityData(new AbilityFireball());
			double speedMult = abilityData.getLevel() >= 2 ? 37.5 : 30;
			Vector lookPos = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(6 + fireball.getAvgSize()));
			fireball.setBehavior(new FireballBehavior.Thrown());
			fireball.rotationPitch = entity.rotationPitch;
			fireball.rotationYaw = entity.rotationYaw;

			Vector vel = lookPos.minus(Vector.getEntityPos(fireball));

			if (!fireballs.isEmpty()) {
				fireballs = fireballs.stream().filter(fireball1 -> !(fireball1.getBehavior() instanceof FireballBehavior.Thrown
						|| fireball1.getBehavior() instanceof AbilityFireball.FireballOrbitController)).collect(Collectors.toList());
				if (!fireballs.isEmpty()) {
					fireballs.get(0).setBehavior(new AbilityFireball.FireballOrbitController());
					for (EntityFireball ball : fireballs)
						ball.setOrbitID(ball.getOrbitID() - 1);
				}
				if (fireballs.size() > 1)
					fireball.setVelocity(vel.normalize().times(speedMult));
				else fireball.setVelocity(Vector.getLookRectangular(entity).times(speedMult));
			}
			else fireball.setVelocity(Vector.getLookRectangular(entity).times(speedMult));
		}


		return true;
	}

}
