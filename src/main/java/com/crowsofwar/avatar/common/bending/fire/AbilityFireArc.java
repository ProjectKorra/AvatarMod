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

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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

		if (data.hasStatusControl(StatusControl.THROW_FIRE)) {
			return;
		}

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

		EntityFireArc fire = AvatarEntity.lookupControlledEntity(ctx.getWorld(), EntityFireArc
				.class, ctx.getBenderEntity());

		if (fire != null) {
			fire.setBehavior(new FireArcBehavior.Thrown());
		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireArc(this, entity, bender);
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();

		int coolDown = 100;

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		if (ctx.getLevel() == 1) {
			coolDown = 90;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 80;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 70;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 60;
		}
		return coolDown;
	}

}
