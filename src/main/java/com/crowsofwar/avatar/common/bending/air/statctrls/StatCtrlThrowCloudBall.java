package com.crowsofwar.avatar.common.bending.air.statctrls;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.common.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

public class StatCtrlThrowCloudBall extends StatusControl {
	public StatCtrlThrowCloudBall() {
		super(16, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getData().getAbilityData("cloudburst");
		double speed = 12.5;

		if (abilityData.getLevel() == 1) {
			speed = 15;
		}

		if (abilityData.getLevel() == 2) {
			speed = 17.5;
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			speed = 20;
		}

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			speed = 22.5;
		}


		EntityCloudBall cloudBall = AvatarEntity.lookupControlledEntity(world, EntityCloudBall.class, entity);

		if (cloudBall != null) {
			cloudBall.setBehavior(new CloudburstBehavior.Thrown());
			cloudBall.setLifeTime(30);
			cloudBall.setVelocity(Vector.getLookRectangular(entity).times(speed * 1.5F));
			//ctx.getData().addTickHandler(AIR_STATCTRL_HANDLER);
		}

		return true;
	}

}
//REGISTER THIS TO SEE IF IT FIXES ITSELF
//Umm Idk what the line above is referring to- cloudburst is pretty much fixed except for the occasional invisibility
//weirdness.

