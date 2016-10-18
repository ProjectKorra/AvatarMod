package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class FirebendingUpdate {
	
	public FirebendingUpdate() {
		
	}
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent e) {
		EntityPlayer player = e.player;
		if (!player.worldObj.isRemote) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(player);
			if (data != null && data.hasBending(BendingType.FIREBENDING)) {
				FirebendingState fs = (FirebendingState) data.getBendingState(BendingType.FIREBENDING);
				if (fs.isFlamethrowing() && player.ticksExisted % 3 < 2) {
					
					Vector look = Vector.fromEntityLook(player);
					Vector eye = Vector.getEyePos(player);
					
					World world = data.getWorld();
					
					EntityFlames flames = new EntityFlames(world, player);
					flames.velocity().set(look.times(10));
					flames.setPosition(eye.x(), eye.y(), eye.z());
					world.spawnEntityInWorld(flames);
					
				}
			}
		}
	}
	
}
