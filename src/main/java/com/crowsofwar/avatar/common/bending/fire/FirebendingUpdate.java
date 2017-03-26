/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending.fire;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.*;

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
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
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			if (data != null && data.hasBending(BendingType.FIREBENDING)) {
				FirebendingState fs = (FirebendingState) data.getBendingState(BendingType.FIREBENDING);
				if (fs.isFlamethrowing() && player.ticksExisted % 3 < 2) {
					
					Chi chi = data.chi();
					float required = STATS_CONFIG.chiFlamethrowerSecond / 20f;
					
					boolean infinite = player.isCreative() && CHI_CONFIG.infiniteInCreative;
					
					if (chi.getAvailableChi() >= required || infinite) {
						
						if (!infinite) {
							chi.changeTotalChi(-required);
							chi.changeAvailableChi(-required);
						}
						
						Vector look = getLookRectangular(player);
						Vector eye = getEyePos(player);
						
						World world = data.getWorld();
						
						EntityFlames flames = new EntityFlames(world, player);
						flames.velocity().set(look.times(10).plus(getVelocityMpS(player)));
						flames.setPosition(eye.x(), eye.y(), eye.z());
						world.spawnEntityInWorld(flames);
						
						if (player.ticksExisted % 3 == 0) world.playSound(null, player.getPosition(),
								SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, 0.8f);
						
					} else {
						fs.setFlamethrowing(false);
					}
					
				}
			}
		}
	}
	
}
