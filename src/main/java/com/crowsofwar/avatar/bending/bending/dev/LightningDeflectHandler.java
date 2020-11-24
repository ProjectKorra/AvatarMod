package com.crowsofwar.avatar.bending.bending.dev;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.entity.EntityLightningArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class LightningDeflectHandler {

	@SubscribeEvent
	public static void onLightningArcPunch (AttackEntityEvent event) {
		Entity e = event.getTarget();
		EntityPlayer p = event.getEntityPlayer();
		if (p.getUniqueID().toString().equals("01535a73-ff8d-4d6c-851e-c71f89e936aa") || p.getName().equals("FavouriteDragon")) {
			if (e instanceof EntityLightningArc) {
				EntityLightningArc arc = (EntityLightningArc) event.getTarget();
				arc.setOwner(p);
				arc.setVelocity(Vector.getLookRectangular(p).times(arc.velocity().magnitude()));

			}
		}

	}

}
