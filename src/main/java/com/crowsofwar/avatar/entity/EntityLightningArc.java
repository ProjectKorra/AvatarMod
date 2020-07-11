package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.client.particle.AvatarParticles;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.bending.bending.lightning.AbilityLightningArc;
import com.crowsofwar.avatar.bending.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.entity.data.LightningFloodFill;
import com.crowsofwar.avatar.util.AvatarDataSerializers;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.joml.Matrix4d;
import org.joml.SimplexNoise;
import org.joml.Vector4d;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityLightningArc extends EntityArc<EntityLightningArc.LightningControlPoint> implements IGlowingEntity {

	//TODO: Find out why tf this is lagging my world so much.
	private static final DataParameter<Vector> SYNC_ENDPOS = EntityDataManager.createKey
			(EntityLightningArc.class, AvatarDataSerializers.SERIALIZER_VECTOR);

	private static final DataParameter<Float> SYNC_TURBULENCE = EntityDataManager.createKey
			(EntityLightningArc.class, DataSerializers.FLOAT);

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
			(EntityLightningArc.class, DataSerializers.FLOAT);

	private static final DataParameter<Boolean> SYNC_MAIN_ARC = EntityDataManager.createKey
			(EntityLightningArc.class, DataSerializers.BOOLEAN);

	/**
	 * If the lightning hits an entity, the lightning "sticks to" that entity and continues to
	 * damage it.
	 */
	@Nullable
	private EntityLivingBase stuckTo;

	/**
	 * If the lightning hits an entity or the ground, the lightning "sticks to" that position and
	 * will die after some time after getting stuck.
	 */
	private int stuckTime;

	/**
	 * Whether the lightning was <b>attempted to be redirected</b> by stuckTo (ie, the targeted
	 * player).
	 * <p>
	 * Even if stuckTo failed to redirect the lightning, wasRedirected will remain true, and stuckTo
	 * will not attempt to redirect lightning again (only gets one chance).
	 *
	 * @see #wasSuccessfullyRedirected
	 */
	private boolean wasRedirected;

	/**
	 * Whether the lightning was successfully redirected by the target mob. In this case, the
	 * lightning will no longer damage the target.
	 *
	 * @see #wasRedirected
	 */
	private boolean wasSuccessfullyRedirected;

	/**
	 * Whether the lightning was created through redirecting a first lightning. Not to be
	 * confused with {@link #wasRedirected}, which is whether a first lightning got redirected to
	 * make a second lightning.
	 */
	private boolean createdByRedirection;

	private float damage;

	private LightningFloodFill floodFill;

	public EntityLightningArc(World world) {
		super(world);
		setSize(0.5f, 0.5f);
		damage = 8;
		this.setsFires = true;
	}

	//Use a particle explosion instead of a shockwave
	private void LightningBurst(double x, double y, double z) {
		EntityShockwave wave = new EntityShockwave(world);
		wave.setAbility(getAbility());
		wave.setOwner(getOwner());
		wave.setParticleSpeed((getSizeMultiplier() / 4) / 5);
		wave.setParticle(AvatarParticles.getParticleElectricity());
		wave.setSpeed(0.5F + getSizeMultiplier() / 10);
		wave.setDamageSource(AvatarDamageSource.LIGHTNING);
		wave.setPosition(x, y + 0.3, z);
		wave.setParticleSpeed(0.05F);
		wave.setParticleAmount(1);
		wave.setParticleWaves(1);
		wave.setDamage(getDamage() / 2);
		wave.setRange(6 + getSizeMultiplier() * 2);
		wave.setRange(getSizeMultiplier());
		wave.setFire(3 + AbilityData.get(getOwner(), getAbility().getName()).getLevel() + 1);
		wave.setParticleController(54 - (getSizeMultiplier() * 7));
		wave.setSphere(true);
		wave.setElement(new Lightningbending());
		wave.setPerformanceAmount(10);
		wave.setKnockbackHeight(0.02);
		world.spawnEntity(wave);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_ENDPOS, Vector.ZERO);
		dataManager.register(SYNC_TURBULENCE, 0.6f);
		dataManager.register(SYNC_SIZE, 1f);
		dataManager.register(SYNC_MAIN_ARC, true);
	}

	@Override
	public int getAmountOfControlPoints() {
		return 12;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isMainArc()) {
			onUpdateMainArc();
		}


		if (getOwner() != null) {
			Vector controllerPos = Vector.getEyePos(getOwner());
			Vector endPosition = getEndPos();
			Vector position = controllerPos;

			// position slightly below eye height
			position = position.minusY(0.3);
			// position slightly away from controller
			position = position.plus(endPosition.minus(position).dividedBy(10));

			setEndPos(position);

			Vector newRotations = Vector.getRotationTo(position(), getEndPos());
			rotationYaw = (float) Math.toDegrees(newRotations.y());
			rotationPitch = (float) Math.toDegrees(newRotations.x());
		}
		if (stuckTo != null && getOwner() != null) {
			setPosition(Vector.getEyePos(stuckTo));
			setVelocity(Vector.ZERO);
			if (!wasSuccessfullyRedirected) {
				damageEntity(stuckTo, 0.333f);
			}
			getControlPoint(11).setPosition(Vector.getLookRectangular(getOwner()).times(2).plus(Vector.getEntityPos(getOwner())));
			getControlPoint(0).setPosition(Vector.getEntityPos(stuckTo));
		}


		if (velocity().equals(Vector.ZERO)) {
			stuckTime++;
			if (stuckTime == 1) {
				world.playSound(null, getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER,
						SoundCategory.PLAYERS, 1, 1);
			}
		}
		boolean existTooLong = stuckTime >= 40 || ticksExisted >= 200;
		boolean stuckIsDead = stuckTo != null && stuckTo.isDead;
		if (existTooLong || stuckIsDead) {
			setDead();
		}
		setSize(0.33f * getSizeMultiplier(), 0.33f * getSizeMultiplier());

	}

	/**
	 * @see #isMainArc()
	 */
	private void onUpdateMainArc() {

		// Lightning flash
		if (world.isRemote) {
			double threshold = stuckTime >= 0 ? 0.2 : 0.3;
			if (SimplexNoise.noise(ticksExisted * 2, 0) >= threshold) {
				world.setLastLightningBolt(2);
			}
		}
		// Electrocute enemies in water
		if (inWater && !world.isRemote) {
			if (floodFill == null) {
				floodFill = new LightningFloodFill(world, getPosition(), 12,
						this::handleWaterElectrocution);
			}
			floodFill.tick();
		}

	}

	@Override
	protected void updateCpBehavior() {
		if (getOwner() != null) {
			for (LightningControlPoint controlPoint : getControlPoints()) {
				controlPoint.setPosition(controlPoint.getPosition
						(ticksExisted));
			}
		}
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (getAbility() instanceof AbilityLightningArc && !world.isRemote && getOwner() != null) {
			AbilityData aD = AbilityData.get(getOwner(), "lightning_arc");
			if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND) && entity instanceof EntityLivingBase) {
				world.playSound(null, getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER,
						SoundCategory.PLAYERS, 1, 1);
				damageEntity(((EntityLivingBase) entity), 1);
				LightningBurst(this.posX, this.posY, this.posZ);
				//Don't use the position of the entity, as that makes them fall through the world
				entity.noClip = false;
				this.setDead();
			}
		}
		if (stuckTo == null && entity instanceof EntityLivingBase) {
			stuckTo = (EntityLivingBase) entity;

		}
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return entity != getOwner();
	}

	/**
	 * Custom lightning collision detection which uses raytrace. Required since lightning moves
	 * quickly and can sometimes "glitch" through an entity without detecting the collision.
	 */
	@Override
	protected void collideWithNearbyEntities() {

		if (getOwner() != null) {
			List<Entity> collisions = Raytrace.entityRaytrace(world, position(), getEntityPos(getOwner()).minus(this.position()), this.getDistance(getOwner()),
					entity -> entity != getOwner() && entity != this);

			for (Entity collided : collisions) {
				if (canCollideWith(collided)) {
					onCollideWithEntity(collided);
				}
			}

		}
	}

	private void handleWaterElectrocution(EntityLivingBase entity) {

		double distance = entity.getDistance(this);
		float damageModifier = (float) (1 - (distance / 12) * (distance / 12));
		damageEntity(entity, damageModifier);

	}

	private void damageEntity(EntityLivingBase entity, float damageModifier) {

		if (world.isRemote || getOwner() == null) {
			return;
		}
		if (!isMainArc()) {
			return;
		}

		// Handle lightning redirection
		if (!wasRedirected && isMainArc() && entity == stuckTo && Bender.isBenderSupported
				(entity)) {
			wasSuccessfullyRedirected = Objects.requireNonNull(Bender.get(entity)).redirectLightning(this);
			wasRedirected = true;
		}

		DamageSource damageSource = createDamageSource(entity);
		if (!wasSuccessfullyRedirected && entity.attackEntityFrom(damageSource, damage *
				damageModifier)) {

			BattlePerformanceScore.addLargeScore(getOwner());

			entity.setFire(4);

			Vector velocity = getEntityPos(entity).minus(this.position()).normalize();
			velocity = velocity.times(2);
			entity.addVelocity(velocity.x(), 0.4, velocity.z());
			AvatarUtils.afterVelocityAdded(entity);

			// Add Experience
			// Although 2 lightning entities are fired in each lightning ability, this won't
			// cause 2x XP rewards as this only happens when the entity is successfully attacked
			// (hurtResistantTime prevents the 2 lightning entities from both damaging at once)
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData("lightning_arc");
				abilityData.addXp(SKILLS_CONFIG.struckWithLightning);
			}
		}

	}

	private DamageSource createDamageSource(EntityLivingBase target) {
		if (createdByRedirection) {
			return AvatarDamageSource.causeRedirectedLightningDamage(target, getOwner());
		} else {
			return AvatarDamageSource.causeLightningDamage(target, getOwner());
		}
	}

	@Override
	public boolean onCollideWithSolid() {
		setDead();
		this.motionX = this.motionY = this.motionZ = 0;
		LightningBurst(posX, posY, posZ);
		return false;
	}

	@Override
	protected LightningControlPoint createControlPoint(float size, int index) {
		return new LightningControlPoint(this, index);
	}

	public Vector getEndPos() {
		return dataManager.get(SYNC_ENDPOS);
	}

	public void setEndPos(Vector endPos) {
		dataManager.set(SYNC_ENDPOS, endPos);
	}

	public float getTurbulence() {
		return dataManager.get(SYNC_TURBULENCE);
	}

	public void setTurbulence(float turbulence) {
		dataManager.set(SYNC_TURBULENCE, turbulence);
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getSizeMultiplier() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSizeMultiplier(float sizeMultiplier) {
		dataManager.set(SYNC_SIZE, sizeMultiplier);
	}

	/**
	 * The ability actually creates 2-3 arcs for aesthetic effect. However, some work (e.g. flood
	 * fill) should only be performed by one arc : The main arc. Of the many arcs spawned in each
	 * ability use, one is flagged as the main arc.
	 */
	public boolean isMainArc() {
		return dataManager.get(SYNC_MAIN_ARC);
	}

	/**
	 * @see #isMainArc()
	 */
	public void setMainArc(boolean mainArc) {
		dataManager.set(SYNC_MAIN_ARC, mainArc);
	}

	public boolean isCreatedByRedirection() {
		return createdByRedirection;
	}

	public void setCreatedByRedirection(boolean createdByRedirection) {
		this.createdByRedirection = createdByRedirection;
	}

	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(87, 161, 235).radius(10f).build();


	}


	public class LightningControlPoint extends ControlPoint {

		private final int index;

		public LightningControlPoint(EntityArc arc, int index) {
			super(arc, 0.1f, 0, 0, 0);
			this.index = index;
		}

		public Vector getPosition(float ticks) {

			float partialTicks = ticks - (int) ticks;
			Vector arcPos = arc.position().plus(arc.velocity().dividedBy(20).times(partialTicks));

			double targetDist = arcPos.dist(getEndPos()) / getControlPoints().size();
			Vector dir = Vector.getLookRectangular(arc);

			Vector normalPosition = arcPos.plus(dir.times(targetDist).times(index));
			Vector randomize = Vector.ZERO;

			if (index != arc.getControlPoints().size() - 1 && index != 0) {
				double actualOffX = SimplexNoise.noise(ticks / 25f * getTurbulence() + index / 1f,
						getEntityId
								() *
								1000) * getTurbulence();
				double actualOffY = SimplexNoise.noise(ticks / 25f * getTurbulence() + index / 1f,
						getEntityId() *
								2000) * getTurbulence();

				Matrix4d matrix = new Matrix4d();
				matrix.rotate(Math.toRadians(rotationYaw), 0, 1, 0);
				matrix.rotate(Math.toRadians(rotationPitch), 1, 0, 0);
				Vector4d randomJoml = new Vector4d(actualOffX, actualOffY, 0, 1);
				randomJoml.mul(matrix);

				randomize = new Vector(randomJoml.x, randomJoml.y, randomJoml.z);
			}

			return normalPosition.plus(randomize);
		}

		@Override
		public Vector getInterpolatedPosition(float partialTicks) {
			return getPosition(arc.ticksExisted + partialTicks);
		}


	}

}
