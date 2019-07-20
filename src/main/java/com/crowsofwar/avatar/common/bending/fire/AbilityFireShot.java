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
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.ShockwaveBehaviour;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireShot extends Ability {

	static HashMap<UUID, HashMap<BlockPos, Integer>> ignitedTimes = new HashMap<>();
 	static HashMap<BlockPos, String> ignitedBlocks = new HashMap<>();

	public AbilityFireShot() {
		super(Firebending.ID, "fire_shot");
		requireRaytrace(-1, false);
	}

	private static void setIgnitedTimes(UUID uuid, BlockPos block, int time) {
		if (ignitedTimes.containsKey(uuid)) {
			ignitedTimes.replace(uuid, ignitedTimes.get(uuid), ignitedTimes.get(block));
		} else {
		    HashMap<BlockPos, Integer> pos = new HashMap<>();
		    pos.put(block, time);
            ignitedTimes.put(uuid, pos);
        }
	}

	static HashMap<BlockPos, Integer> getIgnitedTimes(UUID uuid) {
		return ignitedTimes.getOrDefault(uuid, new HashMap<>());
	}

	private static void setIgnitedBlocks(BlockPos block, String UUID) {
		if (ignitedBlocks.containsKey(block)) {
			ignitedBlocks.replace(block, UUID);
		} else {
			ignitedBlocks.put(block, UUID);
		}
	}

	static String getIgnitedOwner(BlockPos pos) {
		return ignitedBlocks.getOrDefault(pos, null);
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

		float speed = 0.5F;
		double damageMult = bender.getDamageMult(Firebending.ID);
		float chi = STATS_CONFIG.chiFireShot;
		if (ctx.getLevel() == 1) {
			speed += 0.25F;
			chi += 0.5F;
			damageMult += 0.5;
		}
		if (ctx.getLevel() == 2) {
			speed += 0.5F;
			chi += 1;
			damageMult += 1;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			speed += 0.75F;
			chi += 1.5F;
			damageMult += 2;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi += 2F;
		}
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
				flames.setTrailingFire(ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
				flames.setDamageMult(damageMult);
				world.spawnEntity(flames);
			} else {
				EntityShockwave wave = new EntityShockwave(world);
				wave.setOwner(entity);
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
				wave.setParticleSpeed(0.2F);
				wave.setParticleAmount(12);
				world.spawnEntity(wave);
			}
		}

	}

	public static class FireShockwaveBehaviour extends ShockwaveBehaviour {

		@Override
		public Behavior onUpdate(EntityShockwave entity) {
			if (entity.getOwner() != null) {
					BlockPos prevPos = entity.getPosition();
					for (int degree = 0; degree < 360; degree += 30) {
						double angle = Math.toRadians(degree);
						//Sin x for shockwave, cos x for sphere. We want a fire wave, so we sin x.
						double x = entity.posX + (entity.ticksExisted * entity.getSpeed()) * Math.sin(angle);
						//double y = entity.posY;
						double z = entity.posZ + (entity.ticksExisted * entity.getSpeed()) * Math.cos(angle);
						Vec3d direction = new Vec3d(x, entity.getOwner().getEntityBoundingBox().minY, z);
						BlockPos spawnPos = new BlockPos((int) (direction.x /*+ entity.posX**/), (int) (direction.y /*+ entity.posY**/),
								(int) (direction.z /*+ entity.posZ**/));
						if (Blocks.FIRE.canPlaceBlockAt(entity.world, spawnPos) && prevPos.getDistance((int) entity.posX, (int) entity.posY, (int) entity.posZ) !=
								spawnPos.getDistance((int) entity.posX, (int) entity.posY, (int) entity.posZ)
								&& entity.world.getBlockState(spawnPos).getBlock() == Blocks.AIR) {
							int time = entity.ticksExisted * entity.getSpeed() >= entity.getRange() ? 120 : 10;
							UUID uuid = entity.getOwner().getUniqueID();
							setIgnitedTimes(uuid, spawnPos, time);
							setIgnitedBlocks(spawnPos, entity.getOwner().getUniqueID().toString());
							entity.world.setBlockState(spawnPos, Blocks.FIRE.getDefaultState());
							prevPos = spawnPos;
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
