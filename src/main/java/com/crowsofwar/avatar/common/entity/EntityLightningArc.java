package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import org.joml.Matrix4d;
import org.joml.SimplexNoise;
import org.joml.Vector4d;

/**
 * @author CrowsOfWar
 */
public class EntityLightningArc extends EntityArc {

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

		if (ticksExisted % 3 == 0 || true) {

			double offsetX = rand.nextGaussian();
			double offsetY = rand.nextGaussian();

			for (int i = 0; i < getControlPoints().size(); i++) {

//				((LightningControlPoint) getControlPoint(i)).gotoNextPosition();

				ControlPoint controlPoint = getControlPoint(i);
				double targetDist = position().dist(getEndPos()) / getControlPoints().size();
				Vector dir = Vector.getLookRectangular(this);

				Vector normalPosition = position().plus(dir.times(targetDist).times(i));

				Vector randomize = Vector.ZERO;

//				if (i != getControlPoints().size() - 1) {
//				Matrix4d matrix = new Matrix4d();
//				matrix.rotate(Math.toRadians(rotationYaw), 0, 1, 0);
//				matrix.rotate(Math.toRadians(rotationPitch), 1, 0, 0);
//
//					/*
//					0  1 2  3 4
//					0 .5 1 .5 0
//
//					distFromCenter/maxDistFromCenter		// 1/2 = 0.5
//					distFromCenter = abs(center - pos)			// 3-2 = 1
//					maxDistFromCenter = abs(center - size - 1)
//
//					0   1   2   3
//					0   1   1   0
//					 */
//
//				double centerIndex = (getControlPoints().size() - 1) / 2.0;
//				double distFromCenter = Math.abs(centerIndex - i);
//				double maxDistFromCenter = centerIndex;
//
//				double interpolate = 1 - distFromCenter / maxDistFromCenter;
//
////				double actualOffX = offsetX * interpolate + rand.nextGaussian() * 0.2;
////				double actualOffY = offsetY * interpolate + rand.nextGaussian() * 0.2;
//
//				double actualOffX = SimplexNoise.noise(ticksExisted / 5f, i) * 0.4;
//				double actualOffY = SimplexNoise.noise(ticksExisted / 5f, i + 100) * 0.4;
//
//				System.out.println(ticksExisted + " -> " + actualOffX);
//
//				Vector4d randomJoml = new Vector4d(actualOffX, actualOffY, 0, 1);
//				randomJoml.mul(matrix);
//
//				randomize = new Vector(randomJoml.x, randomJoml.y, randomJoml.z);
////				}

//				controlPoint.setPosition(normalPosition.plus(randomize));

				controlPoint.setPosition(((LightningControlPoint) controlPoint).getPosition
						(ticksExisted /
						5f));

			}
		}
	}

	@Override
	protected ControlPoint createControlPoint(float size, int index) {
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

		/**
		 * Moves the ControlPoint to the randomized position
		 */
		public void gotoNextPosition() {
			ControlPoint next = arc.getLeader(index);

			if (index == 0) {
				// If leader, go to a new randomized position

				Matrix4d matrix = new Matrix4d();
				matrix.rotate(Math.toRadians(rotationYaw), 0, 1, 0);
				matrix.rotate(Math.toRadians(rotationPitch), 1, 0, 0);
				Vector4d randomJoml = new Vector4d(rand.nextGaussian(), rand.nextGaussian(), 0, 1);
				randomJoml.mul(matrix);

				Vector randomize = new Vector(randomJoml.x, randomJoml.y, randomJoml.z);
				setPosition(arc.position().plus(randomize));

			} else {
				// Other control points just go to leader pos
				setPosition(next.position());
			}

		}

		public Vector getPosition(float ticks) {
			ControlPoint controlPoint = getControlPoint(index);
			double targetDist = arc.position().dist(getEndPos()) / getControlPoints().size();
			Vector dir = Vector.getLookRectangular(arc);

			Vector normalPosition = arc.position().plus(dir.times(targetDist).times(index));

			double actualOffX = SimplexNoise.noise(ticks / 5f, index) * 0.4;
			double actualOffY = SimplexNoise.noise(ticks / 5f, index + 100) * 0.4;

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
			return getPosition(arc.ticksExisted / 5f + partialTicks);
		}

	}

}
