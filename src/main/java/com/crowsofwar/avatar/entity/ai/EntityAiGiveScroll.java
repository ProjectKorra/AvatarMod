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
package com.crowsofwar.avatar.entity.ai;

import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.item.scroll.Scrolls;
import com.crowsofwar.avatar.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

/**
 * @author CrowsOfWar
 */
public class EntityAiGiveScroll extends EntityAIBase {

	private final EntityLiving entity;
	private final ScrollType scrollType;
	private EntityLivingBase target;
	private int ticksExecuting;
	private int level;

	public EntityAiGiveScroll(EntityLiving entity, ScrollType scrollType, int level) {
		this.entity = entity;
		this.scrollType = scrollType;
		this.level = level;
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
	public boolean shouldContinueExecuting() {

		entity.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ,
				entity.getHorizontalFaceSpeed(), entity.getVerticalFaceSpeed());
		System.out.println("Thonk");

		ticksExecuting++;
		if (ticksExecuting >= 10) {
			System.out.println("Huh");

			World world = entity.world;

			Vector velocity = getEntityPos(target).minus(getEntityPos(entity)).normalize().times(0.3);
			ItemStack scrollStack = new ItemStack(Scrolls.getItemForType(scrollType), 1, level);

			EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY + entity.getEyeHeight(),
					entity.posZ, scrollStack);
			entityItem.setDefaultPickupDelay();
			entityItem.motionX = velocity.x();
			entityItem.motionY = velocity.y();
			entityItem.motionZ = velocity.z();
			world.spawnEntity(entityItem);

			target = null;

			AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onNpcTrade());

		}

		return shouldExecute();

	}

}
