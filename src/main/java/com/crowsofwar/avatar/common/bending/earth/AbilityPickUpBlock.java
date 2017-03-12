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

package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import java.util.Random;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityPickUpBlock extends EarthAbility {
	
	private final Random random;
	
	public AbilityPickUpBlock() {
		super("pickup_block");
		this.random = new Random();
		requireRaytrace(-1, true);
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		
		BendingData data = ctx.getData();
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		
		EntityFloatingBlock currentBlock = AvatarEntity.lookupEntity(ctx.getWorld(),
				EntityFloatingBlock.class,
				fb -> fb.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled
						&& fb.getOwner() == ctx.getBenderEntity());
		
		if (currentBlock != null) {
			currentBlock.drop();
			data.removeStatusControl(StatusControl.THROW_BLOCK);
			data.removeStatusControl(StatusControl.PLACE_BLOCK);
		} else {
			VectorI target = ctx.verifyClientLookBlock(-1, 5);
			if (target != null) {
				IBlockState ibs = world.getBlockState(target.toBlockPos());
				Block block = ibs.getBlock();
				if (STATS_CONFIG.bendableBlocks.contains(block)) {
					
					if (ctx.consumeChi(STATS_CONFIG.chiPickUpBlock)) {
						
						AbilityData abilityData = data.getAbilityData(this);
						float xp = abilityData.getTotalXp();
						
						EntityFloatingBlock floating = new EntityFloatingBlock(world, ibs);
						floating.setPosition(target.x() + 0.5, target.y(), target.z() + 0.5);
						floating.setItemDropsEnabled(!bender.isCreativeMode());
						
						double dist = 2.5;
						Vector force = new Vector(0, Math.sqrt(19.62 * dist), 0);
						floating.velocity().add(force);
						floating.setBehavior(new FloatingBlockBehavior.PickUp());
						floating.setOwner(entity);
						floating.setDamageMult(.75f + xp / 100);
						
						world.spawnEntityInWorld(floating);
						
						world.setBlockState(target.toBlockPos(), Blocks.AIR.getDefaultState());
						
						controller().post(new FloatingBlockEvent.BlockPickedUp(floating, entity));
						
						data.addStatusControl(StatusControl.PLACE_BLOCK);
						data.addStatusControl(StatusControl.THROW_BLOCK);
						
					}
					
				} else {
					world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_LEVER_CLICK,
							SoundCategory.PLAYERS, 1, (float) (random.nextGaussian() / 0.25 + 0.375));
				}
				
			}
		}
	}
	
}
