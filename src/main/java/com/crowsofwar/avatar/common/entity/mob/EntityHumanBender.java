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

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.data.EntityBenderData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityHumanBender extends EntityCreature implements Bender, IRangedAttackMob {
	
	private EntityBenderData data;
	
	/**
	 * @param world
	 */
	public EntityHumanBender(World world) {
		super(world);
		data = new EntityBenderData(this);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.targetTasks.addTask(1,
				new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, player -> {
					// System.out.println("Considering " + player);
					return true;
				}));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAIAttackRanged(this, 6, 40, 10));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		data.readFromNbt(nbt);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		System.out.println(getAttackTarget());
		super.writeEntityToNBT(nbt);
		data.writeToNbt(nbt);
	}
	
	@Override
	public EntityLivingBase getEntity() {
		return this;
	}
	
	@Override
	public BendingData getData() {
		return data;
	}
	
	@Override
	public boolean isCreativeMode() {
		return false;
	}
	
	@Override
	public boolean isFlying() {
		return false;
	}
	
	@Override
	public boolean isPlayer() {
		return false;
	}
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		
		Vector rotations = getRotationTo(getEntityPos(this), getEntityPos(target));
		rotationYaw = (float) toDegrees(rotations.y());
		rotationPitch = (float) toDegrees(rotations.x());
		
		data.chi().setMaxChi(10);
		data.chi().setAvailableChi(10);
		
		BendingAbility ability = BendingAbility.ABILITY_AIR_GUST;
		Raytrace.Result raytrace = Raytrace.getTargetBlock(this, ability.getRaytrace());
		
		ability.execute(new AbilityContext(data, this, this, raytrace));
		
	}
	
}
