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
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class SpawnEarthspikesHandler extends TickHandler {

	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
			("78723aa8-8d42-11e8-9eb6-529269fb1459");

	//public static TickHandler SPAWN_EARTHSPIKES_HANDLER = new SpawnEarthspikesHandler();

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
			//size = STATS_CONFIG.earthspikeSettings.size * 1.25F;
			//1.25
		}

		if (abilityData.getLevel() == 2) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.75F;
			//3
			damage = STATS_CONFIG.earthspikeSettings.damage * 1.66;
			//5
			//size = STATS_CONFIG.earthspikeSettings.size * 1.5F;
			//1.5

		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.5F;
			//2
			damage = STATS_CONFIG.earthspikeSettings.damage * 2;
			//6
			//size = STATS_CONFIG.earthspikeSettings.size * 2F;
			//2
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			damage = STATS_CONFIG.earthspikeSettings.damage * 1.66;
			//5
			//size = STATS_CONFIG.earthspikeSettings.size;

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
					earthspike.setAbility(abilityData.getAbility());
					earthspike.setDamage(damage);
					earthspike.setSize(size);
					earthspike.setLifetime(entity.getDuration());
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);

					BlockPos below = earthspike.getPosition().offset(EnumFacing.DOWN);
					Block belowBlock = world.getBlockState(below).getBlock();
					world.playSound(null, earthspike.posX, earthspike.posY, earthspike.posZ,
							belowBlock.getSoundType().getBreakSound(),
							SoundCategory.BLOCKS, 1, 1);
					if (!world.isRemote) {
						WorldServer World = (WorldServer) world;
						for (int degree = 0; degree < 360; degree++) {
							double radians = Math.toRadians(degree);
							double x = Math.cos(radians) / 2 + earthspike.posX;
							double y = earthspike.posY;
							double z = Math.sin(radians) / 2 + earthspike.posZ;
							World.spawnParticle(EnumParticleTypes.CRIT, x, y, z, 1, 0, 0, 0, 0.5);

						}
					}
				}
				return false;
			}
		} else {
			applyMovementModifier(owner, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			if (duration % 10 == 0 && owner.onGround) {
				//Try using rotation yaw instead of circle particles
				for (int i = 0; i < 8; i++) {
					Vector direction1 = Vector.toRectangular(Math.toRadians(owner.rotationYaw +
							i * 45), 0).withY(0).times(duration / 5);
					EntityEarthspike earthspike = new EntityEarthspike(world);
					if (direction1.x() + owner.posX != owner.posX && direction1.z() + owner.posZ != owner.posZ) {
						earthspike.setPosition(direction1.x() + owner.posX, owner.posY, direction1.z() + owner.posZ);
					}
					earthspike.setDamage(damage);
					earthspike.setSize(size);
					earthspike.setLifetime(20);
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);

					BlockPos below = earthspike.getPosition().offset(EnumFacing.DOWN);
					Block belowBlock = world.getBlockState(below).getBlock();
					world.playSound(null, earthspike.posX, earthspike.posY, earthspike.posZ,
							belowBlock.getSoundType().getBreakSound(),
							SoundCategory.BLOCKS, 1, 1);
					if (!world.isRemote) {
						WorldServer World = (WorldServer) world;
						for (int degree = 0; degree < 360; degree++) {
							double radians = Math.toRadians(degree);
							double x = Math.cos(radians) / 2 + earthspike.posX;
							double y = earthspike.posY;
							double z = Math.sin(radians) / 2 + earthspike.posZ;
							World.spawnParticle(EnumParticleTypes.CRIT, x, y, z, 1, 0, 0, 0, 0.5);

						}
					}
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

