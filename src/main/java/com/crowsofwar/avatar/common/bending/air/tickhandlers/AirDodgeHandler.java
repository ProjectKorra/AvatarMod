package com.crowsofwar.avatar.common.bending.air.tickhandlers;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

//@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class AirDodgeHandler extends TickHandler {
	public static final int maxCoolDown = 20;
	private int leftDown, rightDown, cooldown;

	public AirDodgeHandler(int id) {
		super(id);
	}
/*
	@SubscribeEvent
	public static void onAPress(InputEvent.KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		BendingData data = BendingData.get(player);
		if (data.hasBendingId(Airbending.ID)) {
			if (!data.hasTickHandler(TickHandlerController.AIR_DODGE)) {
				if (mc.gameSettings.keyBindLeft.isKeyDown()) {
					data.addTickHandler(TickHandlerController.AIR_DODGE);
				}
			}
			if (data.hasTickHandler(TickHandlerController.AIR_DODGE)) {
				if (mc.gameSettings.keyBindLeft.isKeyDown()) {
					float yaw = player.rotationYaw - 90;
					Vector velocity = Vector.toRectangular(Math.toRadians(yaw), 0);
					velocity = velocity.times(20);
					player.addVelocity(velocity.x(), velocity.y(), velocity.z());
					AvatarUtils.afterVelocityAdded(player);
					data.removeTickHandler(TickHandlerController.AIR_DODGE);
				}
			}
		}
	}**/

	@Override
	public boolean tick(BendingContext ctx) {
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
	//	if (FMLCommonHandler.instance().getSide().isClient()) MinecraftForge.EVENT_BUS.register(this);
		return duration <= 10;

	}
}
