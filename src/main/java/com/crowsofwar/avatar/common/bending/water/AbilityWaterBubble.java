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

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityWaterBubble extends WaterAbility {
	
	public AbilityWaterBubble() {
		super("water_bubble");
		requireRaytrace(-1, false);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		
		if (ctx.isLookingAtBlock()) {
			BlockPos lookPos = ctx.getClientLookBlock().toBlockPos();
			IBlockState lookingAtBlock = world.getBlockState(lookPos);
			if (lookingAtBlock.getBlock() == Blocks.WATER) {
				
				if (ctx.consumeChi(STATS_CONFIG.chiWaterBubble)) {
					
					EntityWaterBubble existing = AvatarEntity.lookupEntity(world, EntityWaterBubble.class, //
							bub -> bub.getBehavior() instanceof WaterBubbleBehavior.PlayerControlled
									&& bub.getOwner() == entity);
					
					if (existing != null) {
						existing.setBehavior(new WaterBubbleBehavior.Drop());
						// prevent bubble from removing status control
						existing.setOwner(null);
					}
					
					Vector pos = ctx.getLookPos();
					
					EntityWaterBubble bubble = new EntityWaterBubble(world);
					bubble.setPosition(pos.x(), pos.y(), pos.z());
					bubble.setBehavior(new WaterBubbleBehavior.PlayerControlled());
					bubble.setOwner(entity);
					bubble.setSourceBlock(false);
					world.spawnEntityInWorld(bubble);
					
					data.addStatusControl(StatusControl.THROW_BUBBLE);
					world.setBlockToAir(lookPos);
					
				}
			}
		}
	}
	
}
