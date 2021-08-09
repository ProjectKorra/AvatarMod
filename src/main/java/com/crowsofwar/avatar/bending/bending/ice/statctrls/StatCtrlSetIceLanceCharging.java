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

package com.crowsofwar.avatar.bending.bending.ice.statctrls;

import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.ice.Icebending;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.EnumHand;

import static com.crowsofwar.avatar.bending.bending.ice.tickhandlers.IceLanceHandler.ICE_LANCE_MOVEMENT_MOD;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_ICE_LANCE;
import static com.crowsofwar.avatar.util.data.TickHandlerController.ICE_LANCE_HANDLER;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSetIceLanceCharging extends StatusControl {

    private final boolean setting;

    public StatCtrlSetIceLanceCharging(boolean setting) {
        super(setting ? 11 : 12, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
                RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Icebending.ID)) {
            if (setting) {
                data.addStatusControl(RELEASE_ICE_LANCE);
                data.addTickHandler(ICE_LANCE_HANDLER, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ICE_LANCE_MOVEMENT_MOD) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_LANCE_MOVEMENT_MOD);
                //We don't remove the status control here since we want to spawn the tick handler if we stop right clicking.
                bender.swingArm(EnumHand.MAIN_HAND);
            }
        }

        return true;
    }

}
