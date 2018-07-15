package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.lwjgl.Sys;

import java.util.UUID;

public class WaterChargeHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("87a0458a-38ea-4d7a-be3b-0fee10217aa6");

	@Override
	public boolean tick(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData("water_cannon");
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		double powerRating = ctx.getBender().calcPowerRating(Waterbending.ID);
		int duration = data.getTickHandlerDuration(this);
		double speed = abilityData.getLevel() >= 1 ? 20 : 30;
		float damage;
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		float size;
		float ticks = 50;
		//Multiply by 1.5 to get water cannon size
		int durationToFire = 40;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			durationToFire = 60;
		}

		if (world.isRemote) {
			return false;
		}

		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));

		if (duration < durationToFire && !world.isRemote) {
			WorldServer World = (WorldServer) bender.getWorld();
			if (duration % 20 == 0) {
				System.out.println("Particles");
				for (int degree = 0; degree < 360; degree++) {
					double radians = Math.toRadians(degree);
					double x = Math.cos(radians);// + ((360-degree)/100);
					double z = Math.sin(radians);
					double y = entity.posZ;
					World.spawnParticle(EnumParticleTypes.WATER_SPLASH, x + entity.posX, y, z + entity.posZ, 300, 0, 0, 0, (double) 0);

				}
			}
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {

			size = 0.1F;
			ticks = 50;
			damage = (float) (0.25 * bender.getDamageMult(Waterbending.ID));

			// Fire once every 10 ticks, until we get to 100 ticks
			// So at fire at 60, 70, 80, 90, 100
			if (duration >= 40 && duration % 10 == 0) {

				fireCannon(world, entity, damage, speed, size, ticks);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
						SoundCategory.PLAYERS, 1, 2);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

				return duration >= 100;

			}

		} else if (duration >= durationToFire) {

			speed = abilityData.getLevel() >= 1 ? 20 : 30;
			speed += powerRating / 15;
			damage = (float) (0.5 * bender.getDamageMult(Waterbending.ID));
			size = 0.25F;

			if (abilityData.getLevel() >= 1) {
				damage = (float) (1 * bender.getDamageMult(Waterbending.ID));;
				size = 0.5f;
				ticks = 75;
			}
			if (abilityData.getLevel() >= 2) {
				damage =(float) (1.5 * bender.getDamageMult(Waterbending.ID));
				size = 0.75f;
				ticks = 100;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = (float) (3 * bender.getDamageMult(Waterbending.ID));;
				size = 1f;
				ticks = 200;
			}

			damage *= bender.getDamageMult(Waterbending.ID);
			fireCannon(world, entity, damage, speed, size, ticks);

			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_SPLASH,
					SoundCategory.PLAYERS, 1, 2);

			return true;
		}

		return false;

	}

	private void fireCannon(World world, EntityLivingBase entity, float damage, double speed,
							float size, float ticks) {

		EntityWaterCannon cannon = new EntityWaterCannon(world);

		cannon.setOwner(entity);
		cannon.setDamage(damage);
		cannon.setSizeMultiplier(size);
		cannon.setPosition(Vector.getEyePos(entity));
		cannon.setLifeTime(ticks);

		Vector velocity = Vector.getLookRectangular(entity);
		velocity = velocity.normalize().times(speed);
		cannon.setVelocity(velocity);
		world.spawnEntity(cannon);

	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
				.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
				"Water charge modifier", multiplier - 1, 1));

	}

}


