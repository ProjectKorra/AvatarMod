package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.earth.statctrls.StatCtrlEarthRedirect;
import com.crowsofwar.avatar.bending.bending.fire.statctrls.StatCtrlFireRedirect;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class EarthRedirectHandler {

    @SubscribeEvent
    public static void controlRedirect(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (BendingData.getFromEntity(entity) != null) {
            BendingData data = BendingData.getFromEntity(entity);
            assert data != null;
            boolean removeRedirect = false;
            if (data.hasBending(new Earthbending()) && !entity.world.isRemote) {
                AbilityData abilityData = AbilityData.get(entity, "earth_redirect");
                if (abilityData != null && !abilityData.isLocked()) {
                    if (data.getAllStatusControls() != null) {
                        List<StatusControl> controls = data.getAllStatusControls();
                        if (!controls.isEmpty()) {

                            if (controls.contains(StatusControlController.EARTH_REDIRECT)) {
                                for (StatusControl sc : controls) {
                                    if (!(sc instanceof StatCtrlEarthRedirect))
                                        if (sc.getSubscribedControl() == AvatarControl.CONTROL_SHIFT)
                                            removeRedirect = true;
                                }
                            }

                        }
                    }

                }
                if (removeRedirect)
                    data.removeStatusControl(StatusControlController.EARTH_REDIRECT);
                else if (entity.ticksExisted % 20 == 0)
                    data.addStatusControl(StatusControlController.EARTH_REDIRECT);
            }
        }
    }
}
