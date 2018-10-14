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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireArc;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityFireArc extends EntityArc<EntityFireArc.FireControlPoint> {

	private static final DataParameter<FireArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireArc.class, FireArcBehavior.DATA_SERIALIZER);

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager
			.createKey(EntityFireArc.class, DataSerializers.FLOAT);
	private final ParticleSpawner particles;
	private float damageMult;
	private boolean createBigFire;
	private float Gravity;
	private float Size;
	private BlockPos position;


	public EntityFireArc(World world) {
		super(world);
		this.Size = 0.4F;
		this.damageMult = 1;
		this.Gravity = 9.82F;
		this.particles = new ClientParticleSpawner();
	}

	public float getGravity() {
		return this.Gravity;
	}

	public void setGravity(float gravity) {
		this.Gravity = gravity;
	}

	public void setStartingPosition(BlockPos position) {
		this.position = position;
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	@Override
	protected void updateCpBehavior() {
		super.updateCpBehavior();
		getControlPoint(0).setPosition(this.position());
		getLeader().setPosition(this.position().plusY(getSize() / 4));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireArcBehavior.Idle());
		dataManager.register(SYNC_SIZE, Size);
	}

	@Override
	public boolean onAirContact() {
		spawnExtinguishIndicators();
		setDead();
		return super.onAirContact();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getBehavior() == null) {
			this.setBehavior(new FireArcBehavior.Thrown());
		}
		FireArcBehavior newBehavior = (FireArcBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != newBehavior) setBehavior(newBehavior);

		if (getBehavior() != null && getBehavior() instanceof FireArcBehavior.PlayerControlled) {
			this.velocityMultiplier = 4;
			this.position = this.getPosition();
		} else this.velocityMultiplier = 8;

		if (getOwner() != null) {
			EntityFireArc arc = AvatarEntity.lookupControlledEntity(world, EntityFireArc.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (arc == null && bD.hasStatusControl(StatusControl.THROW_FIRE)) {
				bD.removeStatusControl(StatusControl.THROW_FIRE);
			}
			if (arc != null && arc.getBehavior() instanceof FireArcBehavior.PlayerControlled && !(bD.hasStatusControl(StatusControl.THROW_FIRE))) {
				bD.addStatusControl(StatusControl.THROW_FIRE);
			}

		}
		setSize(getSize() / 2, getSize() / 2);

		if (getOwner() == null) {
			this.setDead();
			cleanup();
		}
	}

	@Override
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		cleanup();
		setDead();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		spawnExtinguishIndicators();
		cleanup();
		setDead();
		return true;
	}

	public void cleanup() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_FIRE);
		}
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity && this.getBehavior() instanceof FireArcBehavior.Thrown) {
			((AvatarEntity) entity).onFireContact();
		}
		if (getBehavior() != null && getBehavior() instanceof FireArcBehavior.Thrown) {
			if (entity instanceof AvatarEntity) {
				if (!(((AvatarEntity) entity).getElement() instanceof Airbending)) {
					Firesplosion();
				}
			} else {
				Firesplosion();
			}
			if (getAbility() instanceof AbilityFireArc && !world.isRemote) {
				AbilityData data = AbilityData.get(getOwner(), "fire_arc");
				if (!data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					cleanup();
					this.setDead();
				}
			}


		}
	}

	@Override
	public boolean onCollideWithSolid() {

		if (!(getBehavior() instanceof FireArcBehavior.Thrown)) {
			return false;
		}

		Firesplosion();

		if (!world.isRemote) {
			int x = (int) Math.floor(posX);
			int y = (int) Math.floor(posY);
			int z = (int) Math.floor(posZ);
			BlockPos pos = new BlockPos(x, y, z);
			world.setBlockState(pos, Blocks.FIRE.getDefaultState());

			if (createBigFire) {
				for (EnumFacing dir : EnumFacing.HORIZONTALS) {
					BlockPos offsetPos = pos.offset(dir);
					if (world.isAirBlock(offsetPos)) {
						world.setBlockState(offsetPos, Blocks.FIRE.getDefaultState());
					}
				}
			}
			AbilityData abilityData = BendingData.get(Objects.requireNonNull(getOwner())).getAbilityData("fire_arc");
			if (!abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				cleanup();
				setDead();
			}

		}
		return true;
	}

	//Creates a FIRESPLOSION where it lands
	//Sorry for caps that was just fun to write :P
	public void Firesplosion() {
		if (!world.isRemote && world instanceof WorldServer) {


			float speed = 0.05F;
			float hitBox = 0.5F;
			int numberOfParticles = 250;

			if (getAbility() instanceof AbilityFireArc) {
				AbilityData abilityData = BendingData.get(Objects.requireNonNull(getOwner())).getAbilityData("fire_arc");
				int lvl = abilityData.getLevel();
				this.damageMult = lvl >= 2 ? 2 : 0.5F;
				//If the player's water arc level is level III or greater the aoe will do 2+ damage.
				hitBox = lvl <= 0 ? 0.5F : 0.5f * (lvl + 1);
				speed = lvl <= 0 ? 0.05F : 0.075F;
				numberOfParticles = lvl <= 0 ? 250 : 250 + 100 * lvl;
			} else this.damageMult = 0.5f;


			//if (CLIENT_CONFIG.useCustomParticles) {
			 	/*particles.spawnParticles(world, AvatarParticles.getParticleFlames(), 100, 200, Vector.getEntityPos(this),
						new Vector(speed * 50, speed * 50, speed * 10));**/
			//}
			//else {
			WorldServer World = (WorldServer) this.world;
			World.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, numberOfParticles, 0.2, 0.1, 0.2, speed);
			//}
			world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);

			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(1, 1, 1),
					entity -> entity != getOwner());
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (entity != getOwner() && entity != null && getOwner() != null && canCollideWith(entity)) {
						double distanceTravelled = entity.getDistance(this.position.getX(), this.position.getY(), this.position.getZ());

						Vector velocity = Vector.getEntityPos(entity).minus(Vector.getEntityPos(this));
						double distance = Vector.getEntityPos(entity).dist(Vector.getEntityPos(this));
						double direction = (hitBox - distance) * (speed * 5) / hitBox;
						velocity = velocity.times(direction).times(-1 + (-1 * hitBox / 2)).withY(speed / 2);

						double x = (velocity.x()) + distanceTravelled / 50;
						double y = (velocity.y()) > 0 ? velocity.y() + distanceTravelled / 100 : 0.3F + distanceTravelled / 100;
						double z = (velocity.z()) + distanceTravelled / 50;
						entity.addVelocity(x, y, z);
						if (canDamageEntity(entity)) {
							damageEntity(entity);
						}
						BattlePerformanceScore.addSmallScore(getOwner());

						if (entity instanceof AvatarEntity) {
							AvatarEntity avent = (AvatarEntity) entity;
							avent.addVelocity(x, y, z);
						}
						entity.isAirBorne = true;
						AvatarUtils.afterVelocityAdded(entity);
					}
				}
			}
		}

	}


	public void damageEntity(Entity entity) {
		if (canDamageEntity(entity)) {
			DamageSource ds = AvatarDamageSource.causeFireDamage(entity, getOwner());
			float damage = STATS_CONFIG.fireArcSettings.damage * damageMult;
			if (entity.attackEntityFrom(ds, damage)) {
				if (getOwner() != null && !world.isRemote && getAbility() != null) {
					BendingData data1 = BendingData.get(getOwner());
					AbilityData abilityData1 = data1.getAbilityData(getAbility().getName());
					abilityData1.addXp(SKILLS_CONFIG.fireHit);
					BattlePerformanceScore.addMediumScore(getOwner());

				}
			}
		}
	}

	@Override
	public FireControlPoint createControlPoint(float size, int index) {
		return new FireControlPoint(this, size, 0, 0, 0);
	}

	public FireArcBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(FireArcBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FireArcBehavior.PlayerControlled ? getOwner() : null;
	}

	public float getDamageMult() {
		return damageMult;
	}

	public void setDamageMult(float damageMult) {
		this.damageMult = damageMult;
	}

	public boolean getCreateBigFire() {
		return createBigFire;
	}

	public void setCreateBigFire(boolean createBigFire) {
		this.createBigFire = createBigFire;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	public static class FireControlPoint extends ControlPoint {

		public FireControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}

	}

}
