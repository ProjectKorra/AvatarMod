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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityFireball extends FireAbility {
	
	public AbilityFireball() {
		super("fireball");
		requireRaytrace(2.5, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		AvatarPlayerData data = ctx.getData();
		
		Vector target;
		if (ctx.isLookingAtBlock()) {
			target = ctx.getLookPos();
		} else {
			Vector playerPos = getEyePos(player);
			target = playerPos.plus(getLookRectangular(player).times(2.5));
		}
		
		float xp = data.getAbilityData(this).getXp();
		float damage = STATS_CONFIG.fireballSettings.damage;
		damage *= .75 + xp * .0075f; // 0=.75, 100=1.5
		
		EntityFireball fireball = new EntityFireball(world);
		fireball.position().set(target);
		fireball.setOwner(player);
		fireball.setBehavior(new FireballBehavior.PlayerControlled());
		fireball.setDamage(damage);
		world.spawnEntityInWorld(fireball);
		
		data.addStatusControl(StatusControl.THROW_FIREBALL);
		data.sync();
		
	}
	
	@Override
	public int getIconIndex() {
		return 15;
	}
	
}
