package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityElementshard;
import com.crowsofwar.avatar.common.entity.data.ElementshardBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;


import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK;

public class StatCtrlThrowElementshard extends StatusControl {

	public StatCtrlThrowElementshard() {
		super(10, CONTROL_RIGHT_CLICK, RIGHT_OF_CROSSHAIR);
	}



	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();


		EntityElementshard elementshard = AvatarEntity.lookupControlledEntity(world, EntityElementshard.class, entity);

		if (elementshard != null && elementshard.getShardsLeft() > 0 && elementshard.getShardCooldown() <= 0) {
			AbilityData abilityData = ctx.getData().getAbilityData("element_shard");
			double speedMult = abilityData.getLevel() >= 1 ? 25 : 15;
			elementshard.addVelocity(Vector.getLookRectangular(entity).times(speedMult));
			elementshard.setBehavior(new ElementshardBehavior.Thrown());
			elementshard.setShardsLeft(elementshard.getShardsLeft() - 1);
			System.out.println(elementshard.getShardsLeft());
			elementshard.setShardCooldown(100);
			return false;

		}

		else return elementshard.getShardsLeft() == 0;
	}

}


