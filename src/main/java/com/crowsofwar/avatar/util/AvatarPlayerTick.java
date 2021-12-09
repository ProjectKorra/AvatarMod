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

package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.event.AbilityUnlockEvent;
import com.crowsofwar.avatar.util.event.AbilityUseEvent;
import com.crowsofwar.avatar.util.event.ElementUnlockEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;

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

                //Why's this here? (why is it server side)
                //Note: originally every 50 seconds
                if (!player.world.isRemote && player.ticksExisted == 2) {
                    data.saveAll();
                }

                if (e.phase == Phase.START) {
                    bender.onUpdate();
                }

            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (Bender.isBenderSupported(event.player) && event.player instanceof EntityPlayerMP) {
            Ability.syncProperties(event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP && Bender.isBenderSupported(event.getEntityLiving())) {
            Ability.syncProperties((EntityPlayer) event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        // When a player logs in, they are sent the glyph data, server settings and spell properties.
        if (event.player instanceof EntityPlayerMP) {
            Ability.syncProperties(event.player);
            if (SKILLS_CONFIG.startWithRandomBending && !event.player.world.isRemote) {
                EntityLivingBase bender = event.player;
                if (Bender.isBenderSupported(bender)) {
                    BendingData data = BendingData.getFromEntity(bender);
                    //Applies a config modifier
                    Bender.adjustConfigModifier(bender);
                    if (data != null && !data.hasElements()) {
                        List<BendingStyle> elements = BendingStyles.all().stream()
                                .filter(bendingStyle -> bendingStyle.isParentBending() && bendingStyle.canEntityUse())
                                .collect(Collectors.toList());
                        int elementID = AvatarUtils.getRandomNumberInRange(1, elements.size());
                        BendingStyle style = BendingStyles.get(elements.get(elementID - 1).getName());
                        if (!MinecraftForge.EVENT_BUS.post(new ElementUnlockEvent(bender, style))) {
                            data.addBendingId(style == null ? Airbending.ID : style.getId());

                            // Unlock first ability
                            //noinspection ConstantConditions - can safely assume bending is present if
                            // the ID is in use to unlock it
                            assert style != null;
                            Ability ability = Objects.requireNonNull(style.getAllAbilities().get(0));
                            if (!MinecraftForge.EVENT_BUS.post(new AbilityUnlockEvent(bender, ability)))
                                data.getAbilityData(ability).unlockAbility();
                        }
                    }
                }
            }
        }
        if (event.player != null)
            Bender.adjustConfigModifier(event.player);
    }

    //Prevents keybinds from letting you use abilities you haven't learned
    @SubscribeEvent
    public static void onBendingUseEvent(AbilityUseEvent event) {
        if (event.getEntityLiving() != null) {
            BendingData data = BendingData.getFromEntity(event.getEntityLiving());
            if (data != null && !data.hasBendingId(event.getAbility().getBendingId())) {
                event.setCanceled(true);
            }
        }
    }

}
