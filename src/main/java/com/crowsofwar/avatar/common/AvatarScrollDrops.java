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
package com.crowsofwar.avatar.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarScrollDrops {
	
	private AvatarScrollDrops() {}
	
	@SubscribeEvent
	public void onMobDeath(LivingDeathEvent e) {
		
		EntityLivingBase entity = e.getEntityLiving();
		DamageSource source = e.getSource();
		
		if (source instanceof EntityDamageSource) {
			if (source.getEntity() instanceof EntityPlayer) {
				
				EntityPlayer player = (EntityPlayer) source.getEntity();
				
			}
		}
		
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AvatarScrollDrops());
	}
	
}
