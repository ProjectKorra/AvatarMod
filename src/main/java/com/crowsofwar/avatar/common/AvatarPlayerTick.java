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

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import com.crowsofwar.avatar.common.event.AbilityUnlockEvent;
import com.crowsofwar.avatar.common.event.AbilityUseEvent;
import com.crowsofwar.avatar.common.event.ElementUnlockEvent;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarPlayerTick {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent e) {
		// Also forces loading of data on client
		if (Bender.isBenderSupported(e.player)) {
			Bender bender = Bender.get(e.player);
			if (bender != null) {
				BendingData data = bender.getData();

				EntityPlayer player = e.player;

				if (!player.world.isRemote && player.ticksExisted == 0) {
					data.saveAll();
				}

				if (e.phase == Phase.START) {
					bender.onUpdate();
				}

			}
		}

		//Removes particles.
		/*if (e.phase == Phase.END && Bender.get(e.player) != null) {
			List<EntityLivingBase> benders = e.player.world.getEntities(EntityLivingBase.class, Bender::isBenderSupported);
			boolean flamethrowerActive = false;
			if (!benders.isEmpty()) {
				for (EntityLivingBase entity : benders) {
					if (Bender.get(entity).getData().hasTickHandler(TickHandlerController.FLAMETHROWER) || Bender.get(entity).getData().hasStatusControl(StatusControl.STOP_FLAMETHROW))
						flamethrowerActive = true;
				}
			}
			if (Bender.get(e.player).getData().hasStatusControl(StatusControl.STOP_FLAMETHROW) || Bender.get(e.player).getData().hasTickHandler(TickHandlerController.FLAMETHROWER))
				flamethrowerActive = true;
			if (!flamethrowerActive) {
				System.out.println("Remove flamethrower");
				//ParticleBuilder.aliveParticles.removeIf(particleAvatar -> !particleAvatar.isAlive());
			}
		}**/
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void worldJoinEvent(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityLivingBase && Bender.isBenderSupported((EntityLivingBase) event.getEntity())) {
			if (SKILLS_CONFIG.startWithRandomBending && !event.getWorld().isRemote) {
				EntityLivingBase bender = (EntityLivingBase) event.getEntity();
				BendingData data = BendingData.get(bender);
				if (!data.hasElements()) {
					int elementID = AvatarUtils.getRandomNumberInRange(1, 4);
					List<BendingStyle> elements = BendingStyles.all().stream()
							.filter(bendingStyle -> bendingStyle.isParentBending() && bendingStyle.canEntityUse())
							.collect(Collectors.toList());
					BendingStyle style = BendingStyles.get(elements.get(elementID - 1).getName());
					if (!MinecraftForge.EVENT_BUS.post(new ElementUnlockEvent(bender, style))) {
						data.addBending(style);

						// Unlock first ability
						//noinspection ConstantConditions - can safely assume bending is present if
						// the ID is in use to unlock it
						Ability ability = Objects.requireNonNull(style.getAllAbilities().get(0));
						if (!MinecraftForge.EVENT_BUS.post(new AbilityUnlockEvent(bender, ability)))
							data.getAbilityData(ability).unlockAbility();

					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBendingUseEvent(AbilityUseEvent event) {
		if (event.getEntityLiving() != null) {
			if (BendingData.getFromEntity(event.getEntityLiving()) != null) {
				BendingData data = BendingData.getFromEntity(event.getEntityLiving());
				if (!data.hasBendingId(event.getAbility().getBendingId())) {
					event.setCanceled(true);
				}
			}
		}
	}

}
