package com.crowsofwar.avatar.bending.bending.air.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import static com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler.AIRBURST_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_AIR_BURST;

public class StatCtrlShootAirburst extends StatusControl {


	public StatCtrlShootAirburst() {
		super(16, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		BendingData data = ctx.getData();
		if (ctx.getData().hasTickHandler(TickHandlerController.AIRBURST_CHARGE_HANDLER)) {
			data.addTickHandler(TickHandlerController.SHOOT_AIRBURST);
			AttributeModifier mod = ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
					.getModifier(AIRBURST_MOVEMENT_MODIFIER_ID);
			if (mod != null) {
				ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(mod);
			}
		}
		data.removeTickHandler(TickHandlerController.AIRBURST_CHARGE_HANDLER);
		data.removeStatusControl(RELEASE_AIR_BURST);
		return true;
	}
}
