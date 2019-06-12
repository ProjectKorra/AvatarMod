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
package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningArc;
import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningSpear;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightningFloodFill;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import elucent.albedo.lighting.LightManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Optional;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.bending.lightning.StatCtrlThrowLightningSpear.THROW_LIGHTNINGSPEAR;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityLightningSpear extends AvatarEntity implements ILightProvider {

	private static final DataParameter<LightningSpearBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityLightningSpear.class, LightningSpearBehavior.DATA_SERIALIZER);

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightningSpear.class,
			DataSerializers.FLOAT);

	private static final DataParameter<Float> SYNC_DEGREES_PER_SECOND = EntityDataManager.createKey(EntityLightningSpear.class,
			DataSerializers.FLOAT);


	private float damage;

	/**
	 * Whether the lightning spear can continue through multiple enemies, instead of being destroyed
	 * upon hitting one.
	 */
	private boolean piercing;

	/**
	 * Upon hitting an enemy, whether to damage any additional enemies next to the hit target.
	 */
	private boolean groupAttack;

	/**
	 * Handles electrocution of nearby entities when the lightning spear touches water
	 */
	private LightningFloodFill floodFill;

	private float Size;

	private float degreesPerSecond;

	private ParticleSpawner particleSpawner;

	private Vector velocity;
	//Prevents it from bouncing off of entities (for some reason canBePushed doesn't seem to work)

	/**
	 * @param world The world it spawns in
	 */
	public EntityLightningSpear(World world) {
		super(world);
		this.Size = 0.8F;
		this.degreesPerSecond = 400;
		setSize(Size, Size);
		this.damage = 3F;
		this.piercing = false;
		this.setInvisible(false);
		this.velocity = Vector.ZERO;
		this.particleSpawner = new NetworkParticleSpawner();

	}

	public void setTravellingVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	//So lightning spear doesn't bounce off of entities (if piercing)

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new LightningSpearBehavior.Idle());
		dataManager.register(SYNC_SIZE, Size);
		dataManager.register(SYNC_DEGREES_PER_SECOND, degreesPerSecond);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		LightningSpearBehavior.PlayerControlled controlled = new LightningSpearBehavior.PlayerControlled();
		setBehavior((LightningSpearBehavior) getBehavior().onUpdate(this));

		Vector clientPos = this.position();
		if (world.isRemote) {
			clientPos = this.position();
		}
		if (!world.isRemote) {
			if (this.position() != clientPos) {
				this.setPosition(clientPos);
			}
		}
		//For some reason the server position is inaccurate- setting it to the client side position massively reduces positioning glitchiness

		// Add hook or something
		if (getOwner() != null) {
			if (getBehavior() != null && getBehavior() instanceof LightningSpearBehavior.PlayerControlled) {
				this.rotationYaw = this.getOwner().rotationYaw;
				this.rotationPitch = this.getOwner().rotationPitch;
			}
		}


		this.setSize(getSize() / 2, getSize() / 2);
		//Even though doing size/8 would be better, the entity gets too small, and doesn't render far away enough. Super annoying.


		if (getOwner() != null) {
			EntityLightningSpear spear = AvatarEntity.lookupControlledEntity(world, EntityLightningSpear.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (spear == null && bD.hasStatusControl(THROW_LIGHTNINGSPEAR)) {
				bD.removeStatusControl(THROW_LIGHTNINGSPEAR);
			}
			if (spear != null && spear.getBehavior() == controlled && !(bD.hasStatusControl(THROW_LIGHTNINGSPEAR))) {
				bD.addStatusControl(THROW_LIGHTNINGSPEAR);
			}

		}
		// Electrocute enemies in water
		if (inWater) {

			// When in the water, lightning spear should disappear, but also keep
			// electrocuting entities. If the lightning spear was simply removed, flood fill
			// processing (i.e. electrocution) would end, so don't do that. Instead make it
			// invisible and remove once process is complete.
			// A hack but it works :\
			setInvisible(true);
			setVelocity(Vector.ZERO);

		} else {
			if (getBehavior() != null && getBehavior() instanceof LightningSpearBehavior.Thrown) {
				this.setVelocity(velocity);
			}
		}
		if (inWater && !world.isRemote) {
			if (floodFill == null) {
				floodFill = new LightningFloodFill(world, getPosition(), 12,
						this::handleWaterElectrocution);
			}
			if (floodFill.tick()) {
				// Remove lightning spear when it's finished electrocuting
				setDead();
			}
		}

	}

	public void LightningBurst() {
		if (getOwner() != null) {
			particleSpawner.spawnParticles(world, AvatarParticles.getParticleElectricity(), (int) (getSize() * 25), (int) (getSize() * 30), posX, posY, posZ, getSize() * 1.25, getSize() * 1.25, getSize() * 1.25);
			world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
			world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(getSize() * 2, getSize() * 2, getSize() * 2),
					entity -> entity != getOwner());

			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (entity != getOwner() && entity != null && getOwner() != null && entity != this && canCollideWith(entity)) {

						damageEntity(entity);

						//Divide the result of the position difference to make entities fly
						//further the closer they are to the player.
						double dist = (getSize() * 2 - entity.getDistance(entity)) > 1 ? (getSize() * 2 - entity.getDistance(entity)) : 1;
						Vector velocity = Vector.getEntityPos(entity).minus(Vector.getEntityPos(this));
						velocity = velocity.dividedBy(40).times(dist).withY(getSize() / 50);

						double x = (velocity.x());
						double y = (velocity.y()) > 0 ? velocity.y() : 0.2F;
						double z = (velocity.z());

						if (!entity.world.isRemote) {
							entity.addVelocity(x, y, z);

							if (collided instanceof AvatarEntity) {
								if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
									AvatarEntity avent = (AvatarEntity) collided;
									avent.addVelocity(x, y, z);
								}
								entity.isAirBorne = true;
								AvatarUtils.afterVelocityAdded(entity);
							}
						}
					}
				}
			}

		}
	}

	public void damageEntity(Entity entity) {
		if (getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			AbilityData abilityData = data.getAbilityData("lightning_spear");
			int lvl = abilityData.getLevel();
			float damage = getDamage() / 3;
			if (lvl == 1) {
				damage = getDamage() / 2;
			}
			if (lvl == 2) {
				damage = getDamage() / 1.5F;
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = getDamage();
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage = getDamage();
			}
			boolean attack = entity.attackEntityFrom(AvatarDamageSource.causeLightningSpearDamage(entity, getOwner()), damage);
			if (attack) {
				abilityData.addXp(SKILLS_CONFIG.lightningspearHit);
				BattlePerformanceScore.addMediumScore(getOwner());

			}
		}
	}

	/**
	 * When a lightning spear hits water, electricity spreads through the water and nearby
	 * entities are electrocuted. This method is called when an entity gets electrocuted.
	 */
	private void handleWaterElectrocution(Entity entity) {

		// Uses same DamageSource as lightning arc; this is intentional
		DamageSource damageSource = AvatarDamageSource.causeLightningDamage(entity, getOwner());

		if (entity.attackEntityFrom(damageSource, damage / 2)) {
			BattlePerformanceScore.addLargeScore(getOwner());
		}

	}

	public LightningSpearBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(LightningSpearBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof LightningSpearBehavior.PlayerControlled ? getOwner() : null;
	}


	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public boolean isPiercing() {
		return piercing;
	}

	public void setPiercing(boolean piercing) {
		this.piercing = piercing;
	}

	public boolean isGroupAttack() {
		return groupAttack;
	}

	public void setGroupAttack(boolean groupAttack) {
		this.groupAttack = groupAttack;
	}

	public float getDegreesPerSecond() {
		return dataManager.get(SYNC_DEGREES_PER_SECOND);
	}

	public void setDegreesPerSecond(float degrees) {
		dataManager.set(SYNC_DEGREES_PER_SECOND, degrees);
	}

	@Override
	public boolean onCollideWithSolid() {

		if (!(getBehavior() instanceof LightningSpearBehavior.Thrown)) {
			return false;
		}
		setInvisible(false);
		LightningBurst();
		setDead();
		return true;

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((LightningSpearBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}

	@Override
	public boolean canBePushed() {
		return false;
	}


	@Override
	public boolean onAirContact() {
		if (getAbility() instanceof AbilityLightningArc && !world.isRemote) {
			AbilityData aD = AbilityData.get(getOwner(), "lightning_spear");
			if (!aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				this.setDead();
			}
		}
		return true;
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (getBehavior() instanceof LightningSpearBehavior.Thrown && getBehavior() != null && !world.isRemote) {
			if (this.canCollideWith(entity) && entity != getOwner()) {
				if (getAbility() instanceof AbilityLightningSpear && !world.isRemote) {
					AbilityData aD = AbilityData.get(getOwner(), getAbility().getName());
					if (!aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
						LightningBurst();
						setDead();
					}
					LightningBurst();
				} else {
					LightningBurst();
				}
			}
			if (entity instanceof AvatarEntity) {
				if (((AvatarEntity) entity).getElement() instanceof Airbending) {
					this.onAirContact();
					((AvatarEntity) entity).onLightningContact();
				}
			}
		}
	}

	public void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			data.removeStatusControl(THROW_LIGHTNINGSPEAR);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	@Optional.Method(modid = "albedo")
	public Light provideLight() {
		return Light.builder().pos(this).color(1F, 3F, 3F).radius(15).build();
	}

	@Override
	@Optional.Method(modid = "albedo")
	public void gatherLights(GatherLightsEvent arg0, Entity arg1) {
		
	}
}
