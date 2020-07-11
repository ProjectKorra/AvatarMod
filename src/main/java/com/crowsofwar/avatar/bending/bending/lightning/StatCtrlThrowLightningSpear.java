package com.crowsofwar.avatar.bending.bending.lightning;

import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityLightningSpear;
import com.crowsofwar.avatar.entity.data.LightningSpearBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

public class StatCtrlThrowLightningSpear extends StatusControl {
	public StatCtrlThrowLightningSpear() {
		super(14, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();

		EntityLightningSpear spear = AvatarEntity.lookupControlledEntity(world, EntityLightningSpear.class, entity);

		if (spear != null) {
			AbilityData abilityData = ctx.getData().getAbilityData("lightning_spear");
			double speedMult = abilityData.getLevel() >= 1 ? 55 : 45;

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				speedMult = 80;
			}
			spear.setBehavior(new LightningSpearBehavior.Thrown());
			spear.setVelocity(Vector.getLookRectangular(entity).times(speedMult));
			Vector direction = spear.velocity().toSpherical();
			spear.rotationYaw = (float) Math.toDegrees(direction.y());
			spear.rotationPitch = (float) Math.toDegrees(direction.x());
		}

		return true;
	}

}

