package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityAirBurst;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.UUID;

public class AirBurstHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("f82d325c-9828-11e8-9eb6-529269fb1459");


	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = null;
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		if (!world.isRemote) {
			abilityData = ctx.getData().getAbilityData("air_burst");
		}
		if (abilityData != null) {

			float powerRating = (float) (bender.calcPowerRating(Airbending.ID) / 100);
			int duration = data.getTickHandlerDuration(this);
			double damage = 0.5 + powerRating;
			float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
			float size = 0.25F + powerRating;
			int ticks = 30;
			int durationToFire = 40;

			if (abilityData.getLevel() == 1) {
				damage = 0.75 + powerRating;
				size = 0.5F + powerRating;
			}

			if (abilityData.getLevel() >= 2) {
				damage = 1 + powerRating;
				size = 0.75F + powerRating;
				ticks = 50;
				durationToFire = 50;
			}

			applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			double radius =  (durationToFire - duration) / 10;


			if (world instanceof WorldServer) {
				WorldServer World = (WorldServer) world;
				if (duration % 2 == 0) {
					for (int degree = 0; degree < 360; degree++) {
						double radians = Math.toRadians(degree);
						double x = radius > 0 ? Math.cos(radians) * radius : Math.cos(radians);
						double z = radius > 0 ? Math.sin(radians) * radius : Math.sin(radians);
						double y = entity.posY + entity.getEyeHeight()/2;
						World.spawnParticle(EnumParticleTypes.CLOUD, x + entity.posX, y, z + entity.posZ, 1, 0, 0, 0, 0.005);
					}
				}
			}


				if (duration >= durationToFire) {
				EntityAirBurst burst = new EntityAirBurst(world);
				burst.setDamage(damage);
				burst.setExpandStopTime(ticks);
				burst.setSize(size);
				burst.setAbility(new AbilityAirBurst(Airbending.ID, "air_burst"));
				world.spawnEntity(burst);
				System.out.println(burst.getAbility());
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
						SoundCategory.BLOCKS, 1, 1.5F);
				return true;
			}

			return false;
		} else return true;
	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
				.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
				"Airburst charge modifier", multiplier - 1, 1));

	}


}
