package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAvatarLightning;
import com.crowsofwar.avatar.common.entity.EntityLightningSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;


public class AbilityLightningRaze extends Ability {
	public AbilityLightningRaze() {
		super(Lightningbending.ID, "lightning_raze");
	}



	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();

		int ticks = 20;
		//How long the spawner tays alive.
		int speed = 5;
		float chi = 5;
		float frequency = 5;
		//How many ticks pass before each lightning bolt strikes.
		int bolts = 1;
		float accuracy = 2;
		float damageMult = 0.5F;

		/*0 accuracy is the most accurate; each number represents how far away from the spawn position
		it will be.**/


		if (ctx.getLevel() >= 1) {
			ticks = 40;
			frequency = 4;
			chi = 6;
			speed = 7;
			bolts = 2;
			damageMult = 0.75F;


		}
		if (ctx.getLevel() >= 2) {
			speed = 9;
			frequency = 3;
			chi = 7;
			bolts = 3;
			accuracy = 2;
			damageMult = 1F;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			frequency = 2;
			bolts = 1;
			ticks = 40;
			speed = 20;
			chi = 7;
			accuracy = 0;
			damageMult = 1.75F;
			//Super-fast line of lightning that lights up the ground
			//Zeus' Wrath
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			frequency = 4;
			ticks = 60;
			speed = 3;
			chi = 8;
			bolts = 5;
			accuracy = 3;
			damageMult = 0.25F;
			//Thor's wrath
		}

		if (bender.consumeChi(chi)) {
			Raytrace.Result hit = Raytrace.getTargetBlock(entity, 6);

			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
				hit = Raytrace.getTargetBlock(entity, 8);
			}
			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				hit = Raytrace.getTargetBlock(entity, 20);
			}

			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			Vector playerPos = getEyePos(entity);
			Vector lookPos = playerPos.plus(getLookRectangular(entity).times(1.3));
			Vector hitAt = hit.getPosPrecise();

			EntityLightningSpawner boltSpawner = new EntityLightningSpawner(world);
			boltSpawner.setOwner(entity);
			if (hit.hitSomething()) {
				boltSpawner.setPosition(hitAt.x(), hitAt.y(), hitAt.z());
			} else {
				boltSpawner.setPosition(lookPos.withY(entity.posY));
			}
			boltSpawner.setVelocity(look.times(speed));
			boltSpawner.setSpeed(speed);
			//This is so that the player can control the entity; otherwise unnecessary.
			boltSpawner.setDamageMult(damageMult);
			boltSpawner.setDuration(ticks);
			boltSpawner.setLightningFrequency(frequency);
			boltSpawner.setPlayerControl(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
			boltSpawner.setAmountofBolts(bolts);
			boltSpawner.setAccuracy(accuracy);
			world.spawnEntity(boltSpawner);


		}

	}
}

