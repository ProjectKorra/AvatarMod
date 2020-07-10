package com.crowsofwar.avatar.common.bending.water.tickhandlers;

import com.crowsofwar.avatar.common.bending.water.AbilityWaterBlast;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityLightCylinder;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightCylinderBehaviour;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class WaterChargeHandler extends TickHandler {
	public static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("87a0458a-38ea-4d7a-be3b-0fee10217aa6");

	public WaterChargeHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData("water_blast");
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		double powerRating = ctx.getBender().calcPowerRating(Waterbending.ID);
		int duration = data.getTickHandlerDuration(this);
		double speed = abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND) ? 40 : 0;
		float damage;
		float maxRange = abilityData.getLevel() >= 1 ? 40 : 60;
		Vec3d knockback = entity.getLookVec().scale(maxRange / 50).scale(STATS_CONFIG.waterCannonSettings.waterCannonKnockbackMult);
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		float size;
		//Multiply by 1.5 to get water cannon size
		float ticks = 50;
		int durationToFire = 40;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			durationToFire = 60;
		}



		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {

			size = 0.1F;
			ticks = 50;
			damage = (float) (STATS_CONFIG.waterCannonSettings.waterCannonDamage * 0.5 * bender.getDamageMult(Waterbending.ID));
			knockback.scale(damage);

			// Fire once every 10 ticks, until we get to 100 ticks
			// So at fire at 60, 70, 80, 90, 100
			if (duration >= 40 && duration % 10 == 0) {

				fireCannon(world, entity, damage, speed, size, ticks, maxRange, knockback);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.PLAYERS, 1, 2);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

				return duration >= 100;

			}

		} else if (duration >= durationToFire) {

			speed = abilityData.getLevel() >= 1 ? 20 : 30;
			speed += powerRating / 15;
			damage = (float) (STATS_CONFIG.waterCannonSettings.waterCannonDamage * bender.getDamageMult(Waterbending.ID));
			//Default damage is 1
			size = 0.5F;

			if (abilityData.getLevel() >= 1) {
				damage = (float) (STATS_CONFIG.waterCannonSettings.waterCannonDamage * 1.25 * bender.getDamageMult(Waterbending.ID));
				size = 0.75f;
				ticks = 75;
			}
			if (abilityData.getLevel() >= 2) {
				damage = (float) (STATS_CONFIG.waterCannonSettings.waterCannonDamage * 1.5 * bender.getDamageMult(Waterbending.ID));
				size = 1f;
				ticks = 100;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = (float) (STATS_CONFIG.waterCannonSettings.waterCannonDamage * 2.5 * bender.getDamageMult(Waterbending.ID));
				ticks = 125;
			}

			damage *= bender.getDamageMult(Waterbending.ID);
			knockback.scale(damage);
			fireCannon(world, entity, damage, speed, size, ticks, maxRange, knockback);
			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1, 2);
			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);


			return true;
		}

		return false;

	}

	private void fireCannon(World world, EntityLivingBase entity, float damage, double speed, float size, float ticks, float maxRange, Vec3d knockBack) {

		EntityWaterCannon cannon = new EntityWaterCannon(world);

		cannon.setOwner(entity);
		cannon.setDamage(damage);
		cannon.setEntitySize(1.5F * size);
		cannon.setPosition(Vector.getEyePos(entity).minusY(0.8));
		cannon.setLifeTime((int) ticks);
		cannon.setXp(SKILLS_CONFIG.waterHit / 2);
		cannon.rotationPitch = entity.rotationPitch;
		cannon.rotationYaw = entity.rotationYaw;
		cannon.setTier(new AbilityWaterBlast().getCurrentTier(AbilityData.get(entity, "water_blast")));
		cannon.setAbility(new AbilityWaterBlast());

		Vector velocity = Vector.getLookRectangular(entity);
		velocity = velocity.normalize().times(speed);
		cannon.setSpeed((float) speed);
		cannon.setVelocity(velocity);
		if (!world.isRemote)
			world.spawnEntity(cannon);

		/*EntityLightCylinder cylinder = new EntityLightCylinder(world);
		cylinder.setShouldSpin(true);
		cylinder.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight() - 0.3, 0).add(entity.getLookVec().scale(0.4)));
		cylinder.setTexture("avatarmod:textures/entity/water-ribbon.png");
		cylinder.setLightRadius(0);
		cylinder.setLightAmount(0);
		//TODO: Later use colours instead of a texture, for colour shifting
		cylinder.setColorA(1F);
		cylinder.setColorB(1.0F);
		cylinder.setColorG(1.0F);
		cylinder.setColorR(1.0F);
		cylinder.setOwner(entity);
		cylinder.setType(EntityLightCylinder.EnumType.SQUARE);
		cylinder.setCylinderSize(size / 2);
		cylinder.setCylinderPitch(entity.rotationPitch);
		cylinder.setCylinderYaw(entity.rotationYaw);
		cylinder.setCylinderLength(1);
		cylinder.setBehaviour(new WaterCylinderBehaviour());**/
		//world.spawnEntity(cylinder);

	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID, "Water charge modifier", multiplier - 1, 1));

	}

	public static class WaterCylinderBehaviour extends LightCylinderBehaviour {

		@Override
		public Behavior onUpdate(EntityLightCylinder entity) {
			if (entity.getOwner() != null) {
				EntityWaterCannon cannon = AvatarEntity.lookupControlledEntity(entity.world, EntityWaterCannon.class, entity.getOwner());
				if (cannon != null) {
					entity.setCylinderLength(cannon.getDistance(entity.getOwner()));
					Vec3d height = entity.getOwner().getPositionVector().add(0, entity.getOwner().getEyeHeight() - 0.15, 0);
					Vec3d dist = cannon.getPositionVector().subtract(height).normalize();
					entity.setPosition(height.add(dist.scale(0.075)));
					AvatarEntityUtils.setRotationFromPosition(entity, cannon);
				} else {
					if (entity.ticksExisted > 1)
						entity.setDead();
				}
			}
			else entity.setDead();
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {

		}

		@Override
		public void toBytes(PacketBuffer buf) {

		}

		@Override
		public void load(NBTTagCompound nbt) {

		}

		@Override
		public void save(NBTTagCompound nbt) {

		}
	}

}


