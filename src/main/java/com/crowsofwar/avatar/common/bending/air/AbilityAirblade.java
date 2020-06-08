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
package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirblade;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;

/**
 * @author CrowsOfWar
 */
public class AbilityAirblade extends Ability {

	public AbilityAirblade() {
		super(Airbending.ID, "airblade");
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		if (!bender.consumeChi(STATS_CONFIG.chiAirblade)) return;



		Vector look = Vector.getLookRectangular(entity);
		Vector spawnAt = Vector.getEyePos(entity).plus(look).minusY(0.6);

		AbilityData abilityData = ctx.getData().getAbilityData(this);
		float sizeMult = 1.0F;
		float damage = STATS_CONFIG.airbladeSettings.damage;
		damage *= abilityData.getXpModifier();
		damage *= ctx.getPowerRatingDamageMod();

		switch (ctx.getLevel()) {
			case -1:
			case 0:
				break;
			case 1:
				damage += 0.5F;
				sizeMult = 1.25F;
				break;
			case 2:
				damage += 1f;
				sizeMult = 1.5F;
				break;
		}
		if (ctx.isMasterLevel(SECOND)) {
			sizeMult = 4.0F;
			damage += 2F;
		}
		if (ctx.isMasterLevel(FIRST)) {
			damage += 2.5F;
			sizeMult = 1.5F;
		}

		float chopBlocks = -1;
		if (abilityData.getLevel() >= 1) {
			chopBlocks = 0;
		}
		if (ctx.isMasterLevel(SECOND)) {
			chopBlocks = 4;
		}

		if (ctx.isMasterLevel(FIRST)) {
			for (int i = 0; i < 5; i++) {
				float yaw = entity.rotationYaw - 30 + i * 15;
				Vector direction = Vector.toRectangular(Math.toRadians(yaw), Math.toRadians(entity.rotationPitch));
				EntityAirblade airblade = new EntityAirblade(world);
				airblade.setPosition(spawnAt.x(), spawnAt.y(), spawnAt.z());
				airblade.setAbility(new AbilityAirblade());
				airblade.setVelocity(direction.times(50));
				airblade.setDamage(damage);
				airblade.setElement(new Airbending());
				airblade.setSizeMult(sizeMult);
				airblade.rotationPitch = entity.rotationPitch;
				airblade.rotationYaw = yaw;
				airblade.setOwner(entity);
				airblade.setAbility(this);
				airblade.setPierceArmor(true);
				airblade.setChopBlocksThreshold(chopBlocks);
				if (!world.isRemote)
					world.spawnEntity(airblade);
			}
		} else {
			EntityAirblade airblade = new EntityAirblade(world);
			airblade.setPosition(spawnAt.x(), spawnAt.y(), spawnAt.z());
			airblade.setVelocity(look.times(ctx.getLevel() >= 1 ? 40 : 30));
			airblade.setDamage(damage);
			airblade.setSizeMult(sizeMult);
			airblade.rotationPitch = entity.rotationPitch;
			airblade.rotationYaw = entity.rotationYaw;
			airblade.setOwner(entity);
			airblade.setElement(new Airbending());
			airblade.setAbility(this);
			airblade.setPierceArmor(false);
			airblade.setChopBlocksThreshold(chopBlocks);
			airblade.setTier(getCurrentTier(abilityData.getLevel()));
			if (!world.isRemote)
				world.spawnEntity(airblade);
		}
		super.execute(ctx);

	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirblade(this, entity, bender);
	}

}
