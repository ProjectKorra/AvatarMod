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

//NOTE TO SELF: DO NOT MAKE REDIRECTS EVENT BASED, IT'S HOW YOU SCREW UP EVERYTHING
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireRedirectHandler {
}
