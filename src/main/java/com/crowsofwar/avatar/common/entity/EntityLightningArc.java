package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningArc;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.LightningFloodFill;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.joml.Matrix4d;
import org.joml.SimplexNoise;
import org.joml.Vector4d;

import javax.annotation.Nullable;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

/**
 * @author CrowsOfWar
 */
public class EntityLightningArc extends EntityArc<EntityLightningArc.LightningControlPoint> {

	private static final DataParameter<Vector> SYNC_ENDPOS = EntityDataManager.createKey
			(EntityLightningArc.class, AvatarDataSerializers.SERIALIZER_VECTOR);

	private static final DataParameter<Float> SYNC_TURBULENCE = EntityDataManager.createKey
			(EntityLightningArc.class, DataSerializers.FLOAT);

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey
			(EntityLightningArc.class, DataSerializers.FLOAT);

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

	private float damage;

	private LightningFloodFill floodFill;

	public EntityLightningArc(World world) {
		super(world);
		setSize(0.5f, 0.5f);
		damage = 8;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_ENDPOS, Vector.ZERO);
		dataManager.register(SYNC_TURBULENCE, 0.6f);
		dataManager.register(SYNC_SIZE, 1f);
	}

	@Override
	public int getAmountOfControlPoints() {
		return 6;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getOwner() != null) {
			Vector ownerPosition = Vector.getEyePos(getOwner());
			Vector endPosition = getEndPos();
			Vector position = ownerPosition;

			// position slightly below eye height
			position = position.minusY(0.3);
			// position slightly away from owner
			position = position.plus(endPosition.minus(position).dividedBy(10));

			setEndPos(position);

			Vector newRotations = Vector.getRotationTo(position(), getEndPos());
			rotationYaw = (float) Math.toDegrees(newRotations.y());
			rotationPitch = (float) Math.toDegrees(newRotations.x());
		}
		if (stuckTo != null) {
			setPosition(Vector.getEyePos(stuckTo));
			setVelocity(Vector.ZERO);
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

		// Lightning flash
		if (world.isRemote) {
			double threshold = stuckTime >= 0 ? 0.2 : 0.3;
			if (SimplexNoise.noise(ticksExisted * 2, 0) >= threshold) {
				world.setLastLightningBolt(2);
			}
		}

		setSize(0.33f * getSizeMultiplier(), 0.33f * getSizeMultiplier());

		if (inWater && !world.isRemote) {
			if (floodFill == null) {
				floodFill = new LightningFloodFill(world, getPosition(), 5,
						this::handleWaterElectrocution);
			}
			floodFill.tick();
		}

	}

	@Override
	protected void updateCpBehavior() {
		for (LightningControlPoint controlPoint : getControlPoints()) {

			controlPoint.setPosition(controlPoint.getPosition
					(ticksExisted));

		}
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (stuckTo == null && entity instanceof EntityLivingBase) {

			stuckTo = (EntityLivingBase) entity;
			damageEntity(stuckTo);

		}
	}

	private void handleWaterElectrocution(EntityLivingBase entity) {

		if (entity != getOwner()) {
			damageEntity(entity);
		}

	}

	private void damageEntity(EntityLivingBase entity) {

		double distance = entity.getDistanceSqToEntity(this);
		float damageModifier = (float) (1 - distance);

		if (entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, damage * damageModifier)) {

			entity.setFire(4);

			Vector velocity = getEntityPos(entity).minus(this.position());
			velocity = velocity.times(0.2);
			entity.addVelocity(velocity.x(), 0.4, velocity.z());
			AvatarUtils.afterVelocityAdded(entity);

			// Add Experience
			// Although 2 lightning entities are fired in each lightning ability, this won't
			// cause 2x XP rewards as this only happens when the entity is successfully attacked
			// (hurtResistantTime prevents the 2 lightning entities from both damaging at once)
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				AbilityData abilityData = data.getAbilityData(AbilityLightningArc.ID);
				abilityData.addXp(SKILLS_CONFIG.struckWithLightning);
			}
		}

	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		return entity != getOwner();
	}

	@Override
	public boolean onCollideWithSolid() {
//		setDead();
		setVelocity(Vector.ZERO);
		if (!world.isRemote) {
			if (world.isAirBlock(getPosition())) {
				world.setBlockState(getPosition(), Blocks.FIRE.getDefaultState());
			}
		}
		return false;
//		return true;
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
