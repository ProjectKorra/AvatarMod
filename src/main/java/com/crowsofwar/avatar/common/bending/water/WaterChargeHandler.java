package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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

		double powerRating = ctx.getBender().calcPowerRating(Waterbending.ID);
		int duration = data.getTickHandlerDuration(this);
		double speed = abilityData.getLevel() >= 1 ? 20 : 30;
		float damage = 4;
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		float size = 0.1f;
		int durationToFire = 100;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			durationToFire = 50;
		}

		if (world.isRemote) {
			return false;
		}

		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {

			// Fire once every 10 ticks, until we get to 100 ticks
			// So at fire at 60, 70, 80, 90, 100
			if (duration >= 60 && duration % 10 == 0) {

				fireCannon(world, entity, damage, speed, size);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
						SoundCategory.PLAYERS, 1, 2);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

				return duration >= 100;

			}

		}

		if (duration >= durationToFire) {

			speed = abilityData.getLevel() >= 1 ? 20 : 30;
			speed += powerRating / 15;
			damage = 8;
			size = 1;

			if (abilityData.getLevel() == 1) {
				damage = 11;
				size = 2;
			}
			if (abilityData.getLevel() == 2) {
				damage = 12;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = 20;
				size = 2.5F;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				size = 2;
			}
			damage += powerRating / 30;

			if (abilityData.getLevel() == 1) {
				damage = 11;
				size = 1.1F;

			}
			if (abilityData.getLevel() == 2) {
				damage = 12;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {

				damage = 17;
				size = 1.25f;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {

				damage = 4;
				size = 0.1F;

			}
			damage += powerRating / 30;

			fireCannon(world, entity, damage, speed, size);

			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
					SoundCategory.PLAYERS, 1, 2);

			return true;
		}

		return false;

	}

	private void fireCannon(World world, EntityLivingBase entity, float damage, double speed,
							float size) {

		EntityWaterCannon cannon = new EntityWaterCannon(world);

		cannon.setOwner(entity);
		cannon.setDamage(damage);
		cannon.setSizeMultiplier(size);

		cannon.setPosition(Vector.getEyePos(entity));

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


