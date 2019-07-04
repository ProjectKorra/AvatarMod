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

package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireShot extends Ability {

	private final ParticleSpawner particles;

	public AbilityFireShot() {
		super(Firebending.ID, "fire_shot");
		requireRaytrace(-1, false);
		particles = new NetworkParticleSpawner();
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();

		//VectorI looking = ctx.getLookPosI();
		//EnumFacing side = ctx.getLookSide();
		float speed = 0.5F;
		if (ctx.getLevel() == 1) {
			speed += 0.25F;
		}
		if (ctx.getLevel() == 2) {
			speed += 0.5F;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			speed += 0.75F;
		}
		if (bender.consumeChi(1)) {
			if (!ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				EntityFlames flames = new EntityFlames(world);
				flames.setVelocity(entity.getLookVec().scale(speed));
				flames.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight(), 0).add(entity.getLookVec().scale(0.05)));
				flames.setOwner(entity);
				flames.setReflect(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				flames.rotationPitch = entity.rotationPitch;
				flames.rotationYaw = entity.rotationYaw;
				flames.setAbility(new AbilityFireShot());
				flames.setTrailingFire(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				flames.setDamageMult(bender.getDamageMult(Firebending.ID));
				world.spawnEntity(flames);
			}
			else {
				EntityShockwave wave = new EntityShockwave(world);
				wave.setOwner(entity);
				wave.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0));
				wave.setFireTime(10);
				wave.setElement(new Firebending());
				wave.setAbility(this);
				wave.setParticle(AvatarParticles.getParticleFlames());
				wave.setDamage(5F);
				wave.setPerformanceAmount(15);
				wave.setSpeed(0.4F);
				wave.setKnockbackMult(new Vec3d(1.6, 1, 1.6));
				wave.setKnockbackHeight(0.15);
				wave.setParticleSpeed(0.2F);
				wave.setParticleAmount(8);
				world.spawnEntity(wave);
			}
		}

		/*if (ctx.isLookingAtBlock()) {
			if (looking != null) {
				VectorI setAt = new VectorI(looking.x(), looking.y(), looking.z());
				setAt.offset(side);
				BlockPos blockPos = setAt.toBlockPos();

				double chance = 20 * ctx.getLevel() + 40;
				chance += ctx.getPowerRating() / 10;

				if (ctx.isDynamicMasterLevel(AbilityTreePath.FIRST)) {

					int yaw = (int) floor((ctx.getBenderEntity().rotationYaw * 8 / 360) + 0.5) & 7;
					int x = 0, z = 0;
					if (yaw == 1 || yaw == 2 || yaw == 3) x = -1;
					if (yaw == 5 || yaw == 6 || yaw == 7) x = 1;
					if (yaw == 3 || yaw == 4 || yaw == 5) z = -1;
					if (yaw == 0 || yaw == 1 || yaw == 7) z = 1;

					if (spawnFire(world, blockPos, ctx, true, chance)) {
						for (int i = 1; i < 5; i++) {
							spawnFire(world, blockPos.add(x * i, 0, z * i), ctx, false, 100);
						}
						ctx.getAbilityData().addXp(SKILLS_CONFIG.litFire);
					}

				} else if (ctx.isDynamicMasterLevel(AbilityTreePath.SECOND)) {

					if (spawnFire(world, blockPos, ctx, true, chance)) {
						spawnFire(world, blockPos.add(1, 0, 0), ctx, false, 100);
						spawnFire(world, blockPos.add(-1, 0, 0), ctx, false, 100);
						spawnFire(world, blockPos.add(0, 0, 1), ctx, false, 100);
						spawnFire(world, blockPos.add(0, 0, -1), ctx, false, 100);
						ctx.getAbilityData().addXp(SKILLS_CONFIG.litFire);
					}



				} else {
					if (spawnFire(world, blockPos, ctx, true, chance)) {
						ctx.getAbilityData().addXp(SKILLS_CONFIG.litFire);
					}
				}

			}
		}**/
	}

	private boolean spawnFire(World world, BlockPos blockPos, AbilityContext ctx, boolean useChi,
							  double chance) {
		EntityLivingBase entity = ctx.getBenderEntity();


		if (world.isRainingAt(blockPos) && ctx.getLookPos() != null) {

			particles.spawnParticles(world, EnumParticleTypes.CLOUD, 3, 7, ctx.getLookPos(),
					new Vector(0.5f, 0.75f, 0.5f));
			world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
					SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS,
					0.4f + (float) Math.random() * 0.2f, 0.9f + (float) Math.random() * 0.2f);

		} else {
			if (world.getBlockState(blockPos).getBlock() == Blocks.AIR
					&& Blocks.FIRE.canPlaceBlockAt(world, blockPos)) {

				if (!useChi || ctx.getBender().consumeChi(STATS_CONFIG.chiLightFire)) {

					double random = Math.random() * 100;

					if (random < chance || (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())) {

						world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
						world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
								SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS,
								0.7f + (float) Math.random() * 0.3f, 0.9f + (float) Math.random() * 0.2f);

						return true;

					} else {

						ctx.getBender().sendMessage("avatar.ability.fire_shot.fail");

					}

				}

			}
		}

		return false;

	}
}
