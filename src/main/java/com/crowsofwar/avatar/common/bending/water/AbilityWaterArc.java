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

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		WaterbendingState bendingState = (WaterbendingState) ctx.getData().getBendingState(controller());
		World world = ctx.getWorld();
		EntityPlayer player = ctx.getPlayerEntity();
		
		boolean needsSync = false;
		
		if (bendingState.getWaterArc(world) != null) {
			EntityWaterArc water = bendingState.getWaterArc(world);
			water.setGravityEnabled(true);
			bendingState.setWaterArc(null);
			needsSync = true;
		}
		
		VectorI targetPos = ctx.getClientLookBlock();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			if (lookAt == Blocks.WATER || lookAt == Blocks.FLOWING_WATER) {
				
				EntityWaterArc water = new EntityWaterArc(world);
				water.setOwner(player);
				water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
				water.setGravityEnabled(false);
				bendingState.setWaterArc(water);
				water.setDamageMult(1 + ctx.getData().getAbilityData(this).getXp() / 200);
				
				water.setBehavior(new WaterArcBehavior.PlayerControlled(water, player));
				
				world.spawnEntityInWorld(water);
				
				needsSync = true;
				
				ctx.getData().addStatusControl(StatusControl.THROW_WATER);
				ctx.getData().sync();
				
			}
		}
		
		if (needsSync) ctx.getData().sendBendingState(bendingState);
	}
	
	@Override
	public int getIconIndex() {
		return 4;
	}
	
}
