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
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.blocks.BlockTemp;
import com.crowsofwar.avatar.common.blocks.BlockUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.ShockwaveBehaviour;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireShot extends Ability {

	static HashMap<BlockPos, Long> ignitedTimes = new HashMap<>();

	public AbilityFireShot() {
		super(Firebending.ID, "fire_shot");
		requireRaytrace(-1, false);
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
		AbilityData abilityData = ctx.getAbilityData();

		float speed = 0.5F;
		double damageMult = bender.getDamageMult(Firebending.ID);
		float damage = STATS_CONFIG.fireShotSetttings.damage;
		float chi = STATS_CONFIG.chiFireShot;
		if (ctx.getLevel() == 1) {
			speed += 0.25F;
			chi += 0.5F;
			damageMult += 0.5;
			damage += 2;
		}
		if (ctx.getLevel() == 2) {
			speed += 0.5F;
			chi += 1;
			damageMult += 1;
			damage += 4;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			speed += 0.75F;
			chi += 1.5F;
			damageMult += 2;
			damage += 7;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi += 2F;
		}
		damage += abilityData.getTotalXp() / 50;
		if (bender.consumeChi(chi)) {
			if (!ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				EntityFlames flames = new EntityFlames(world);
				flames.setVelocity(entity.getLookVec().scale(speed));
				flames.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight(), 0).add(entity.getLookVec().scale(0.05)));
				flames.setOwner(entity);
				flames.setReflect(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				flames.rotationPitch = entity.rotationPitch;
				flames.rotationYaw = entity.rotationYaw;
				flames.setAbility(new AbilityFireShot());
				flames.setLifeTime((int) abilityData.getTotalXp() + 60);
				flames.setTrailingFire(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				//TODO: Remove all damage calculations in EntityFlames
				flames.setFireTime((int) (4F * 1 + abilityData.getTotalXp() / 50f));
				flames.setDamage(damage * (float) damageMult);
				if (!world.isRemote)
					world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.75F +
							world.rand.nextFloat(), 0.5F + world.rand.nextFloat(), false);
				world.spawnEntity(flames);
			} else {
				//TODO: Fix particle spawning
				EntityShockwave wave = new EntityShockwave(world);
				wave.setOwner(entity);
				wave.rotationPitch = entity.rotationPitch;
				wave.rotationYaw = entity.rotationYaw;
				wave.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0));
				wave.setFireTime(10);
				wave.setElement(new Firebending());
				wave.setAbility(this);
				wave.setParticle(AvatarParticles.getParticleFlames());
				wave.setDamage(5F);
				wave.setPerformanceAmount(15);
				wave.setBehaviour(new FireShockwaveBehaviour());
				wave.setSpeed(0.4F);
				wave.setKnockbackMult(new Vec3d(1.5, 1, 1.5));
				wave.setKnockbackHeight(0.15);
				wave.setParticleSpeed(0.18F);
				wave.setParticleWaves(2);
				wave.setParticleAmount(10);
				if (!world.isRemote)
					world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.75F +
							world.rand.nextFloat(), 0.5F + world.rand.nextFloat(), false);
				world.spawnEntity(wave);
			}
		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireShot(this, entity, bender);
	}

	public static class FireShockwaveBehaviour extends ShockwaveBehaviour {

		@Override
		public Behavior onUpdate(EntityShockwave entity) {
			if (entity.getOwner() != null) {
				for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (entity.ticksExisted * 3)) {
					int x = entity.posX < 0 ? (int) (entity.posX + ((entity.ticksExisted * entity.getSpeed())) * Math.sin(angle) - 1)
							: (int) (entity.posX + ((entity.ticksExisted * entity.getSpeed())) * Math.sin(angle));
					int y = (int) (entity.posY - 0.5);
					int z = entity.posZ < 0 ? (int) (entity.posZ + ((entity.ticksExisted * entity.getSpeed()) * Math.cos(angle) - 1))
							: (int) (entity.posZ + ((entity.ticksExisted * entity.getSpeed())) * Math.cos(angle));

					BlockPos spawnPos = new BlockPos(x, (int) (entity.posY), z);
					if (BlockUtils.canPlaceFireAt(entity.world, spawnPos)) {
						if (spawnPos != entity.getPosition()) {
							int time = entity.ticksExisted * entity.getSpeed() >= entity.getRange() - 0.2 ? 120 : 10;
							BlockTemp.createTempBlock(entity.world, spawnPos, time, Blocks.FIRE.getDefaultState());
						}
					}

				}
			}
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {

		}

		@Override
		public void toBytes(PacketBuffer buf) {

		}

		@Override
		public void load(NBTTagCompound nbt) {

		}

		@Override
		public void save(NBTTagCompound nbt) {

		}
	}
}
