package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.joml.Matrix4d;
import org.joml.SimplexNoise;
import org.joml.Vector4d;

/**
 * @author CrowsOfWar
 */
public class EntityLightningArc extends EntityArc<EntityLightningArc.LightningControlPoint> {

	private static final DataParameter<Vector> SYNC_ENDPOS = EntityDataManager.createKey
			(EntityLightningArc.class, AvatarDataSerializers.SERIALIZER_VECTOR);

	public EntityLightningArc(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_ENDPOS, Vector.ZERO);
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
		if (ticksExisted > 40) {
			setDead();
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
		if (entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 8)) {
			entity.setFire(4);

			Vector velocity = velocity().normalize().times(3);
			entity.addVelocity(velocity.x(), 0.6, velocity.z());
			AvatarUtils.afterVelocityAdded(entity);

			setDead();
		}
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		return entity != getOwner();
	}

	@Override
	public boolean onCollideWithSolid() {
		setDead();
		if (!world.isRemote) {
			world.setBlockState(getPosition(), Blocks.FIRE.getDefaultState());
		}
		return true;
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

	public class LightningControlPoint extends ControlPoint {

		private final int index;

		public LightningControlPoint(EntityArc arc, int index) {
			super(arc, 0.15f, 0, 0, 0);
			this.index = index;
		}

		public Vector getPosition(float ticks) {
			double targetDist = arc.position().dist(getEndPos()) / getControlPoints().size();
			Vector dir = Vector.getLookRectangular(arc);

			Vector normalPosition = arc.position().plus(dir.times(targetDist).times(index));

			double actualOffX = SimplexNoise.noise(ticks / 15f + index / 1f, getEntityId() *
					1000) * 0.6;
			double actualOffY = SimplexNoise.noise(ticks / 15f + index / 1f, getEntityId() *
					2000) * 0.6;

			Matrix4d matrix = new Matrix4d();
			matrix.rotate(Math.toRadians(rotationYaw), 0, 1, 0);
			matrix.rotate(Math.toRadians(rotationPitch), 1, 0, 0);
			Vector4d randomJoml = new Vector4d(actualOffX, actualOffY, 0, 1);
			randomJoml.mul(matrix);

			Vector randomize = new Vector(randomJoml.x, randomJoml.y, randomJoml.z);

			return normalPosition.plus(randomize);
		}

		@Override
		public Vector getInterpolatedPosition(float partialTicks) {
			return getPosition(arc.ticksExisted + partialTicks);
		}

	}

}
