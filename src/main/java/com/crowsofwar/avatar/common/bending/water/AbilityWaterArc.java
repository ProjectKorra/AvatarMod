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

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		WaterbendingState bendingState = (WaterbendingState) ctx.getData().getBendingState(controller());
		World world = ctx.getWorld();
		EntityPlayer player = ctx.getPlayerEntity();
		
		Vector targetPos = getClosestWater(player);
		if (targetPos != null) {
			
			if (ctx.consumeChi(STATS_CONFIG.chiWaterArc)) {
				
				AxisAlignedBB boundingBox = new AxisAlignedBB(player.posX - 5, player.posY - 5,
						player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5);
				List<EntityWaterArc> existing = world.getEntitiesWithinAABB(EntityWaterArc.class, boundingBox,
						arc -> arc.getOwner() == player
								&& arc.getBehavior() instanceof WaterArcBehavior.PlayerControlled);
				
				for (EntityWaterArc arc : existing) {
					arc.setBehavior(new WaterArcBehavior.Thrown());
				}
				
				EntityWaterArc water = new EntityWaterArc(world);
				water.setOwner(player);
				water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
				water.setDamageMult(1 + ctx.getData().getAbilityData(this).getXp() / 200);
				
				water.setBehavior(new WaterArcBehavior.PlayerControlled());
				
				world.spawnEntityInWorld(water);
				
				ctx.getData().addStatusControl(StatusControl.THROW_WATER);
				ctx.getData().sync();
				
			}
		}
		
	}
	
	private Vector getClosestWater(EntityPlayer player) {
		World world = player.worldObj;
		
		Vector eye = Vector.getEyePos(player);
		
		double range = STATS_CONFIG.waterArcSearchRadius;
		for (int i = 0; i < STATS_CONFIG.waterArcAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.waterArcAngles; j++) {
				
				double yaw = player.rotationYaw + i * 360.0 / STATS_CONFIG.waterArcAngles;
				double pitch = player.rotationPitch + j * 360.0 / STATS_CONFIG.waterArcAngles;
				
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
	
}
