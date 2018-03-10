package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityElementshard;
import com.crowsofwar.avatar.common.entity.data.ElementshardBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
		AbilityElementshard abilityElementshard = new AbilityElementshard();

		if (elementshard != null && abilityElementshard.shardsAvailable > 0) {
			AbilityData abilityData = ctx.getData().getAbilityData("element_shard");
			double speedMult = abilityData.getLevel() >= 1 ? 25 : 15;
			elementshard.addVelocity(Vector.getLookRectangular(entity).times(speedMult));
			elementshard.setBehavior(new ElementshardBehavior.Thrown());
			abilityElementshard.shardsAvailable--;
			System.out.println(abilityElementshard.shardsAvailable);
			return false;
		}
		else if (abilityElementshard.shardsAvailable == 0){
			abilityElementshard.shardsAvailable = 4;
			return true;
		}
		 else return true;
	}

}


