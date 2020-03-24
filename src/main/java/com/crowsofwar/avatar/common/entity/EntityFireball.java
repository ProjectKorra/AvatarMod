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

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.THROW_FIREBALL;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityFireball extends AvatarEntity implements ILightProvider {

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityFireball.class,
			DataSerializers.VARINT);
	private static final DataParameter<FireballBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireball.class, FireballBehavior.DATA_SERIALIZER);
	private AxisAlignedBB expandedHitbox;

	private float damage;
	private BlockPos position;

	/**
	 * @param world
	 */
	public EntityFireball(World world) {
		super(world);
		setSize(.8f, .8f);
		this.position = this.getPosition();
		this.lightTnt = true;
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireballBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		/*if (world.isRemote) {
			for (double i = 0; i < width; i += 0.05) {
				Random random = new Random();
				AxisAlignedBB boundingBox = getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12).clr(255, 10, 5)
						.scale(getSize() * 0.03125F * 2).element(getElement()).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 60), 10)
						.scale(getSize() * 0.03125F * 2).element(getElement()).spawn(world);
			}
		}**/
		if (getBehavior() == null) {
			this.setBehavior(new FireballBehavior.Thrown());
		}
		setBehavior((FireballBehavior) getBehavior().onUpdate(this));
		if (ticksExisted % 30 == 0) {
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 6, 0.8F);
		}

		// Add hook or something
		if (getOwner() == null) {
			setDead();
			removeStatCtrl();
		}

		if (getOwner() != null) {
			EntityFireball ball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (ball == null && bD.hasStatusControl(THROW_FIREBALL)) {
				bD.removeStatusControl(THROW_FIREBALL);
			}
			if (ball != null && ball.getBehavior() instanceof FireballBehavior.PlayerControlled
					&& !(bD.hasStatusControl(THROW_FIREBALL))) {
				bD.addStatusControl(THROW_FIREBALL);
			}
			if (getBehavior() != null && getBehavior() instanceof FireballBehavior.PlayerControlled) {
				this.position = this.getPosition();
			}
		}
		//I'm using 0.03125, because that results in a size of 0.5F when rendering, as the default size for the fireball is actually 16.
		//This is due to weird rendering shenanigans
		setSize(getSize() * 0.03125F, getSize() * 0.03125F);
	}

	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		if (getBehavior() instanceof FireballBehavior.PlayerControlled) {
			removeStatCtrl();
		}
		setDead();
		removeStatCtrl();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		spawnExtinguishIndicators();
		return false;
	}

	public FireballBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(FireballBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FireballBehavior.PlayerControlled ? getOwner() : null;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(int size) {
		dataManager.set(SYNC_SIZE, size);
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onFireContact();
		}
		if (canCollideWith(entity) && entity != getOwner() && getBehavior() instanceof FireballBehavior.Thrown) {
			float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;

			explosionSize *= getSize() / 15f;
			explosionSize += getPowerRating() * 2.0 / 100;
			Explode(explosionSize);
		}
	}

	@Override
	public boolean onCollideWithSolid() {

		if (getBehavior() instanceof FireballBehavior.Thrown) {
			float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;

			explosionSize *= getSize() / 15f;
			explosionSize += getPowerRating() * 2.0 / 100;
			boolean destroyObsidian = false;

			if (getOwner() != null && !world.isRemote) {
				if (getAbility() instanceof AbilityFireball) {
					AbilityData abilityData = BendingData.get(getOwner()).getAbilityData("fireball");
					if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
						destroyObsidian = true;
					}
				}

			}

			Explode(explosionSize);

			if (destroyObsidian) {
				for (EnumFacing dir : EnumFacing.values()) {
					BlockPos pos = getPosition().offset(dir);
					if (world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
						world.destroyBlock(pos, true);
					}
				}
			}

			setDead();
			removeStatCtrl();

		}
		return true;

	}

	@Override
	public void setDead() {
		super.setDead();
		removeStatCtrl();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((FireballBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}

	@Override
	public int getBrightnessForRender() {
		return 150;
	}

	public AxisAlignedBB getExpandedHitbox() {
		return this.expandedHitbox;
	}

	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(0.35, 0.35, 0.35);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}
	// Mostly fixes a glitch where the entity turns invisible

	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			if (data != null) {
				data.removeStatusControl(THROW_FIREBALL);
			}
		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	public void Explode(float ExplosionSize) {
		if (world instanceof WorldServer) {
			float speed = ExplosionSize / 20;
			float hitBox = ExplosionSize + 0.5F;
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData("fireball");
				if (abilityData.getLevel() == 1) {
					speed = ExplosionSize / 8;
					hitBox = ExplosionSize + 1.5F;
				}
				if (abilityData.getLevel() >= 2) {
					speed = ExplosionSize / 4;
					hitBox = ExplosionSize + 4;
				}

				WorldServer World = (WorldServer) this.world;
				World.spawnParticle(AvatarParticles.getParticleFlames(), posX, posY, posZ, getSize() * 8, 0, 0, 0,
						getSize() / 25F);
				World.spawnParticle(EnumParticleTypes.LAVA, posX, posY, posZ, (int) (getSize() * 3.5), 0, 0, 0, speed);
				world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
						SoundCategory.BLOCKS, 4.0F,
						(1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
				List<Entity> collided = world.getEntitiesInAABBexcluding(this,
						getEntityBoundingBox().grow(hitBox, hitBox, hitBox), entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							if (canCollideWith(entity) && entity != getOwner()) {
								damageEntity(entity);

								//Divide the result of the position difference to make entities fly
								//further the closer they are to the player.
								double dist = (hitBox - entity.getDistance(entity)) > 1 ? (hitBox - entity.getDistance(entity)) : 1;
								Vector velocity = Vector.getEntityPos(entity).minus(Vector.getEntityPos(this));
								velocity = velocity.dividedBy(60).times(dist).withY(hitBox / 50);
								velocity = velocity.times(ExplosionSize);

								double x = (velocity.x());
								double y = (velocity.y()) > 0 ? velocity.y() : 0.25F;
								double z = (velocity.z());

								if (!entity.world.isRemote) {
									entity.motionX += velocity.x();
									entity.motionY += velocity.y();
									entity.motionZ += velocity.z();

									if (collided instanceof AvatarEntity) {
										if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment)
												&& !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
											AvatarEntity avent = (AvatarEntity) collided;
											avent.addVelocity(x, y, z);
											avent.onFireContact();
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
		}
		setDead();
	}

	public void damageEntity(Entity entity) {
		if (getOwner() != null) {
			AbilityData abilityData;
			if (getAbility() instanceof AbilityFireball) {
				abilityData = AbilityData.get(getOwner(), getAbility().getName());
				DamageSource ds = AvatarDamageSource.causeFireballDamage(entity, getOwner());
				if (entity.attackEntityFrom(ds, damage)) {
					abilityData.addXp(SKILLS_CONFIG.fireballHit);
					BattlePerformanceScore.addMediumScore(getOwner());

				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	@Optional.Method(modid = "albedo")
	public Light provideLight() {
		return Light.builder().pos(this).color(2F, 1F, 0F).radius(10).build();
	}

	@Override
	@Optional.Method(modid = "albedo")
	public void gatherLights(GatherLightsEvent event, Entity entity) {

	}
}
