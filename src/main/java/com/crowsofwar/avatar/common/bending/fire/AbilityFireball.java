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

import java.util.UUID;

import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityFireball extends FireAbility {
	
	public static final UUID ID = UUID.fromString("c6d1a13e-06fd-4d58-be21-f187995717c0");
	
	public AbilityFireball() {
		super("fireball");
		requireRaytrace(2.5, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();
		
		if (data.hasStatusControl(StatusControl.THROW_FIREBALL)) return;
		
		if (ctx.consumeChi(STATS_CONFIG.chiFireball)) {
			
			Vector target;
			if (ctx.isLookingAtBlock()) {
				target = ctx.getLookPos();
			} else {
				Vector playerPos = getEyePos(entity);
				target = playerPos.plus(getLookRectangular(entity).times(2.5));
			}
			
			float xp = data.getAbilityData(ID).getTotalXp();
			float damage = STATS_CONFIG.fireballSettings.damage;
			damage *= ctx.getLevel() >= 2 ? 2.5f : 1f;
			
			EntityFireball fireball = new EntityFireball(world);
			fireball.position().set(target);
			fireball.setOwner(entity);
			fireball.setBehavior(new FireballBehavior.PlayerControlled());
			fireball.setDamage(damage);
			if (ctx.isMasterLevel(AbilityTreePath.SECOND)) fireball.setSize(20);
			world.spawnEntity(fireball);
			
			data.addStatusControl(StatusControl.THROW_FIREBALL);
			
		}
		
	}
	
	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireball(this, entity, bender);
	}
	
	@Override
	public UUID getId() {
		return ID;
	}
	
}
