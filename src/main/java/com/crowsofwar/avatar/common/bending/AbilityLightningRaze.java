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
		double speed = 4;
		float chi = 5;
		float frequency = 5;
		int bolts = 1;
		//use randoms for positioning
		//Spawn entity a little ahead of the player


		if (ctx.getLevel() >= 1) {
			ticks = 40;
			frequency = 4;
			chi = 6;
			speed = 6;
			bolts = 2;
		}
		if (ctx.getLevel() >= 2) {
			speed = 8;
			frequency = 3;
			chi = 7;
			bolts = 3;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			frequency = 2;
			bolts = 1;
			ticks = 40;
			speed = 40;
			chi = 6;
			//Super-fast line of lightning that lights up the ground
			//Zeus' Wrath
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			frequency = 4;
			ticks = 60;
			speed = 5;
			chi = 8;
			bolts = 5;
			//spawn 3 (cloud of lightning), tracks enemies
			//Thor's wrath
		}

		if (bender.consumeChi(chi)) {

					Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
					EntityLightningSpawner boltSpawner = new EntityLightningSpawner(world);
					boltSpawner.setOwner(entity);
					//boltSpawner.setPosition(entity.posX, entity.posY, entity.posZ);
					boltSpawner.setPosition(look.minusY(entity.getEyeHeight()));
					boltSpawner.setVelocity(look.times(speed));
					boltSpawner.setDuration(ticks);
					boltSpawner.setLightningFrequency(frequency);
					boltSpawner.setTrackEnemies(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
					boltSpawner.setAmountofBolts(bolts) ;
					world.spawnEntity(boltSpawner);

			}

		}
	}

