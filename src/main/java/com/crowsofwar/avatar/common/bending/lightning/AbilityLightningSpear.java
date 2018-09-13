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
package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.lightning.StatCtrlThrowLightningSpear.THROW_LIGHTNINGSPEAR;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

/**
 * @author CrowsOfWar
 */
public class AbilityLightningSpear extends Ability {

	public AbilityLightningSpear() {
		super(Lightningbending.ID, "lightning_spear");
		requireRaytrace(-1, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();
		AbilityData abilityData = ctx.getAbilityData();

		if (data.hasStatusControl(THROW_LIGHTNINGSPEAR)) return;

		if (bender.consumeChi(STATS_CONFIG.chiLightningSpear)) {


			float damage = 2F;
			if (abilityData.getLevel() >= 2) {
				damage = 8;
			}
			float size = 1.2F;

			if (ctx.getLevel() == 1) {
				size = 1.4F;
				damage = 3;
			}

			if (ctx.getLevel() == 2) {
				size = 1.6F;
				damage = 4;
			}

			if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
				size =  1.2F;
				damage = 6;
			}

			if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
				size = 2.2F;
				damage = 5;
			}

			damage *= ctx.getPowerRatingDamageMod();

			EntityLightningSpear spear = new EntityLightningSpear(world);
			spear.setSize(size);

			Vector target;
			if (ctx.isLookingAtBlock()) {
				target = ctx.getLookPos();
			} else {
				Vector playerPos = getEyePos(entity);
				target = playerPos.plus(getLookRectangular(entity).times(spear.getSize()));
			}


			spear.setPosition(target);
			spear.setOwner(entity);
			spear.setBehavior(new LightningSpearBehavior.PlayerControlled());
			spear.setDamage(damage);
			spear.rotationPitch = entity.rotationPitch;
			spear.rotationYaw = entity.rotationYaw;
			spear.setPiercing(abilityData.isMasterPath(AbilityTreePath.FIRST));
			spear.setAbility(this);
			spear.setDegreesPerSecond(400);
			spear.setGroupAttack(abilityData.isMasterPath(AbilityTreePath.SECOND));
			world.spawnEntity(spear);

			data.addStatusControl(THROW_LIGHTNINGSPEAR);

		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiLightningSpear(this, entity, bender);
	}

}
