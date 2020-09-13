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

package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthControl;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.util.data.StatusControlController.PLACE_BLOCK;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowBlock extends StatusControl {

    public StatCtrlThrowBlock() {
        super(2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        World world = entity.world;
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = AbilityData.get(entity, "earth_control");
        AbilityEarthControl control = (AbilityEarthControl) Abilities.get("earth_control");

        EntityFloatingBlock floating = AvatarEntity.lookupControlledEntity(world, EntityFloatingBlock.class,
                entity);
        List<EntityFloatingBlock> blocks = world.getEntitiesWithinAABB(EntityFloatingBlock.class,
                entity.getEntityBoundingBox().grow(3.5, 3, 3.5));


        if (floating != null && abilityData != null && control != null) {
            if (abilityData.getAbilityCooldown(entity) > 0) {
                float yaw = (float) Math.toRadians(entity.rotationYaw);
                float pitch = (float) Math.toRadians(entity.rotationPitch);

                // Calculate force and everything
                float forceMult = control.getProperty(Ability.SPEED, abilityData).floatValue() * 4;
                float chiCost = control.getChiCost(abilityData);
                float exhaustion = control.getExhaustion(abilityData);
                float burnout = control.getBurnOut(abilityData);
                int cooldown = control.getCooldown(abilityData);

                chiCost *= abilityData.getDamageMult() * abilityData.getXpModifier();
                exhaustion *= abilityData.getDamageMult() * abilityData.getXpModifier();
                burnout *= abilityData.getDamageMult() * abilityData.getXpModifier();
                cooldown *= abilityData.getDamageMult() * abilityData.getXpModifier();
                forceMult *= abilityData.getDamageMult() * abilityData.getXpModifier();

                if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                    chiCost = exhaustion = burnout = cooldown = 0;

                Vector direction;
                Vec3d look = entity.getLookVec();
                Vec3d pos = entity.getPositionEyes(1.0F);

                //Drillgon200: Raytrace from the bender's line of sight and if it hit anything, use the vector from the
                //block to the hit point for the motion vector rather than the look vector. This improves accuracy when the
                //block isn't directly in front of the bender.
                RayTraceResult r = Raytrace.rayTrace(world, pos, look.scale(75).add(pos), 0, false, true, false,
                        Entity.class, e -> e instanceof EntityFloatingBlock || e == entity);

                if (r != null && r.hitVec != null) {
                    Vec3d dir = r.hitVec.subtract(floating.getPositionVector()).normalize();
                    direction = new Vector(dir.x, dir.y, dir.z);
                } else {
                    direction = Vector.toRectangular(yaw, pitch);
                }

                if (bender.consumeChi(chiCost)) {
                    abilityData.setAbilityCooldown(cooldown);
                    abilityData.addBurnout(burnout);
                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).addExhaustion(exhaustion);

                    floating.setVelocity(direction.times(forceMult));
                    floating.setBehavior(new FloatingBlockBehavior.Thrown());
                }
                data.removeStatusControl(PLACE_BLOCK);

                blocks = blocks.stream().filter(block -> block.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled).collect(Collectors.toList());
                if (blocks.isEmpty())
                    abilityData.setRegenBurnout(true);

                return true;
            }
        }

        return false;

    }

}
