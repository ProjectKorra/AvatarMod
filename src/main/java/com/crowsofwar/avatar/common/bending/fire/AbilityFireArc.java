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

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityFireArc extends FireAbility {
	
	/**
	 * @param controller
	 */
	public AbilityFireArc() {
		super("fire_arc");
		requireRaytrace(-1, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		FirebendingState fs = (FirebendingState) ctx.getData().getBendingState(controller());
		AvatarPlayerData data = ctx.getData();
		
		Vector lookPos;
		if (ctx.isLookingAtBlock()) {
			lookPos = ctx.getLookPos();
		} else {
			Vector look = Vector.getLookRectangular(player);
			lookPos = Vector.getEyePos(player).plus(look.times(3));
		}
		
		EntityFireArc fire = new EntityFireArc(world);
		fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
		fire.setBehavior(new FireArcBehavior.PlayerControlled(fire, player));
		fire.setOwner(player);
		fire.setDamageMult(0.75f + ctx.getData().getAbilityData(this).getXp() / 100);
		
		world.spawnEntityInWorld(fire);
		
		fs.setFireArc(fire);
		data.sendBendingState(fs);
		
		data.addStatusControl(StatusControl.THROW_FIRE);
		data.sync();
		
	}
	
}
