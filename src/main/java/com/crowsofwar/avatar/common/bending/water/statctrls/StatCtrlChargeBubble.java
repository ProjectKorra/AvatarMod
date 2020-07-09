package com.crowsofwar.avatar.common.bending.water.statctrls;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
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
			bubble.setSize(bubble.getSize() + 0.01F);
			bubble.setDegreesPerSecond(bubble.getDegreesPerSecond() + 1);
			bubble.setHealth(bubble.getHealth() + 0.02F);
			return bubble.getSize() > bubble.getMaxSize();
		} else return true;
	}
}
