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

import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityWave;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class AbilityCreateWave extends WaterAbility {
	
	public AbilityCreateWave() {
		super("wave");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		
		Vector look = Vector.getLookRectangular(entity);
		look.setY(0);
		Raytrace.Result result = Raytrace.predicateRaytrace(world, Vector.getEntityPos(entity).add(0, -1, 0),
				look, 4, (pos, blockState) -> blockState.getBlock() == Blocks.WATER);
		if (result.hitSomething()) {
			
			VectorI pos = result.getPos();
			IBlockState hitBlockState = world.getBlockState(pos.toBlockPos());
			IBlockState up = world.getBlockState(pos.toBlockPos().up());
			
			for (int i = 0; i < 3; i++) {
				if (world.getBlockState(pos.toBlockPos().up()).getBlock() == Blocks.AIR) {
					
					if (ctx.consumeChi(STATS_CONFIG.chiWave)) {
						
						EntityWave wave = new EntityWave(world);
						wave.setOwner(entity);
						wave.velocity().set(look.times(10));
						wave.setPosition(pos.x() + 0.5, pos.y(), pos.z() + 0.5);
						
						wave.setDamageMultiplier(ctx.getLevel() >= 1 ? 1.5f : 1);
						wave.setWaveSize(ctx.getLevel() >= 2 ? 3 : 2);
						if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
							wave.setWaveSize(5);
						}
						
						wave.rotationYaw = (float) Math.toDegrees(look.toSpherical().y());
						
						world.spawnEntityInWorld(wave);
						
						if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
							entity.startRiding(wave);
						}
						
					}
					
					break;
					
				}
				pos.add(0, 1, 0);
			}
			
		}
		
	}
	
	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiWave(this, entity, bender);
	}
	
}
