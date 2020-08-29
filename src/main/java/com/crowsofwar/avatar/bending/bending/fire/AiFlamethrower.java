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
package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.data.StatusControlController.START_FLAMETHROW;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_FLAMETHROW;
import static com.crowsofwar.avatar.util.data.TickHandlerController.FLAMETHROWER;

/**
 * @author CrowsOfWar
 */
public class AiFlamethrower extends BendingAi {

    protected AiFlamethrower(Ability ability, EntityLiving entity, Bender bender) {
        super(ability, entity, bender);
        setMutexBits(3);
    }


    @Override
    protected boolean shouldExec() {
        EntityLivingBase target = entity.getAttackTarget();
        double chance = 1 - timeExecuting / (double) getTotalDuration();
        //Chance to end while using it
        return target != null && Math.random() / 3 < chance;
    }

    @Override
    protected void startExec() {
        cleanUp();
        execAbility();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public StatusControl[] getStatusControls() {
        StatusControl[] controls = new StatusControl[2];
        controls[0] = START_FLAMETHROW;
        controls[1] = STOP_FLAMETHROW;
        return controls;
    }

    @Override
    public boolean shouldExecStatCtrl(StatusControl statusControl) {
        if (statusControl == START_FLAMETHROW)
            return timeExecuting >= getWaitDuration();
        if (statusControl == STOP_FLAMETHROW)
            return timeExecuting >= getTotalDuration();
        return super.shouldExecStatCtrl(statusControl);
    }

    @Override
    public boolean shouldExecAbility() {
        return timeExecuting <= 1;
    }

    @Override
    public int getWaitDuration() {
        return 5;
    }

    @Override
    public int getTotalDuration() {
        return 50;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        bender.getData().removeTickHandler(FLAMETHROWER, new BendingContext(bender.getData(), entity, new Raytrace.Result()));
    }
}
