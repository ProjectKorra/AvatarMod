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

import java.util.UUID;

import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityFireArc extends FireAbility {
	
	public static final UUID ID = UUID.fromString("48a755a4-5d4f-4b32-b299-093786dd93f3");
	
	/**
	 * @param controller
	 */
	public AbilityFireArc() {
		super("fire_arc");
		requireRaytrace(-1, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		if (ctx.consumeChi(STATS_CONFIG.chiFireArc)) {
			
			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			BendingData data = ctx.getData();
			
			Vector lookPos;
			if (ctx.isLookingAtBlock()) {
				lookPos = ctx.getLookPos();
			} else {
				Vector look = Vector.getLookRectangular(entity);
				lookPos = Vector.getEyePos(entity).plus(look.times(3));
			}
			
			EntityFireArc fire = new EntityFireArc(world);
			fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
			fire.setBehavior(new FireArcBehavior.PlayerControlled());
			fire.setOwner(entity);
			fire.setDamageMult(ctx.getLevel() >= 2 ? 2 : 1);
			fire.setCreateBigFire(ctx.isMasterLevel(AbilityTreePath.FIRST));
			
			world.spawnEntity(fire);
			
			data.addStatusControl(StatusControl.THROW_FIRE);
			
		}
		
	}
	
	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireArc(this, entity, bender);
	}
	
	@Override
	public UUID getId() {
		return ID;
	}
	
}
