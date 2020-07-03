package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_FIREBALL;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

public class AbilitySwirlingInferno extends Ability {

	public AbilitySwirlingInferno() {
		super(Firebending.ID, "swirling_inferno");
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
			canUse |= fireballs.size() < 3 && ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST);

			damage *= ctx.getLevel() >= 2 ? 1.75f : 1f;
			damage *= ctx.getPowerRatingDamageMod();

			if (ctx.getLevel() == 1) {
				size = 18;
			}

			if (ctx.getLevel() == 2) {
				size = 20;
			}

			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
				size = 18;
				damage -= 2F;
			}
			if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND))
				size = 20;
			damage += size / 10F;

			if (canUse) {
				assert target != null;
				EntityFireball fireball = new EntityFireball(world);
				fireball.setPosition(target);
				fireball.setOwner(entity);
				fireball.setBehavior(fireballs.size() < 1 ? new AbilityFireball.FireballOrbitController() : new FireballBehavior.PlayerControlled());
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
	public int getBaseTier() {
		return 5;
	}

	@Override
	public boolean isChargeable() {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}
}
