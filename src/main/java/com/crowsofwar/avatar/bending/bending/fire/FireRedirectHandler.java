package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.fire.statctrls.StatCtrlFireRedirect;
import com.crowsofwar.avatar.bending.bending.fire.statctrls.StatCtrlFireSplit;
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
import java.util.Objects;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireRedirectHandler {

    @SubscribeEvent
    public static void controlRedirect(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (BendingData.getFromEntity(entity) != null) {
            BendingData data = BendingData.getFromEntity(entity);
            assert data != null;
            boolean removeRedirect = false;
            boolean removeSplit = false;
            if (data.hasBending(new Firebending()) && !entity.world.isRemote) {
                if (AbilityData.get(entity, "fire_redirect") != null &&
                        Objects.requireNonNull(AbilityData.get(entity, "fire_redirect")).getLevel() > -1) {
                    AbilityData abilityData = AbilityData.get(entity, "fire_redirect");

                    if (data.getAllStatusControls() != null) {
                        List<StatusControl> controls = data.getAllStatusControls();
                        if (!controls.isEmpty()) {

                            if (controls.contains(StatusControlController.REDIRECT_FIRE)) {
                                for (StatusControl sc : controls) {
                                    if (!(sc instanceof StatCtrlFireRedirect))
                                        if (sc.getSubscribedControl() == AvatarControl.CONTROL_SHIFT)
                                            removeRedirect = true;
                                }
                            } if (controls.contains(StatusControlController.SPLIT_FIRE)) {
                                for (StatusControl sc : controls) {
                                    if (!(sc instanceof StatCtrlFireSplit))
                                        if (sc.getSubscribedControl() == AvatarControl.CONTROL_RIGHT_CLICK
                                                || sc.getSubscribedControl() == AvatarControl.CONTROL_RIGHT_CLICK_DOWN
                                                || sc.getSubscribedControl() == AvatarControl.CONTROL_RIGHT_CLICK_UP)
                                            removeSplit = true;
                                }
                            }

                        }
                    }

                }
                if (removeRedirect)
                    data.removeStatusControl(StatusControlController.REDIRECT_FIRE);
                else if (entity.ticksExisted % 20 == 0)
                    data.addStatusControl(StatusControlController.REDIRECT_FIRE);

                if (removeSplit)
                    data.removeStatusControl(StatusControlController.SPLIT_FIRE);
                else if (entity.ticksExisted % 20 == 0)
                    data.addStatusControl(StatusControlController.SPLIT_FIRE);
            }
        }
    }
}
