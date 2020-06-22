package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_CLOUDBURST;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

public class AbilityCloudBurst extends Ability {

	public AbilityCloudBurst() {
		super(Airbending.ID, "cloudburst");
		requireRaytrace(2.5, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		BendingData data = ctx.getData();

		if (data.hasStatusControl(THROW_CLOUDBURST)) return;

		float chi = STATS_CONFIG.chiCloudburst;
		//2.5F

		float xp = SKILLS_CONFIG.cloudburstHit;

		if (ctx.getLevel() == 1) {
			chi += 1;
		}

		if (ctx.getLevel() == 2) {
			chi += 1.5;
		}

		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			chi *= 2;
		}

		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi *= 2.5;
		}

		if (bender.consumeChi(chi)) {

			Vector target;
			if (ctx.isLookingAtBlock()) {
				target = ctx.getLookPos();
			} else {
				Vector playerPos = getEyePos(entity);
				target = playerPos.plus(getLookRectangular(entity).times(2.5));
			}

			double damage = STATS_CONFIG.cloudburstSettings.damage;
			//2
			int size = 16;
			EntityCloudBall cloudball = new EntityCloudBall(world);

			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
				size = 30;
				damage = STATS_CONFIG.cloudburstSettings.damage * 4;
				//8
				cloudball.setChiSmash(true);
			}
			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				damage = STATS_CONFIG.cloudburstSettings.damage * 2;
				//4
				cloudball.setAbsorb(true);
				size = 20;
			}
			if (ctx.getLevel() == 1) {
				damage = STATS_CONFIG.cloudburstSettings.damage * 1.5;
				//3
				size += 4;
			}

			if (ctx.getLevel() == 2) {
				damage = STATS_CONFIG.cloudburstSettings.damage * 2.25;
				//4.5
				size += 8;
			}

			damage *= ctx.getPowerRatingDamageMod();
			damage += ctx.getAbilityData().getTotalXp() / 100;


			if (target != null) {
				cloudball.setPosition(target);
			}
			cloudball.setOwner(entity);
			cloudball.setSize(size);
			cloudball.setPushStoneButton(ctx.getLevel() >= 1);
			cloudball.setPushIronTrapDoor(ctx.getLevel() >= 2);
			cloudball.setPushIronDoor(ctx.getLevel() >= 2);
			cloudball.setBehavior(new CloudburstBehavior.PlayerControlled());
			cloudball.setDamage((float) damage);
			cloudball.setXp(xp);
			cloudball.setAbility(this);
			cloudball.setElement(new Airbending());
			if (!world.isRemote)
				world.spawnEntity(cloudball);

			data.addStatusControl(THROW_CLOUDBURST);
		}
		super.execute(ctx);

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
	public int getBaseTier() {
		return 3;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiCloudBall(this, entity, bender);
	}

}
