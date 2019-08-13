package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Lightning benders or redirectors need to "charge" the lightning for a short time before
 * releasing it. This is a generic handler for lightning charging, which can either be used for
 * {@link LightningCreateHandler regular lightningbending} or
 * {@link LightningRedirectHandler lightning redirection}.
 *
 * @author CrowsOfWar
 */
public abstract class LightningChargeHandler extends TickHandler {
	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("dfb6235c-82b6-407e-beaf-a48045735a82");
	private ParticleSpawner particleSpawner;

	LightningChargeHandler(int id) {
		super(id);
		this.particleSpawner = new NetworkParticleSpawner();
	}

	/**
	 * Gets AbilityData to be used for determining lightning strength. This is normally the
	 * bender's AbilityData, but in the case of redirection, it is the original bender's
	 * AbilityData.
	 */
	@Nullable
	protected abstract AbilityData getLightningData(BendingContext ctx);

	@Override
	public boolean tick(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		double powerRating = ctx.getBender().calcPowerRating(Lightningbending.ID);

		if (world.isRemote) {
			return false;
		}

		int duration = data.getTickHandlerDuration(this);

		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
		double inverseRadius = (40F - duration) / 10;

		if (duration % 3 == 0) {
			for (int i = 0; i < 8; i++) {
				Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
						i * 45), 0).times(inverseRadius).withY(entity.getEyeHeight() / 2);
				particleSpawner.spawnParticles(world, AvatarParticles.getParticleElectricity(), 1, 2, lookpos.x() + entity.posX,
						lookpos.y() + entity.getEntityBoundingBox().minY, lookpos.z() + entity.posZ, 2, 1.2, 2, true);
			}
		}

		if (duration >= 40) {

			AbilityData abilityData = getLightningData(ctx);
			if (abilityData == null) {
				return true;
			}

			double speed = abilityData.getLevel() >= 1 ? 30 : 40;
			float damage = abilityData.getLevel() >= 2 ? 8 : 6;
			float size = 1;
			float[] turbulenceValues = { 0.6f, 1.2f };

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = 12;
				size = 0.75f;
				turbulenceValues = new float[] { 0.6f, 1.2f, 0.8f };
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				size = 1.5f;
			}

			speed += powerRating / 15;
			damage *= ctx.getBender().getDamageMult(Lightningbending.ID);

			fireLightning(world, entity, damage, speed, size, turbulenceValues);

			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1, 2);

			return true;

		}

		return false;

	}

	private void fireLightning(World world, EntityLivingBase entity, float damage, double speed, float size, float[] turbulenceValues) {

		for (float turbulence : turbulenceValues) {

			EntityLightningArc lightning = new EntityLightningArc(world);
			lightning.setOwner(entity);
			lightning.setTurbulence(turbulence);
			lightning.setDamage(damage);
			lightning.setSizeMultiplier(size);
			lightning.setAbility(new AbilityLightningArc());
			lightning.setMainArc(turbulence == turbulenceValues[0]);

			lightning.setPosition(Vector.getEyePos(entity));
			lightning.setEndPos(Vector.getEyePos(entity));

			Vector velocity = Vector.getLookRectangular(entity);
			velocity = velocity.normalize().times(speed);
			lightning.setVelocity(velocity);

			world.spawnEntity(lightning);

		}

	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID, "Lightning charge modifier", multiplier - 1, 1));

	}

}
