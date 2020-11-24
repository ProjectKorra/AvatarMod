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
package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

/**
 * @author CrowsOfWar
 */
public class AiWave extends BendingAi {

	protected AiWave(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
	}

	@Override
	protected boolean shouldExec() {

		EntityLivingBase target = entity.getAttackTarget();
		if (target != null && target.isInWater()) {

			return isAtEdgeOfWater();

		}

		return false;

	}

	@Override
	protected void startExec() {
		shouldContinueExecuting();
	}

	@Override
	public boolean shouldContinueExecuting() {

		EntityLivingBase target = entity.getAttackTarget();
		if (target != null && target.isInWater()) {
			entity.getLookHelper().setLookPosition(target.posX, target.posY, target.posZ, 10, 10);

			if (timeExecuting >= 40) {

				Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(target));
				entity.rotationYaw = (float) toDegrees(rotations.y());
				entity.rotationPitch = (float) toDegrees(rotations.x());

				execAbility();
				return false;

			}

			return true;

		}

		return false;

	}

	private boolean isAtEdgeOfWater() {

		World world = entity.world;
		Vector look = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()))
				.withY(0);

		Raytrace.Result result = Raytrace.predicateRaytrace(world, Vector.getEntityPos(entity)
						.minusY(1),
				look, 4, (pos, blockState) -> blockState.getBlock() == Blocks.WATER);
		if (result.hitSomething()) {

			VectorI pos = result.getPos();
			IBlockState hitBlockState = world.getBlockState(pos.toBlockPos());
			IBlockState up = world.getBlockState(pos.toBlockPos().up());

			for (int i = 0; i < 3; i++) {
				if (world.getBlockState(pos.toBlockPos().up()).getBlock() == Blocks.AIR) {
					return true;
				}
			}

		}
		return false;

	}

}
