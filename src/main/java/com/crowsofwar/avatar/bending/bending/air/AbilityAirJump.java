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

package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.util.data.StatusControlController.AIR_JUMP;
import static com.crowsofwar.avatar.util.data.TickHandlerController.AIR_PARTICLE_SPAWNER;

/**
 * @author CrowsOfWar
 */
public class AbilityAirJump extends Ability {

	public AbilityAirJump() {
		super(Airbending.ID, "air_jump");
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void init() {
		super.init();
		addProperties(JUMP_HEIGHT, JUMPS, KNOCKBACK, EFFECT_RADIUS, PERFORMANCE, FALL_ABSORPTION, XP_HIT, POWERRATING, DURATION, CHI_HIT);
		addBooleanProperties(GROUND_POUND);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getAbilityData();

		boolean allowDoubleJump = abilityData.getUseNumber() < getProperty(JUMPS, ctx).intValue();
		List<AxisAlignedBB> collideWithGround = world.getCollisionBoxes(entity, entity.getEntityBoundingBox().grow(0.2, 0.5, 0.2));
		boolean onGround = !collideWithGround.isEmpty() || entity.collidedVertically || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WEB;

		if (!data.hasStatusControl(AIR_JUMP) && bender.consumeChi(getChiCost(ctx) / 4)) {
			data.addStatusControl(AIR_JUMP);
			if (data.hasTickHandler(AIR_PARTICLE_SPAWNER) || allowDoubleJump && !onGround) {
				Raytrace.Result raytrace = Raytrace.getTargetBlock(ctx.getBenderEntity(), -1);
				if (AIR_JUMP.execute(new BendingContext(data, ctx.getBenderEntity(), ctx.getBender(), raytrace))) {
					if (!ctx.getWorld().isRemote)
						data.removeStatusControl(AIR_JUMP);
				}
			}
		}
		abilityData.setRegenBurnout(true);
		super.execute(ctx);
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getBurnOut(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getExhaustion(AbilityContext ctx) {
		return 0;
	}
}
