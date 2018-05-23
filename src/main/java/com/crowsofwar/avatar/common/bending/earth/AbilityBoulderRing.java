package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityBoulder;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.BoulderBehavior;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

public class AbilityBoulderRing extends Ability {

	public AbilityBoulderRing(){
		super (Earthbending.ID, "boulder_ring");
		requireRaytrace(2.5, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		AbilityData abilityData = ctx.getAbilityData();
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();

		float damage = 0.1f;
		float xp = abilityData.getTotalXp();
		float ticks = 50;
		double speed = 1;
		float chi = 4;
		int boulders = 3;
		float radius = 2;
		int health = 3;

		if (ctx.getLevel() >= 1) {
			damage = 0.2f;
			ticks = 80;
			boulders = 4;
			radius = 3;
			health = 4;
		}
		if (ctx.getLevel() >= 2) {
			ticks = 100;
			boulders = 5;
			radius = 4;
			health = 6;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			damage = 1f;
			ticks = 30;
			//speed = 12;
			boulders = 4;
			health = 1;

		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			damage = 0.5f;
			ticks = 60;
			//speed = 20;
			boulders = 10;
		}

		if (bender.consumeChi(chi)) {

				//for (int i = 0; i < 3; i++) {

					Vector direction1 = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							360/boulders), entity.rotationPitch);
					//Vector velocity = direction1.times(speed);
					Vector playerPos = getEyePos(entity);
					Vector target = playerPos.plus(getLookRectangular(entity).times(2.5));

					EntityBoulder boulder = new EntityBoulder(world);
					boulder.setSpeed((float) speed);
					boulder.setTicksAlive(ticks);
					boulder.setOwner(entity);
					boulder.setPosition(target);
					boulder.setHealth(health);
					boulder.setBouldersLeft(boulders);
					boulder.setDamage(damage);
					boulder.setRadius(radius);
					//boulder.setSize(4);
					boulder.setKnockBack(0.1F);
					boulder.setBehavior(new BoulderBehavior.PlayerControlled());
					world.spawnEntity(boulder);

				//}
			}

		}
	}

