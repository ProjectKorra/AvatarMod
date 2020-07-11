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
package com.crowsofwar.avatar.common.entity.mob;

import net.minecraft.entity.EntityLivingBase;

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import java.util.*;

/**
 * @author CrowsOfWar
 */
public class BisonSummonHandler extends TickHandler {

	public BisonSummonHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {

		if (ctx.getWorld().isRemote) return false;

		//BendingData data = ctx.getData();

		//int cooldown = data.getMiscData().getPetSummonCooldown();
		//if (cooldown <= 0) {

		trySummonBison(ctx.getBenderEntity());
		return true;

		/*} else {

			data.getMiscData().setPetSummonCooldown(cooldown - 1);
			return false;
**/
	}

	private boolean trySummonBison(EntityLivingBase player) {

		List<EntitySkyBison> entities = player.world.getEntities(EntitySkyBison.class, bison -> bison.getOwner() == player);

		if (!entities.isEmpty()) {

			for (EntitySkyBison bison : entities) {
				Random random = new Random();

				// Find suitable location near player
				for (int i = 0; i < 10; i++) {

					double x = player.posX + (random.nextDouble() * 2 - 1) * 6;
					double y = player.posY + (random.nextDouble() * 2 - 1) * 3;
					double z = player.posZ + (random.nextDouble() * 2 - 1) * 6;

					bison.attemptTeleport(x, y, z);
					if (bison.attemptTeleport(x, y, z)) {
						return true;
					}

				}
			}
		}

		return false;

	}

}
