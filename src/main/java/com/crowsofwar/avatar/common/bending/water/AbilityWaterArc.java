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

import java.util.List;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
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
		requireRaytrace(-1, false);
		getRaytrace().setPredicate(
				(pos, state) -> state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		WaterbendingState bendingState = (WaterbendingState) ctx.getData().getBendingState(controller());
		World world = ctx.getWorld();
		EntityPlayer player = ctx.getPlayerEntity();
		
		VectorI targetPos = ctx.getClientLookBlock();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			if (lookAt == Blocks.WATER || lookAt == Blocks.FLOWING_WATER) {
				
				if (ctx.consumeChi(STATS_CONFIG.chiWaterArc)) {
					
					AxisAlignedBB boundingBox = new AxisAlignedBB(player.posX - 5, player.posY - 5,
							player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5);
					List<EntityWaterArc> existing = world.getEntitiesWithinAABB(EntityWaterArc.class,
							boundingBox, arc -> arc.getOwner() == player
									&& arc.getBehavior() instanceof WaterArcBehavior.PlayerControlled);
					
					for (EntityWaterArc arc : existing) {
						arc.setBehavior(new WaterArcBehavior.Thrown());
					}
					
					EntityWaterArc water = new EntityWaterArc(world);
					water.setOwner(player);
					water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
					water.setDamageMult(1 + ctx.getData().getAbilityData(this).getTotalXp() / 200);
					
					water.setBehavior(new WaterArcBehavior.PlayerControlled());
					
					world.spawnEntityInWorld(water);
					
					ctx.getData().addStatusControl(StatusControl.THROW_WATER);
					ctx.getData().sync();
					
				}
				
			}
		}
		
	}
	
}
