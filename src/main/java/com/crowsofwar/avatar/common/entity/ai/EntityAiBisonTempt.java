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
package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;
import static net.minecraft.item.ItemStack.EMPTY;

/**
 * @author CrowsOfWar
 */
public class EntityAiBisonTempt extends EntityAIBase {

	private final EntitySkyBison bison;
	private final double maxDistSq;
	private EntityPlayer following;

	public EntityAiBisonTempt(EntitySkyBison bison, double maxDist) {
		this.bison = bison;
		this.maxDistSq = maxDist * maxDist;
		this.following = null;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {

		if (bison.getCondition().getFoodPoints() >= 25) return false;

		List<EntityPlayer> players = bison.world.getEntities(EntityPlayer.class, player -> {
			if (bison.getDistanceSq(player) > maxDistSq) return false;
			return isHoldingFood(player);
		});

		players.sort((p1, p2) -> {
			double d1 = bison.getDistanceSq(p1);
			double d2 = bison.getDistanceSq(p2);
			return d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
		});

		if (!players.isEmpty()) {
			following = players.get(0);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void startExecuting() {
		bison.getMoveHelper().setMoveTo(following.posX,
				following.posY + following.eyeHeight - bison.getEyeHeight(), following.posZ, 1);
	}

	@Override
	public boolean shouldContinueExecuting() {

		if (!following.isDead && bison.getDistanceSq(following) <= maxDistSq
				&& isHoldingFood(following)) {

			bison.getMoveHelper().setMoveTo(following.posX,
					following.posY + following.eyeHeight - bison.getEyeHeight(), following.posZ, 1);
			return true;

		} else {
			following = null;
			bison.getMoveHelper().action = Action.WAIT;
			return false;
		}
	}

	private boolean isHoldingFood(EntityPlayer player) {
		for (EnumHand hand : EnumHand.values()) {
			ItemStack stack = player.getHeldItem(hand);
			if (stack != EMPTY) {
				if (MOBS_CONFIG.isBisonFood(stack.getItem())) {
					return true;
				}
			}
		}
		return false;
	}

}
