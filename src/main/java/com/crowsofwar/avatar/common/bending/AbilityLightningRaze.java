package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;


public class AbilityLightningRaze extends Ability {
	public AbilityLightningRaze() {
	super(Lightningbending.ID, "lightning_raze");
}

	@Override
	public void execute(AbilityContext ctx) {

		AbilityData abilityData = ctx.getAbilityData();
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();


		float xp = abilityData.getTotalXp();
		float ticks = 20;
		//How long the spawner tays alive.
		double speed = 5;
		float chi = 5;
		float frequency = 5;
		//How many ticks pass before each lightning bolt strikes.
		int bolts = 1;
		float accuracy = 2;
		/*0 accuracy is the most accurate; each number represents how far away from the spawn position
		it will be.**/



		if (ctx.getLevel() >= 1) {
			ticks = 40;
			frequency = 4;
			chi = 6;
			speed = 7;
			bolts = 2;

		}
		if (ctx.getLevel() >= 2) {
			speed = 9;
			frequency = 3;
			chi = 7;
			bolts = 3;
			accuracy = 2;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			frequency = 2;
			bolts = 1;
			ticks = 40;
			speed = 20;
			chi = 6;
			accuracy = 0;
			//Super-fast line of lightning that lights up the ground
			//Zeus' Wrath
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			frequency = 4;
			ticks = 60;
			speed = 5;
			chi = 8;
			bolts = 5;
			accuracy = 3;
			//spawn 3 (cloud of lightning), tracks enemies
			//Thor's wrath
		}

		if (bender.consumeChi(chi)) {

					Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
					//Vector lookPos = Vector.getLookRectangular(entity);

					EntityLightningSpawner boltSpawner = new EntityLightningSpawner(world);
					boltSpawner.setOwner(entity);
					boltSpawner.setPosition(entity.posX, entity.posY, entity.posZ);
					//boltSpawner.setPosition(lookPos.minusY(entity.getEyeHeight()));
					boltSpawner.setVelocity(look.times(speed));
					boltSpawner.setDuration(ticks);
					boltSpawner.setLightningFrequency(frequency);
					boltSpawner.setTrackEnemies(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
					boltSpawner.setAmountofBolts(bolts) ;
					boltSpawner.setAccuracy(accuracy);
					world.spawnEntity(boltSpawner);

			}

		}
	}

