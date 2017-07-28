package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.world.World;
import org.joml.Matrix4d;
import org.joml.Vector4d;

/**
 * @author CrowsOfWar
 */
public class EntityLightningArc extends EntityArc {

	public EntityLightningArc(World world) {
		super(world);
	}

	@Override
	public int getAmountOfControlPoints() {
		return 6;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	protected void updateCpBehavior() {

		if (ticksExisted % 10 == 1) {
			for (int i = 0; i < getControlPoints().size(); i++) {

				ControlPoint controlPoint = getControlPoint(i);
				double targetDist = 1;
				Vector dir = Vector.getLookRectangular(this);

				Vector normalPosition = position().minus(dir.times(targetDist).times(i));

				Vector randomize = Vector.ZERO;

				if (i != getControlPoints().size() - 1) {
					Matrix4d matrix = new Matrix4d();
					matrix.rotate(Math.toRadians(rotationYaw), 0, 1, 0);
					matrix.rotate(Math.toRadians(rotationPitch), 1, 0, 0);
					Vector4d randomJoml = new Vector4d(rand.nextGaussian(), rand.nextGaussian(), 0, 1);
					randomJoml.mul(matrix);

					randomize = new Vector(randomJoml.x, randomJoml.y, randomJoml.z);
				}

				controlPoint.setPosition(normalPosition.plus(randomize));

			}
		}
	}

}
