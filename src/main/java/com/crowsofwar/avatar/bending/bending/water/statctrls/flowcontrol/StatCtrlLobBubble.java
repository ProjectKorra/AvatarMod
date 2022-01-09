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

package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.water.AbilityFlowControl;
import com.crowsofwar.avatar.bending.bending.water.AbilityWaterArc;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.util.math.MathHelper;

import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

/**
 * @author CrowsOfWar
 */
public class StatCtrlLobBubble extends StatusControl {

    /**
     *
     */
    public StatCtrlLobBubble() {
        super(7, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        //If bubble
        AbilityFlowControl control = (AbilityFlowControl) Abilities.get("flow_control");
        AbilityData controlData = AbilityData.get(ctx.getBenderEntity(), "flow_control");

        //If arc
        AbilityWaterArc arc = (AbilityWaterArc) Abilities.get("water_arc");
        AbilityData arcData = AbilityData.get(ctx.getBenderEntity(), "water_arc");

        EntityWaterBubble bubble = AvatarEntity.lookupEntity(ctx.getWorld(), EntityWaterBubble.class, //
                bub -> bub.getBehaviour() instanceof WaterBubbleBehavior.PlayerControlled
                        && bub.getOwner() == ctx.getBenderEntity());

        if (bubble != null && control != null && controlData != null && arc != null && arcData != null) {

            float speed = 1;
            float speedMod = bubble.getDegreesPerSecond() / 2;
            if (bubble.getDefaultState() == EntityWaterBubble.State.BUBBLE) {
                speed = control.getProperty(Ability.SPEED, controlData).floatValue();
                speed = control.powerModify(speed, controlData);
                speed *= MathHelper.clamp(speedMod, 1, 6);
            }
            if (bubble.getDefaultState() == EntityWaterBubble.State.ARC) {
                speed = arc.getProperty(Ability.SPEED, arcData).floatValue() * 4;
                speed = arc.powerModify(speed, arcData);
                //Default speed is higher so charging affects it less
                speedMod = bubble.getDegreesPerSecond() / 6;
                speed *= MathHelper.clamp(speedMod, 1, 6);
            }
            bubble.setBehaviour(new WaterBubbleBehavior.Lobbed());
            bubble.setDamage(bubble.getDamage() * MathHelper.clamp(speed / 5, 1, 2));
            bubble.setVelocity(Vector.getLookRectangular(ctx.getBenderEntity()).times(speed));


        }

        return true;
    }

}
