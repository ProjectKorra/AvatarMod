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

package com.crowsofwar.avatar.bending.bending.custom.hyper.statctrls;

import com.crowsofwar.avatar.bending.bending.custom.hyper.Hyperbending;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import static com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers.HyperBeamHandler.HYPER_BEAM_MOVEMENT_MOD_ID;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.TickHandlerController.CHARGE_HYPER_BEAM;
import static com.crowsofwar.avatar.util.data.TickHandlerController.HYPER_BEAM_HANDLER;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSetHyperBeam extends StatusControl {

    private final boolean setting;

    public StatCtrlSetHyperBeam(boolean setting) {
        super(setting ? 4 : 5, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
                RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Hyperbending.ID)) {
            if (setting) {
                data.addStatusControl(StatusControlController.SHOOT_HYPER_BEAM);
                data.addTickHandler(CHARGE_HYPER_BEAM, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(HYPER_BEAM_MOVEMENT_MOD_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HYPER_BEAM_MOVEMENT_MOD_ID);
                data.removeTickHandler(HYPER_BEAM_HANDLER, ctx);
                data.removeTickHandler(CHARGE_HYPER_BEAM, ctx);
            }
        }

        return true;
    }

}
