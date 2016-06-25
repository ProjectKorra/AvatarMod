package com.maxandnoah.avatar.client;

import com.google.common.eventbus.Subscribe;
import com.maxandnoah.avatar.common.entity.EntityFloatingBlock;
import com.maxandnoah.avatar.common.util.VectorUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class AvatarClientTick {
	
	public static final AvatarClientTick instance = new AvatarClientTick();
	public EntityFloatingBlock floating;
	
	private AvatarClientTick() {
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.side == Side.CLIENT) {
			if (floating != null && !floating.isLifting()) {
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				double yaw = Math.toRadians(player.rotationYaw);
				double pitch = Math.toRadians(player.rotationPitch);
				Vec3 forward = VectorUtils.fromYawPitch(yaw, pitch);
				Vec3 target = VectorUtils.plus(VectorUtils.times(forward, 2), VectorUtils.getEntityPos(player));
				
				floating.setPosition(target.xCoord, target.yCoord, target.zCoord);
			}
		}
	}
	
}
