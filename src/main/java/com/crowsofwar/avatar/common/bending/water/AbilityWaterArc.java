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

package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

import java.util.List;
import java.util.function.BiPredicate;

import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityWaterArc extends WaterAbility {
	
	/**
	 * @param controller
	 */
	public AbilityWaterArc() {
		super("water_arc");
		requireRaytrace(-1, true);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		
		Vector targetPos = getClosestWaterBlock(entity, ctx.getLevel());
		
		if (targetPos != null || ctx.consumeWater(1)) {
			
			if (targetPos == null) {
				targetPos = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(4));
			}
			
			if (ctx.consumeChi(STATS_CONFIG.chiWaterArc)) {
				
				// Remove existing water arc
				
				AxisAlignedBB boundingBox = new AxisAlignedBB(entity.posX - 5, entity.posY - 5,
						entity.posZ - 5, entity.posX + 5, entity.posY + 5, entity.posZ + 5);
				
				List<EntityWaterArc> existing = world.getEntitiesWithinAABB(EntityWaterArc.class, boundingBox,
						arc -> arc.getOwner() == entity
								&& arc.getBehavior() instanceof WaterArcBehavior.PlayerControlled);
				
				for (EntityWaterArc arc : existing) {
					arc.setBehavior(new WaterArcBehavior.Thrown());
				}
				
				// Spawn new water arc
				
				EntityWaterArc water = new EntityWaterArc(world);
				water.setOwner(entity);
				water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
				water.setDamageMult(1 + ctx.getData().getAbilityData(this).getXp() / 200);
				
				water.setBehavior(new WaterArcBehavior.PlayerControlled());
				
				world.spawnEntityInWorld(water);
				
				ctx.getData().addStatusControl(StatusControl.THROW_WATER);
				
			}
		}
	}
	
	private Vector getClosestWaterBlock(EntityLivingBase entity, int level) {
		World world = entity.world;
		
		Vector eye = Vector.getEyePos(entity);
		
		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}
		
		double range = STATS_CONFIG.waterArcSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.waterArcAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterArcAngles; j++) {
				
				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterArcAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterArcAngles;
				
				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> state.getBlock() == Blocks.WATER
						|| state.getBlock() == Blocks.FLOWING_WATER;
				
				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}
				
			}
			
		}
		
		return null;
		
	}
	
	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiWaterArc(this, entity, bender);
	}
	
}
