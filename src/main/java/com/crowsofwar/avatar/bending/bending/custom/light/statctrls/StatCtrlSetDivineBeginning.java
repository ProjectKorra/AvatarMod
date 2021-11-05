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

package com.crowsofwar.avatar.bending.bending.custom.light.statctrls;

import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.bending.bending.custom.light.Lightbending;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import static com.crowsofwar.avatar.bending.bending.custom.dark.tickhandlers.OblivionBeamHandler.OBLIVION_BEAM_MOVEMENT_MOD_ID;
import static com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers.ChargeDivineBeginning.DIVINE_BEGINNING_MOD_ID;
import static com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers.DivineBeginningHandler.DIVINE_BEGINNING_MOVEMENT_ID;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_DIVINE_BEGINNING;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_OBLIVION_BEAM;
import static com.crowsofwar.avatar.util.data.TickHandlerController.*;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSetDivineBeginning extends StatusControl {

    private final boolean setting;

    public StatCtrlSetDivineBeginning(boolean setting) {
        super(setting ? 4 : 5, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
                RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Lightbending.ID)) {
            if (setting) {
                data.addStatusControl(RELEASE_DIVINE_BEGINNING);
                data.addTickHandler(DIVINE_BEGINNING_CHARGER, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(DIVINE_BEGINNING_MOD_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(DIVINE_BEGINNING_MOD_ID);
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(DIVINE_BEGINNING_MOVEMENT_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(DIVINE_BEGINNING_MOVEMENT_ID);
                data.removeTickHandler(DIVINE_BEGINNING_HANDLER, ctx);
                data.removeTickHandler(DIVINE_BEGINNING_CHARGER, ctx);
            }
        }

        return true;
    }

}
