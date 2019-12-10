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

import com.crowsofwar.avatar.common.AvatarAnnouncements;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFlamethrower;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.avatar.common.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.config.convert.Type;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireBlast extends Ability {


	public AbilityFireBlast() {
		super(Firebending.ID, "fire_blast");
		requireRaytrace(-1, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();


		if (bender.consumeChi(STATS_CONFIG.chiFireBlast)) {

			float xp = SKILLS_CONFIG.fireBlastHit;
			float damage = STATS_CONFIG.fireBlastSettings.damage;
			double knockbackMult = STATS_CONFIG.fireBlastSettings.push;

			Vector lookPos;
			if (ctx.isLookingAtBlock()) {
				lookPos = ctx.getLookPos();
			} else {
				Vector look = Vector.getLookRectangular(entity);
				lookPos = Vector.getEyePos(entity).plus(look.times(3));
			}

			float damageMult = ctx.getLevel() >= 2 ? 2 : 1;
			damageMult *= ctx.getPowerRatingDamageMod();

			if (ctx.getLevel() == 1) {
				knockbackMult += 0.25;
			}
			if (ctx.getLevel() == 2) {
				knockbackMult += 0.5;
			}
			if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {

			}
			if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {

			}

			Vec3d height, rightSide;
			if (entity instanceof EntityPlayer) {
				height = entity.getPositionVector().add(0, 0.84, 0);
				height = height.add(entity.getLookVec().scale(0.1));
				//Right
				if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				}
				//Left
				else {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				}
			} else {
				height = entity.getPositionVector().add(0, 0.84, 0);
				height = height.add(entity.getLookVec().scale(0.1));
				if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				} else {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				}

			}

			data.addTickHandler(TickHandlerController.FIREBLAST_UPDATE_TICK);
			/*EntityFlamethrower fireblast = new EntityFlamethrower(world);
			fireblast.setTier(getCurrentTier(ctx.getLevel()));
			fireblast.setPosition(rightSide);
			fireblast.setVelocity(entity.getLookVec().scale(4));
			fireblast.setBehaviour(new FireblastBehaviour());
			fireblast.setElement(new Firebending());
			fireblast.setAbility(this);
			fireblast.setLifeTime(10 + AvatarUtils.getRandomNumberInRange(0, 4));
			fireblast.setExpandedHitbox(2, 2);
			fireblast.setDamage(damage * damageMult);
			fireblast.setEntitySize(0.5F);
			fireblast.setXp(xp);
			fireblast.setOwner(entity);
			world.spawnEntity(fireblast);**/
			entity.swingArm(EnumHand.MAIN_HAND);
		/*	EntityFireShooter shooter = new EntityFireShooter(world);
			shooter.setElement(new Firebending());
			shooter.setOwner(entity);
			shooter.setAbility(this);
			shooter.setKnockbackMult(new Vec3d(knockbackMult, knockbackMult, knockbackMult));
			shooter.setBehaviour(new FireBlastBehaviour());**/
			//Vec3d vel = entity.getLookVec();
			//vel.scale(20000);
			//vel.add(world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat(), world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat(),
			//		world.rand.nextBoolean() ? world.rand.nextFloat() : -world.rand.nextFloat());
			//spawner.spawnParticles(world, AvatarParticles.getParticleFlames(), 120, 140, rightSide.x, rightSide.y,
			//		rightSide.z, vel.x, vel.y, vel.z, false);
		}

	}

	/**
	 * Kills already existing fire arc if there is one
	 */
	private void removeExisting(AbilityContext ctx) {

		EntityFireArc fire = AvatarEntity.lookupControlledEntity(ctx.getWorld(), EntityFireArc
				.class, ctx.getBenderEntity());

		if (fire != null) {
			fire.setBehavior(new FireArcBehavior.Thrown());
		}

	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireBlast(this, entity, bender);
	}

	public static class FireblastBehaviour extends OffensiveBehaviour {

		@Override
		public Behavior onUpdate(EntityOffensive entity) {
			entity.setEntitySize(entity.getAvgSize() * 1.15F);
			entity.setVelocity(entity.getVelocity().scale(0.95));
			if (entity.velocity().magnitude() < 0.8 * 0.8)
				entity.setDead();
			if (entity.onGround)
				entity.setDead();

			World world = entity.world;
			if (world.isRemote) {
				for (int i = 0; i < 3; i++) {
					AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
					double spawnX = boundingBox.getCenter().x + world.rand.nextGaussian() / 10;
					double spawnY = boundingBox.getCenter().y + world.rand.nextGaussian() / 10;
					double spawnZ = boundingBox.getCenter().z + world.rand.nextGaussian() / 10;
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
							world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
							scale(entity.getAvgSize() * 1.5F).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(255, 10, 5).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
							world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
							scale(entity.getAvgSize() * 1.5F).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(255, 10, 5).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
							world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
							scale(entity.getAvgSize() * 1.5F).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
							20 + AvatarUtils.getRandomNumberInRange(0, 30), 10).spawn(world);
					ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
							world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
							scale(entity.getAvgSize() * 1.5F).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
							20 + AvatarUtils.getRandomNumberInRange(0, 30), 10).spawn(world);

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
