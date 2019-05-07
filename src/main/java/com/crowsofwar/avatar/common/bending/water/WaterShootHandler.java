package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.UUID;

public class WaterShootHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("6fdbbf2b-b7a8-4332-bd1f-6344687724bf");

	public WaterShootHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		AbilityData abilityData = ctx.getData().getAbilityData("water_cannon");
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		double powerRating = ctx.getBender().calcPowerRating(Waterbending.ID);
		int duration = data.getTickHandlerDuration(this);
		double range = abilityData.getLevel() >= 1 ? 40 : 60;
		float damage = 1;
		Vec3d knockback;
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
		//Multiply by 1.5 to get water cannon size
		float maxDuration = 50;
		if (world.isRemote) {
			return false;
		}
		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
		range += powerRating / 15;

		if (abilityData.getLevel() >= 1) {
			maxDuration = 75;
		}
		if (abilityData.getLevel() >= 2) {
			maxDuration = 100;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			maxDuration = 125;
		}

		EntityWaterCannon cannon = AvatarEntity.lookupControlledEntity(world, EntityWaterCannon.class, entity);
		if (cannon != null) {
			HashSet<Entity> excluded = new HashSet<Entity>();
			excluded.add(cannon);
			excluded.add(entity);
			Vector eyePos = Vector.getEyePos(entity).minus(0, 0.3, 0);
			Vector directionToEnd = cannon.position().minus(eyePos).normalize();
			Vector startPos = (eyePos.plus(directionToEnd.times(0.5)));
			Vec3d endPos = (startPos.withY(startPos.y())).toMinecraft().add(entity.getLookVec().scale(range));
			RayTraceResult result = Raytrace.handlePiercingBeamCollision(world, entity, startPos.toMinecraft(), endPos, 1.5F * cannon.getSizeMultiplier(),
					cannon, AvatarDamageSource.WATER, damage, knockback, false, 0, 1.5F * cannon.getSizeMultiplier());
			if (result != null) {
				if (result.entityHit != null && result.hitVec.distanceTo(entity.getPositionVector()) < range)
				cannon.posX = result.hitVec.x;
			}
		}
		return (duration >= maxDuration) && cannon != null;
	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID, "Water charge modifier", multiplier - 1, 1));

	}
}
