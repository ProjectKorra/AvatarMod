package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
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

		if (data.hasStatusControl(StatusControl.THROW_CLOUDBURST)) return;

		float chi  = STATS_CONFIG.chiCloudburst;
		//2.5F

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
			//1.5
			EntityCloudBall cloudball = new EntityCloudBall(world);

			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				cloudball.setSize(20);
				damage = STATS_CONFIG.cloudburstSettings.damage * 4;
				//6
				cloudball.canchiSmash(true);
			}
			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
				damage = STATS_CONFIG.cloudburstSettings.damage * 2;
				//3
				cloudball.canAbsorb(true);
			}
			if (ctx.getLevel() == 1) {
				damage = STATS_CONFIG.cloudburstSettings.damage * 1.5;
				//2.25
			}

			if (ctx.getLevel() == 2) {
				damage = STATS_CONFIG.cloudburstSettings.damage * 2.25;
				//3.375
			}

			damage *= ctx.getPowerRatingDamageMod();


			cloudball.setPosition(target);
			cloudball.setOwner(entity);
			cloudball.setStartingPosition(entity.getPosition());
			cloudball.setBehavior(new CloudburstBehavior.PlayerControlled());
			cloudball.setDamage((float) damage);
			cloudball.setAbility(this);
			world.spawnEntity(cloudball);

			data.addStatusControl(StatusControl.THROW_CLOUDBURST);

		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiCloudBall(this, entity, bender);
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();

		int coolDown = 140;

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		if (ctx.getLevel() == 1) {
			coolDown = 120;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 100;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 70;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 85;
		}
		return coolDown;
	}

}
