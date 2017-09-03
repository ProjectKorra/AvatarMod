package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.LightningRedirectionData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Same as LightningChargeHandler, but handles lightning redirection
 *
 * @author CrowsOfWar
 */
public class LightningRedirectHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("dfb6235c-82b6-407e-beaf-a48045735a82");

	@Override
	public boolean tick(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();

		if (world.isRemote) {
			return false;
		}

		int duration = data.getTickHandlerDuration(this);

		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));

		if (duration >= 40) {

			List<LightningRedirectionData> redirectionDataList = data.getMiscData()
					.getLightningRedirectionData();
			fireLightning(world, entity, redirectionDataList);
			redirectionDataList.clear();

			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE,
					SoundCategory.PLAYERS, 1, 2);


			return true;

		}

		return false;

	}

	private void fireLightning(World world, EntityLivingBase entity,
							   List<LightningRedirectionData> redirectionDataList) {

		for (LightningRedirectionData redirectionData : redirectionDataList) {

			EntityLightningArc lightning = new EntityLightningArc(world);
			lightning.setController(entity);
			lightning.setOwner(redirectionData.getOriginalShooter());
			lightning.setTurbulence(redirectionData.getTurbulence());
			lightning.setDamage(redirectionData.getDamage());
			lightning.setSizeMultiplier(redirectionData.getSizeMultiplier());
			lightning.setMainArc(redirectionData.isMainArc());

			lightning.setPosition(Vector.getEyePos(entity));
			lightning.setEndPos(Vector.getEyePos(entity));

			Vector velocity = Vector.getLookRectangular(entity);
			velocity = velocity.normalize().times(20); // TODO redirection speed
			lightning.setVelocity(velocity);

			world.spawnEntity(lightning);

		}

	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
				.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
				"Lightning charge modifier", multiplier - 1, 1));

	}

}
