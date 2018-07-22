package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class SpawnEarthspikesHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("78723aa8-8d42-11e8-9eb6-529269fb1459");

	private final ParticleSpawner particles;

	public SpawnEarthspikesHandler() {
		particles = new NetworkParticleSpawner();
	}

	@Override
	public boolean tick(BendingContext ctx) {
		boolean stop = false;
		World world = ctx.getWorld();
		EntityLivingBase owner = ctx.getBenderEntity();
		AbilityData abilityData = AbilityData.get(owner, "earthspike");
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);

		float frequency = STATS_CONFIG.earthspikeSettings.frequency;
		//4 (by default)
		double damage = STATS_CONFIG.earthspikeSettings.damage;
		//3 (by default)
		float size = STATS_CONFIG.earthspikeSettings.size;
		//1 (by default)


		if (abilityData.getLevel() == 1) {
			damage = STATS_CONFIG.earthspikeSettings.damage * 1.33;
			//4
			size = STATS_CONFIG.earthspikeSettings.size * 1.25F;
			//1.25
		}

		if (abilityData.getLevel() == 2) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.75F;
			//3
			damage = STATS_CONFIG.earthspikeSettings.damage * 1.66;
			//5
			size = STATS_CONFIG.earthspikeSettings.size * 1.5F;
			//1.5

		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.5F;
			//2
			damage = STATS_CONFIG.earthspikeSettings.damage * 2;
			//6
			size = STATS_CONFIG.earthspikeSettings.size * 2F;
			//2
		}


		//For some reason using *= or += seems to glitch out everything- that's why
		//I'm using tedious equations.

		size += duration / 20;
		EntityEarthspikeSpawner entity = AvatarEntity.lookupControlledEntity(world, EntityEarthspikeSpawner.class, owner);

		if (!abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			if (entity != null) {
				if (duration % frequency == 0 && duration > frequency / 3) {
					EntityEarthspike earthspike = new EntityEarthspike(world);
					earthspike.posX = entity.posX;
					earthspike.posY = entity.posY;
					earthspike.posZ = entity.posZ;
					if (!world.isRemote) {
						particles.spawnParticles(world, EnumParticleTypes.BLOCK_CRACK, 100, 120, Vector.getEntityPos(earthspike).plusY(0.3),
								new Vector(1, 10, 1));
					}
					earthspike.setAbility(abilityData.getAbility());
					earthspike.setDamage(damage);
					earthspike.setSize(size);
					earthspike.setLifetime(entity.getDuration());
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);
				}
				return false;
			}
		} else {
			applyMovementModifier(owner, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			if (duration % 10 == 0) {
				//Try using rotation yaw instead of circle particles
				for (int i = 0; i < 8; i++) {
					Vector direction1 = Vector.toRectangular(Math.toRadians(owner.rotationYaw +
							i * 45), 0).withY(0).times(duration / 5);
					EntityEarthspike earthspike = new EntityEarthspike(world);
					if (direction1.x() + owner.posX != owner.posX && direction1.z() + owner.posZ != owner.posZ) {
						earthspike.setPosition(direction1.x() + owner.posX, owner.posY, direction1.z() + owner.posZ);
					}
					earthspike.setDamage(STATS_CONFIG.earthspikeSettings.damage * 2);
					earthspike.setSize(STATS_CONFIG.earthspikeSettings.size * 1.25F);
					earthspike.setLifetime(20);
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);
					//Ring of instantaneous earthspikes.
				}
			}
		}
		if (duration >= 20 && entity == null) {
			owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);
			stop = true;
		}
		return stop;
	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
				.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
				"Earthspike modifier", multiplier - 1, 1));

	}
}

