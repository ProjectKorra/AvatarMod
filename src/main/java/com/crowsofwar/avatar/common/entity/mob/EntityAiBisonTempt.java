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

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;
import static net.minecraft.item.ItemStack.field_190927_a;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * 
 * 
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
	}
	
	@Override
	public boolean shouldExecute() {
		
		List<EntityPlayer> players = bison.worldObj.getEntities(EntityPlayer.class, player -> {
			
			System.out.println("Player " + player);
			if (bison.getDistanceSqToEntity(player) > maxDistSq) return false;
			
			for (EnumHand hand : EnumHand.values()) {
				ItemStack stack = player.getHeldItem(hand);
				if (stack != field_190927_a) {
					if (MOBS_CONFIG.isBisonFood(stack.getItem())) {
						return true;
					}
				}
			}
			return false;
		});
		
		players.sort((p1, p2) -> {
			double d1 = bison.getDistanceSqToEntity(p1);
			double d2 = bison.getDistanceSqToEntity(p2);
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
		bison.getMoveHelper().setMoveTo(following.posX, following.posY, following.posZ, 1);
	}
	
	@Override
	public boolean continueExecuting() {
		if (bison.getDistanceSqToEntity(following) <= maxDistSq) {
			bison.getMoveHelper().setMoveTo(following.posX, following.posY, following.posZ, 1);
			return true;
		} else {
			following = null;
			return false;
		}
	}
	
}
