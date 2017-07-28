package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.world.World;

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
		if (ticksExisted % 30 == 0) {
			for (int i = 1; i < getControlPoints().size(); i++) {

				ControlPoint controlPoint = getControlPoint(i);
				double targetDist = 1;
				Vector dir = Vector.getLookRectangular(this);

				Vector normalPosition = position().minus(dir.times(targetDist).times(i));
				Vector randomize = new Vector(rand.nextGaussian() * 3, rand.nextGaussian() * 3,
						rand.nextGaussian() * 3);

				controlPoint.setPosition(normalPosition.plus(randomize));

			}
		}
	}

}
