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

package com.crowsofwar.avatar.common.bending.fire;

import net.minecraft.entity.*;
import net.minecraft.world.World;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireArc extends Ability {

	public AbilityFireArc() {
		super(Firebending.ID, "fire_arc");
		requireRaytrace(-1, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();

		if (bender.consumeChi(STATS_CONFIG.chiFireArc)) {

			Vector lookPos;
			if (ctx.isLookingAtBlock()) {
				lookPos = ctx.getLookPos();
			} else {
				Vector look = Vector.getLookRectangular(entity);
				lookPos = Vector.getEyePos(entity).plus(look.times(3));
			}

			removeExisting(ctx);

			float damageMult = ctx.getLevel() >= 2 ? 2 : 1;
			damageMult *= ctx.getPowerRatingDamageMod();

			List<Entity> fireArc = Raytrace.entityRaytrace(world, Vector.getEntityPos(entity).withY(entity.getEyeHeight()),
														   Vector.getLookRectangular(entity).times(10), 3, entity1 -> entity1 != entity);

			if (fireArc.isEmpty()) {
				if (ctx.getLevel() >= 2) {
					for (Entity a : fireArc) {
						if (a instanceof AvatarEntity) {
							if (((AvatarEntity) a).getOwner() != entity) {
								if (a instanceof EntityFireArc) {
									((EntityFireArc) a).setOwner(entity);
									((EntityFireArc) a).setBehavior(new FireArcBehavior.PlayerControlled());
									((EntityFireArc) a).setAbility(this);
									((EntityFireArc) a).setPosition(Vector.getLookRectangular(entity).times(1.5F));
									((EntityFireArc) a).setDamageMult(damageMult);
								}
							}
						}
					}
				}
			}

			EntityFireArc fire = new EntityFireArc(world);
			if (lookPos != null) {
				fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
				fire.setBehavior(new FireArcBehavior.PlayerControlled());
				fire.setOwner(entity);
				fire.setDamageMult(damageMult);
				fire.setCreateBigFire(ctx.isMasterLevel(AbilityTreePath.FIRST));
				fire.setAbility(this);
				world.spawnEntity(fire);

				data.addStatusControl(StatusControl.THROW_FIRE);
			}

		}

	}

	/**
	 * Kills already existing fire arc if there is one
	 */
	private void removeExisting(AbilityContext ctx) {

		EntityFireArc fire = AvatarEntity.lookupControlledEntity(ctx.getWorld(), EntityFireArc.class, ctx.getBenderEntity());

		if (fire != null) {
			fire.setBehavior(new FireArcBehavior.Thrown());
		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireArc(this, entity, bender);
	}
}
