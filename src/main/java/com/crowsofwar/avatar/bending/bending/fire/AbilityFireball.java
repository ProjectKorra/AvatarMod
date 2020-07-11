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
package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_FIREBALL;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

/**
 * @author CrowsOfWar
 */
public class AbilityFireball extends Ability {

	public AbilityFireball() {
		super(Firebending.ID, "fireball");
		requireRaytrace(2.5, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();


		if (bender.consumeChi(STATS_CONFIG.chiFireball)) {

			Vector target;
			if (ctx.isLookingAtBlock() && !world.isRemote) {
				target = ctx.getLookPos();
			} else {
				Vector playerPos = getEyePos(entity);
				target = playerPos.plus(getLookRectangular(entity).times(2.5));
			}

			float damage = STATS_CONFIG.fireballSettings.damage;
			int size = 16;
			boolean canUse = !data.hasStatusControl(THROW_FIREBALL);
			List<EntityFireball> fireballs = world.getEntitiesWithinAABB(EntityFireball.class,
					entity.getEntityBoundingBox().grow(3.5, 3.5, 3.5));
			canUse |= fireballs.size() < 3 && ctx.isDynamicMasterLevel(AbilityTreePath.FIRST);

			damage *= ctx.getLevel() >= 2 ? 1.75f : 1f;
			damage *= ctx.getPowerRatingDamageMod();

			if (ctx.getLevel() == 1) {
				size = 18;
			}

			if (ctx.getLevel() == 2) {
				size = 20;
			}

			if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
				size = 18;
				damage -= 2F;
			}
			if (ctx.isDynamicMasterLevel(AbilityTreePath.SECOND))
				size = 20;
			damage += size / 10F;

			if (canUse) {
				assert target != null;
				EntityFireball fireball = new EntityFireball(world);
				fireball.setPosition(target);
				fireball.setOwner(entity);
				fireball.setBehavior(fireballs.size() < 1 ? new FireballOrbitController() : new FireballBehavior.PlayerControlled());
				fireball.setDamage(damage);
				fireball.setPowerRating(bender.calcPowerRating(Firebending.ID));
				fireball.setSize(size);
				fireball.setLifeTime(30);
				fireball.setOrbitID(fireballs.size() + 1);
				fireball.setPerformanceAmount((int) (BattlePerformanceScore.SCORE_MOD_SMALL * 1.5));
				fireball.setAbility(this);
				fireball.setFireTime(size / 5);
				fireball.setXp(SKILLS_CONFIG.fireballHit);
				if (!world.isRemote)
					world.spawnEntity(fireball);

				data.addStatusControl(THROW_FIREBALL);

			}

		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireball(this, entity, bender);
	}

	@Override
	public int getBaseTier() {
		return 3;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	public static class FireballOrbitController extends FireballBehavior.PlayerControlled {

		@Override
		public FireballBehavior onUpdate(EntityOffensive entity) {
			EntityLivingBase owner = entity.getOwner();

			if (owner == null || !(entity instanceof EntityFireball)) return this;

			BendingData data = Objects.requireNonNull(Bender.get(owner)).getData();

			Vector look = Vector.getLookRectangular(owner);
			Vector target = Vector.getEyePos(owner).plus(look.times(2 + ((EntityFireball) entity).getSize() * 0.03125F));
			List<EntityFireball> fireballs = entity.world.getEntitiesWithinAABB(EntityFireball.class,
					owner.getEntityBoundingBox().grow(5, 5, 5));
			fireballs = fireballs.stream().filter(entityFireball -> entityFireball.getBehavior() instanceof FireballBehavior.PlayerControlled
					&& entityFireball.getOwner() == entity.getOwner()).collect(Collectors.toList());
			Vec3d motion = Objects.requireNonNull(target).minus(Vector.getEntityPos(entity)).toMinecraft();

			if (!fireballs.isEmpty() && fireballs.size() > 1 && fireballs.contains(entity)) {
				//Ensures a constant list order for the fireballs
				int index = fireballs.indexOf(entity);
				int id = Math.max(((EntityFireball) entity).getOrbitID() - 1, 0);
				if (index != id) {
					EntityFireball newBall = fireballs.get(id);
					fireballs.set(fireballs.indexOf(newBall), (EntityFireball) entity);
					fireballs.set(index, newBall);
				}
				int secondIn = 1;
				EntityFireball ball2nd = fireballs.get(1);
				int secondId = Math.max(ball2nd.getOrbitID() - 1, 0);
				if (secondIn != secondId) {
					EntityFireball newBall = fireballs.get(secondId);
					fireballs.set(fireballs.indexOf(newBall), ball2nd);
					fireballs.set(secondIn, newBall);
				}
				int angle = (entity.getOwner().ticksExisted * 5) % 360;
				for (int i = 0; i < fireballs.size(); i++) {
					//Tfw the game is adding an extra 120 degrees for no reason
					angle = angle + (360 / fireballs.size() * i);
					if (i == 2)
						angle -= 120;
					double radians = Math.toRadians(angle);
					double x = 1.75 * Math.cos(radians);
					double z = 1.75 * Math.sin(radians);
					Vec3d pos = new Vec3d(x, 0, z);
					pos = pos.add(owner.posX, owner.getEntityBoundingBox().minY + 1, owner.posZ);
					motion = pos.subtract(fireballs.get(i).getPositionVector()).scale(0.75);
					fireballs.get(i).setVelocity(motion);
				}
			} else {
				motion = motion.scale(0.75);
				entity.setVelocity(motion);
			}
			data.addStatusControl(THROW_FIREBALL);

			if (entity.getAbility() instanceof AbilityFireball) {
				if (data.getAbilityData(new AbilityFireball().getName()).isMasterPath(AbilityTreePath.SECOND)) {
					int size = ((EntityFireball) entity).getSize();
					if (size < 60 && entity.ticksExisted % 4 == 0) {
						((EntityFireball) entity).setSize(size + 1);
						entity.setDamage(20 / 32F * (((EntityFireball) entity).getSize() * 0.03125F) * entity.getDamage());
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
