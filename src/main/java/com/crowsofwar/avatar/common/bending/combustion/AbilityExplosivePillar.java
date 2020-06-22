package com.crowsofwar.avatar.common.bending.combustion;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityExplosionSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityExplosivePillar extends Ability {
	public AbilityExplosivePillar() {
		super(Combustionbending.ID, "explosive_pillar");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getData().getAbilityData(this);
		float xp = 4F;
		float ticks = 100;
		float chi = STATS_CONFIG.chiExplosivePillar;
		if (bender.consumeChi(chi)) {
			EntityExplosionSpawner spawner = new EntityExplosionSpawner(world);
			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			double mult = ctx.getLevel() >= 1 ? 10 : 8;
				spawner.setOwner(entity);
				spawner.setExplosionFrequency(10F);
				spawner.setExplosionStrength(1F);
				spawner.setPosition(entity.posX, entity.posY, entity.posZ);
				spawner.setVelocity(look.times(mult));
				spawner.maxTicks(ticks);

			if (abilityData.getLevel() == 1) {
				spawner.setOwner(entity);
				spawner.setExplosionFrequency(8F);
				spawner.setExplosionStrength(1.25F);
				spawner.setPosition(entity.posX, entity.posY, entity.posZ);
				spawner.setVelocity(look.times(mult));
				spawner.maxTicks(ticks * 1.5F);
				data.getAbilityData("explosive_pillar").addXp(xp - 1F);

			}
			if (abilityData.getLevel() == 2) {
				spawner.setOwner(entity);
				spawner.setExplosionFrequency(6F);
				spawner.setExplosionStrength(1.5F);
				spawner.setPosition(entity.posX, entity.posY, entity.posZ);
				spawner.setVelocity(look.times(mult));
				spawner.maxTicks(ticks * 2F);
				data.getAbilityData("explosive_pillar").addXp(xp - 2F);

			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				spawner.setOwner(entity);
				spawner.setExplosionFrequency(15F);
				spawner.setExplosionStrength(2F);
				spawner.setPosition(entity.posX, entity.posY, entity.posZ);
				spawner.setVelocity(look.times(mult));
				spawner.maxTicks(ticks * 2.5F);
			}
			data.getAbilityData("explosive_pillar").addXp(xp);
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {

				for (int i = 0; i < 3; i++) {
					Vector direction = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
					Vector direction2 = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							60), 0);
					Vector direction3 = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
							320), 0);
					EntityExplosionSpawner explosionSpawner = new EntityExplosionSpawner(world);
					if (i == 0) {
						explosionSpawner.setVelocity(direction.times(mult));
					}
					if (i == 1) {
						explosionSpawner.setVelocity(direction2.times(mult));
					}
					if (i == 2) {
						explosionSpawner.setVelocity(direction3.times(mult));
					}
					explosionSpawner.setOwner(entity);
					explosionSpawner.setExplosionFrequency(3F);
					explosionSpawner.setExplosionStrength(1F);
					explosionSpawner.setPosition(entity.posX, entity.posY, entity.posZ);
					explosionSpawner.maxTicks(ticks * 1.5F);
					explosionSpawner.setAbility(this);
					world.spawnEntity(explosionSpawner);

				}
			}
			world.spawnEntity(spawner);
		}
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public int getBaseParentTier() {
		return 4;
	}
}
