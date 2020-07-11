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
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.EntityWave;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

public class AbilityCreateWave extends Ability {

	public AbilityCreateWave() {
		super(Waterbending.ID, "wave");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();

		Vector look = Vector.getLookRectangular(entity);
		Raytrace.Result result = Raytrace.predicateRaytrace(world, Vector.getEntityPos(entity).minusY(1)
				, look, 4 + ctx.getLevel(), (pos, blockState) -> blockState.getBlock() == Blocks
						.WATER);
		/*Raytrace.Result extraResult = Raytrace.predicateRaytrace(world, Vector.getEntityPos(entity).minusY(1), look,
				4 + ctx.getLevel(), (blockPos, iBlockState) -> iBlockState.getBlock() == Blocks.SNOW || iBlockState.getBlock() == Blocks.FLOWING_WATER
						|| iBlockState.getBlock() == Blocks.ICE);**/

		/*Raytrace.Result rayTraceResult = Raytrace.getTargetBlock(entity, 4 + ctx.getLevel(), true);

		if (rayTraceResult.hitSomething() && rayTraceResult.getPos() != null) {
			Block hitBlock = world.getBlockState(rayTraceResult.getPos().toBlockPos()).getBlock();
			if (STATS_CONFIG.waterBendableBlocks.contains(hitBlock)) {
				VectorI pos = rayTraceResult.getPos();
				assert pos != null;
				IBlockState up = world.getBlockState(pos.toBlockPos().up());
				if (up.getBlock() == Blocks.AIR) {
					if (bender.consumeChi(STATS_CONFIG.chiWave)) {

						float size = 2;
						double speed = 6.5;
						if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
							speed = 12.5;
							size = 5F;
						}
						if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
							speed = 17;
							size = 2.75F;
						}
						if (ctx.getLevel() == 1) {
							size = 2.5F;
							speed = 8;
						}
						if (ctx.getLevel() == 2) {
							size = 3;
							speed = 10;
						}

						size += ctx.getPowerRating() / 100;

						speed += ctx.getPowerRating() / 100 * 8;

						EntityWave wave = new EntityWave(world);
						wave.setOwner(entity);
						wave.setVelocity(look.times(speed));
						wave.setPosition(pos.x(), pos.y(), pos.z());
						wave.setAbility(this);
						wave.rotationYaw = (float) Math.toDegrees(look.toSpherical().y());

						float damageMult = ctx.getLevel() >= 1 ? 1.5f : 1;
						damageMult *= ctx.getPowerRatingDamageMod();
						wave.setDamageMultiplier(damageMult);
						wave.setWaveSize(size);

						wave.setCreateExplosion(ctx.isMasterLevel(AbilityTreePath.SECOND));
						world.spawnEntity(wave);

					}
				}
			}

		}**/
		if (result.hitSomething()) {

			VectorI pos = result.getPos();
			//IBlockState hitBlockState = world.getBlockState(pos.toBlockPos());
			assert pos != null;
			IBlockState up = world.getBlockState(pos.toBlockPos().up());

			for (int i = 0; i < 3; i++) {
				if (up.getBlock() == Blocks.AIR) {

					if (bender.consumeChi(STATS_CONFIG.chiWave)) {

						float size = 2;
						double speed = 6.5;
						if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
							speed = 12.5;
							size = 5F;
						}
						if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
							speed = 17;
							size = 2.75F;
						}
						if (ctx.getLevel() == 1) {
							size = 2.5F;
							speed = 8;
						}
						if (ctx.getLevel() == 2) {
							size = 3;
							speed = 10;
						}

						size += ctx.getPowerRating() / 100;

						speed += ctx.getPowerRating() / 100 * 8;

						EntityWave wave = new EntityWave(world);
						wave.setOwner(entity);
						wave.setVelocity(look.times(speed));
						wave.setPosition(pos.x(), pos.y(), pos.z());
						wave.setAbility(this);
						wave.rotationYaw = (float) Math.toDegrees(look.toSpherical().y());

						float damageMult = ctx.getLevel() >= 1 ? 1.5f : 1;
						damageMult *= ctx.getPowerRatingDamageMod();
						wave.setDamageMultiplier(damageMult);
						wave.setWaveSize(size);

						wave.setCreateExplosion(ctx.isMasterLevel(AbilityTreePath.SECOND));
						world.spawnEntity(wave);

					}

					break;

				}
				pos.add(0, 1, 0);
			}

		} /*else if (ctx.consumeWater(2)) {
			for (int i = 0; i < 3; i++) {
				if (bender.consumeChi(STATS_CONFIG.chiWave)) {

					float size = 2;
					double speed = 6.5;
					if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
						speed = 12.5;
						size = 5;
					}
					if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
						speed = 17;
						size = 2.75F;
					}
					if (ctx.getLevel() == 1) {
						size = 2.5F;
						speed = 8;
					}
					if (ctx.getLevel() == 2) {
						size = 3;
						speed = 10;
					}

					size += ctx.getPowerRating() / 100;

					speed += ctx.getPowerRating() / 100 * 8;

					Vector direction = Vector.getLookRectangular(entity).withY(0);
					EntityWave wave = new EntityWave(world);
					wave.setOwner(entity);
					wave.setVelocity(direction.times(speed));
					wave.setPosition(direction.minusY(1));
					wave.setAbility(this);
					wave.rotationYaw = (float) Math.toDegrees(look.toSpherical().y());

					float damageMult = ctx.getLevel() >= 1 ? 1.5f : 1;
					damageMult *= ctx.getPowerRatingDamageMod();
					wave.setDamageMultiplier(damageMult);
					wave.setWaveSize(size);

					wave.setCreateExplosion(ctx.isMasterLevel(AbilityTreePath.SECOND));
					world.spawnEntity(wave);

				}

			}
		}**/

	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiWave(this, entity, bender);
	}

}
