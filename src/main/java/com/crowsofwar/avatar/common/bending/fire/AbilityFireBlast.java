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
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFireShooter;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.avatar.common.entity.data.FireShooterBehaviour;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityFireBlast extends Ability {

	private ParticleSpawner spawner;

	public AbilityFireBlast() {
		super(Firebending.ID, "fire_blast");
		requireRaytrace(-1, false);
		spawner = new NetworkParticleSpawner();
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

			removeExisting(ctx);


			float damageMult = ctx.getLevel() >= 2 ? 2 : 1;
			damageMult *= ctx.getPowerRatingDamageMod();
			/*
			List<Entity> fireArc = Raytrace.entityRaytrace(world, Vector.getEntityPos(entity).withY(entity.getEyeHeight()), Vector.getLookRectangular(entity).times(10), 3,
					entity1 -> entity1 != entity);

			if (fireArc.isEmpty()) {
				if (ctx.getLevel() >= 2) {
					for (Entity a : fireArc) {
						if (a instanceof AvatarEntity) {
							if (((AvatarEntity) a).getOwner() != entity) {
								if (a instanceof EntityFireArc) {
									((EntityFireArc) a).setOwner(entity);
									((EntityFireArc) a).setBehavior(new FireArcBehavior.PlayerControlled());
									((EntityFireArc) a).setAbility(this);
									((EntityFireArc) a).setPosition(Vector.getLookRectangular(entity).times(1.5F));
									((EntityFireArc) a).setDamageMult(damageMult);
								}
							}
						}
					}
				}
			}

			EntityFireArc fire = new EntityFireArc(world);
			if (lookPos != null) {
				fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
				fire.setBehavior(new FireArcBehavior.PlayerControlled());
				fire.setOwner(entity);
				fire.setDamageMult(damageMult);
				fire.setCreateBigFire(ctx.isMasterLevel(AbilityTreePath.FIRST));
				fire.setAbility(this);
				world.spawnEntity(fire);

				data.addStatusControl(StatusControl.THROW_FIRE);
			}
**/
			Vec3d height, rightSide;
			if (entity instanceof EntityPlayer) {
				height = entity.getPositionVector().add(0, 1.6, 0);
				height = height.add(entity.getLookVec().scale(0.8));
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
				height = entity.getPositionVector().add(0, 1.6, 0);
				height = height.add(entity.getLookVec().scale(0.8));
				if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				} else {
					rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
					rightSide = rightSide.add(height);
				}

			}
			EntityFireShooter shooter = new EntityFireShooter(world);
			shooter.setElement(new Firebending());
			shooter.setOwner(entity);
			shooter.setAbility(this);
			shooter.setKnockbackMult(new Vec3d(knockbackMult, knockbackMult, knockbackMult));
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
	public int getTier() {
		return 2;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireBlast(this, entity, bender);
	}

	public static class FireBlastBehaviour extends FireShooterBehaviour {

		@Override
		public Behavior onUpdate(EntityFireShooter entity) {
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
