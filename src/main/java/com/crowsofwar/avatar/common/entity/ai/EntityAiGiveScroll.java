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

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAiGiveScroll extends EntityAIBase {
	
	private final EntityLiving entity;
	private final ScrollType scrollType;
	private EntityLivingBase target;
	private int ticksExecuting;
	
	public EntityAiGiveScroll(EntityLiving entity, ScrollType scrollType) {
		this.entity = entity;
		this.scrollType = scrollType;
		setMutexBits(1);
	}
	
	/**
	 * Starts giving scroll to the target and returns true. However, if this is
	 * already executing, rejects and returns false.
	 */
	public boolean giveScrollTo(EntityLivingBase player) {
		if (target == null || !target.isEntityAlive()) {
			this.target = player;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean shouldExecute() {
		return target != null && target.isEntityAlive() && entity.getAttackTarget() == null;
	}
	
	@Override
	public void startExecuting() {
		ticksExecuting = 0;
		entity.getLookHelper().setLookPositionWithEntity(target, 10, 10);
	}
	
	@Override
	public boolean continueExecuting() {
		
		entity.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ,
				entity.getHorizontalFaceSpeed(), entity.getVerticalFaceSpeed());
		
		ticksExecuting++;
		if (ticksExecuting >= 50) {
			
			World world = entity.worldObj;
			
			Vector velocity = getEntityPos(target).minus(getEntityPos(entity)).normalize().times(0.3);
			ItemStack scrollStack = new ItemStack(AvatarItems.itemScroll, 1, scrollType.id());
			
			EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY + entity.getEyeHeight(),
					entity.posZ, scrollStack);
			entityItem.setDefaultPickupDelay();
			entityItem.motionX = velocity.x();
			entityItem.motionY = velocity.y();
			entityItem.motionZ = velocity.z();
			world.spawnEntityInWorld(entityItem);
			
			target = null;
			
		}
		
		return shouldExecute();
		
	}
	
}
