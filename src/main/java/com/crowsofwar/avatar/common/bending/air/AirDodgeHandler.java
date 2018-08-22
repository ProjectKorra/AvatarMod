package com.crowsofwar.avatar.common.bending.air;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

//@Mod.EventBusSubscriber(modid = AvatarInfo.MODID)

public class AirDodgeHandler extends TickHandler {

	public static final int MAX_CoolDown = 20;
	private int leftDown, rightDown, cooldown;

	@Override
	public boolean tick(BendingContext ctx) {
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
		if (FMLCommonHandler.instance().getSide().isClient()) MinecraftForge.EVENT_BUS.register(this);
		return duration <= 10;

	}

	@SubscribeEvent
	public static void onAPress(InputEvent.KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		BendingData data = BendingData.get(player);
		if (data != null) {
			if (data.hasBendingId(Airbending.ID)) {
				if (!data.hasTickHandler(AIR_DODGE)) {
					if (mc.gameSettings.keyBindLeft.isKeyDown()) {
						data.addTickHandler(AIR_DODGE);
					}
				}
				if (data.hasTickHandler(AIR_DODGE)) {
					if (mc.gameSettings.keyBindLeft.isKeyDown()) {
						float yaw = player.rotationYaw - 90;
						Vector velocity = Vector.toRectangular(Math.toRadians(yaw), 0);
						velocity = velocity.times(20);
						player.addVelocity(velocity.x(), velocity.y(), velocity.z());
						AvatarUtils.afterVelocityAdded(player);
						data.removeTickHandler(AIR_DODGE);
					}
				}
			}
		}
	}
}
