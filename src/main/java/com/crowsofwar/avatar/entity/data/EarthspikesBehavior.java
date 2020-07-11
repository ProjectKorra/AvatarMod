/*
  This file is part of AvatarMod.

  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.entity.EntityEarthspike;
import com.crowsofwar.avatar.entity.EntityEarthspikeSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author Aang23
 */
public abstract class EarthspikesBehavior extends Behavior<EntityEarthspikeSpawner> {

	public static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("78723aa8-8d42-11e8-9eb6-529269fb1459");
	public static DataSerializer<EarthspikesBehavior> SERIALIZER = new Behavior.BehaviorSerializer<>();

	public static void register() {
		DataSerializers.registerSerializer(SERIALIZER);
		registerBehavior(Line.class);
		registerBehavior(Octopus.class);
		registerBehavior(Init.class);
	}

	public static class Init extends EarthspikesBehavior {

		@Override
		public Behavior onUpdate(EntityEarthspikeSpawner entity) {
			return entity.getType() == EntityEarthspikeSpawner.SpikesType.LINE ? new Line() : new Octopus();
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

	public static class Line extends EarthspikesBehavior {

		int ticks = 0;
		boolean spawned = false;

		@Override
		public Behavior onUpdate(EntityEarthspikeSpawner entity) {
			ticks++;

			// Do not run on the client side
			if (entity.getEntityWorld().isRemote) return this;

			World world = entity.getEntityWorld();
			EntityLivingBase owner = entity.getOwner();
			AbilityData abilityData = AbilityData.get(owner, entity.getAbility().getName());
			float frequency = STATS_CONFIG.earthspikeSettings.frequency;
			double damage = STATS_CONFIG.earthspikeSettings.damage;
			float size = STATS_CONFIG.earthspikeSettings.size * 0.25F;
			float xpModifier = abilityData.getTotalXp() / 400;

			switch (abilityData.getLevel()) {
				case 1:
					damage = STATS_CONFIG.earthspikeSettings.damage * 1.33;
					// 4
					size = STATS_CONFIG.earthspikeSettings.size * 0.5F;
					// 1.25
					break;
				case 2:
					frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.75F;
					// 3
					damage = STATS_CONFIG.earthspikeSettings.damage * 1.66;
					// 5
					size = STATS_CONFIG.earthspikeSettings.size * 0.75F;
					// 1.5
					break;
				case 3:
					// Flash Fissure
					frequency = STATS_CONFIG.earthspikeSettings.frequency * 0.5F;
					// 2
					damage = STATS_CONFIG.earthspikeSettings.damage * 2.25;
					// 7.5
					size = STATS_CONFIG.earthspikeSettings.size * 1F;
					// 2
			}

			// For some reason using *= or += seems to glitch out everything- that's why I'm
			// using tedious equations.

			size += ticks / 45F;
			size += xpModifier;

			damage += xpModifier;
			damage *= Objects.requireNonNull(Bender.get(owner)).getDamageMult(Earthbending.ID);

			if (entity != null) {
				if (ticks % frequency == 0 && ticks > frequency / 3) {
					// For some reason getting the duration too early made everything glitch out
					double duration = entity.getDuration();
					EntityEarthspike earthspike = new EntityEarthspike(world);
					earthspike.posX = entity.posX;
					earthspike.posY = entity.posY;
					earthspike.posZ = entity.posZ;
					earthspike.setAbility(Objects.requireNonNull(abilityData.getAbility()));
					earthspike.setDamage(damage);
					earthspike.setSize(size + ticks / (30f / size));
					earthspike.setLifetime(duration);
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);

					BlockPos below = earthspike.getPosition().offset(EnumFacing.DOWN);
					Block belowBlock = world.getBlockState(below).getBlock();
					world.playSound(null, earthspike.posX, earthspike.posY, earthspike.posZ,
							belowBlock.getSoundType().getBreakSound(), SoundCategory.BLOCKS, 1, 1);
					if (!world.isRemote) {
						WorldServer World = (WorldServer) world;
						World.spawnParticle(EnumParticleTypes.CRIT, earthspike.posX, earthspike.posY, earthspike.posZ,
								100, 0, 0, 0, 0.5);
					}
				}
			}

			return this;
		}

		@Override
		public void renderUpdate(EntityEarthspikeSpawner entity) {

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

	public static class Octopus extends EarthspikesBehavior {

		int ticks = 0;
		boolean spawned = false;

		@Override
		public Behavior onUpdate(EntityEarthspikeSpawner entity) {
			ticks++;

			// Do not run on the client side
			if (entity.getEntityWorld().isRemote) return this;

			World world = entity.getEntityWorld();
			EntityLivingBase owner = entity.getOwner();
			AbilityData abilityData = AbilityData.get(owner, entity.getAbility().getName());
			double damage;
			float size;
			float xpModifier = abilityData.getTotalXp() / 400;
			float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(ticks / 40f);

			// Octopus Fissure
			damage = STATS_CONFIG.earthspikeSettings.damage * 2.5;
			// 6
			size = STATS_CONFIG.earthspikeSettings.size * 1.25F;
			// 1.25

			size = (size + ticks / 45F) + xpModifier;

			damage = (damage + xpModifier) * Bender.get(owner).getDamageMult(Earthbending.ID);

			if (!spawned) {
				for (int i = 0; i < 8; i++) {
					Vector direction1 = Vector.toRectangular(Math.toRadians(entity.rotationYaw + i * 45), 0).times(1.4)
							.withY(0);
					EntityEarthspike earthspike = new EntityEarthspike(world);
					earthspike.setPosition(direction1.x() + entity.posX, entity.posY, direction1.z() + entity.posZ);
					earthspike.setDamage(damage);
					earthspike.setSize(size);
					earthspike.setOwner(owner);
					earthspike.setAbility(abilityData.getAbility());
					world.spawnEntity(earthspike);
					// Ring of instantaneous earthspikes.
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
				spawned = true;
				return this;
			}

			assert owner != null;
			applyMovementModifier(owner, MathHelper.clamp(movementMultiplier, 0.1f, 1));
			if (ticks % 15 == 0 && owner.onGround) {
				// Try using rotation yaw instead of circle particles
				for (int i = 0; i < 8; i++) {
					Vector direction1 = Vector.toRectangular(Math.toRadians(owner.rotationYaw + i * 45), 0).withY(0)
							.times(ticks / 5F);
					EntityEarthspike earthspike = new EntityEarthspike(world);
					if (direction1.x() + owner.posX != owner.posX && direction1.z() + owner.posZ != owner.posZ) {
						earthspike.setPosition(direction1.x() + owner.posX, owner.posY, direction1.z() + owner.posZ);
					}
					// Necessary so that the player doesn't spawn a center earthspike
					earthspike.setDamage(damage);
					earthspike.setSize(size);
					earthspike.setLifetime(20 + size * 2);
					earthspike.setOwner(owner);
					world.spawnEntity(earthspike);

					BlockPos below = earthspike.getPosition().offset(EnumFacing.DOWN);
					Block belowBlock = world.getBlockState(below).getBlock();
					world.playSound(null, earthspike.posX, earthspike.posY, earthspike.posZ,
							belowBlock.getSoundType().getBreakSound(), SoundCategory.BLOCKS, 1, 1);
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

			if (ticks >= 30) {
				owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);
			}

			return this;
		}

		private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

			IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

			moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

			moveSpeed.applyModifier(
					new AttributeModifier(MOVEMENT_MODIFIER_ID, "Earthspikes modifier", multiplier - 1, 1));

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
