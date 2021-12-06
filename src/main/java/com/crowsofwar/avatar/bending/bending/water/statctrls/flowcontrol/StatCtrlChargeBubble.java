package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class StatCtrlChargeBubble extends StatusControl {
	public StatCtrlChargeBubble() {
		super(20, AvatarControl.CONTROL_SHIFT, CrosshairPosition.ABOVE_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class, entity);
		if (bubble != null) {
			bubble.setEntitySize(bubble.getAvgSize() + 0.01F);
			bubble.setDegreesPerSecond(bubble.getDegreesPerSecond() + 1);
			bubble.setHealth(bubble.getHealth() + 0.02F);
			return bubble.getAvgSize() > bubble.getMaxSize();
		} else return true;
	}
}