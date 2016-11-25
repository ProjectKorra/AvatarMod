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
import com.crowsofwar.avatar.common.entity.EntityWave;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class AbilityCreateWave extends WaterAbility {
	
	private final Raytrace.Info raytrace;
	
	public AbilityCreateWave() {
		super("wave");
		this.raytrace = new Raytrace.Info();
	}
	
	@Override
	public void execute(AbilityContext data) {
		EntityPlayer player = data.getPlayerEntity();
		World world = data.getWorld();
		
		Vector look = Vector.fromEntityLook(player);
		look.setY(0);
		Raytrace.Result result = Raytrace.predicateRaytrace(world, Vector.getEntityPos(player).add(0, -1, 0),
				look, 4, (pos, blockState) -> blockState.getBlock() == Blocks.WATER);
		if (result.hitSomething()) {
			
			VectorI hitPos = result.getPos();
			IBlockState hitBlockState = world.getBlockState(hitPos.toBlockPos());
			
			EntityWave wave = new EntityWave(world);
			wave.setOwner(player);
			wave.velocity().set(look.times(10));
			wave.setPosition(hitPos.x() + 0.5, hitPos.y(), hitPos.z() + 0.5);
			wave.setDamageMultiplier(1 + data.getData().getAbilityData(this).getXp() / 100f);
			
			wave.rotationYaw = (float) look.toSpherical().toDegrees().y();
			
			world.spawnEntityInWorld(wave);
			
		}
		
	}
	
	@Override
	public int getIconIndex() {
		return 11;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
