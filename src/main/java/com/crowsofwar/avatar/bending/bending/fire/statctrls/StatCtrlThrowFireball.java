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
package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFireball;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.client.controls.AvatarControl.*;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowFireball extends StatusControl {

    public StatCtrlThrowFireball() {
        super(10, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityFireball ability = (AbilityFireball) Abilities.get("fireball");
        AbilityData abilityData = ctx.getData().getAbilityData(new AbilityFireball());

        int cooldown;
        float burnOut, exhaustion, size, damage, maxDamage, maxBurnout, maxExhaustion;

        EntityFireball fireball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, entity);
        List<EntityFireball> fireballs = world.getEntitiesWithinAABB(EntityFireball.class,
                entity.getEntityBoundingBox().grow(3.5, 3, 3.5));

        if (fireball != null) {

            assert ability != null;

            float yaw = (float) Math.toRadians(entity.rotationYaw);
            float pitch = (float) Math.toRadians(entity.rotationPitch);

            double speedMult = ability.getProperty(Ability.SPEED, abilityData).floatValue() * 3.5;
            float chi = ability.getChiCost(abilityData);

            cooldown = ability.getCooldown(abilityData);
            burnOut = ability.getBurnOut(abilityData);
            exhaustion = ability.getExhaustion(abilityData);
            size = ability.getProperty(Ability.SIZE, abilityData).floatValue();
            damage = ability.getProperty(Ability.DAMAGE, abilityData).floatValue();
            maxDamage = ability.getProperty(Ability.MAX_DAMAGE, abilityData).floatValue();
            maxBurnout = ability.getProperty(Ability.MAX_BURNOUT, abilityData).floatValue();
            maxExhaustion = ability.getProperty(Ability.MAX_EXHAUSTION, abilityData).floatValue();

            speedMult *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            maxDamage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();

            float mult = fireball.getAvgSize() / size;

            maxBurnout *= (2 - abilityData.getDamageMult()) * abilityData.getXpModifier();
            maxExhaustion *= (2 - abilityData.getDamageMult()) * abilityData.getXpModifier();

            exhaustion *= mult;
            exhaustion = Math.min(exhaustion, maxExhaustion);

            burnOut *= mult;
            burnOut = Math.min(burnOut, maxBurnout);

            damage *= mult;
            damage = Math.min(damage, maxDamage);


            Vector direction;
            Vec3d look = entity.getLookVec();
            Vec3d pos = entity.getPositionEyes(1.0F);

            //Drillgon200: Raytrace from the bender's line of sight and if it hit anything, use the vector from the
            //block to the hit point for the motion vector rather than the look vector. This improves accuracy when the
            //block isn't directly in front of the bender.
            RayTraceResult r = Raytrace.rayTrace(world, pos, look.scale(75).add(pos), 0, false, true, false,
                    Entity.class, e -> e instanceof EntityFireball || e == entity);

            if (r != null && r.hitVec != null) {
                Vec3d dir = r.hitVec.subtract(fireball.getPositionVector()).normalize();
                direction = new Vector(dir.x, dir.y, dir.z);
            } else {
                direction = Vector.toRectangular(yaw, pitch);
            }

            //Sets it to 0 if the entity is in creative mode.
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                chi = burnOut = exhaustion = cooldown = 0;
            }

            if (Objects.requireNonNull(Bender.get(entity)).consumeChi(chi)) {

                fireball.setBehaviour(new FireballBehavior.Thrown());
                fireball.rotationPitch = entity.rotationPitch;
                fireball.rotationYaw = entity.rotationYaw;
                fireball.setDamage(damage);
                fireball.setVelocity(direction.times(speedMult));
                abilityData.setAbilityCooldown(cooldown);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                abilityData.addBurnout(burnOut);



                fireballs = fireballs.stream().filter(fireball1 -> !(fireball1.getBehaviour() instanceof FireballBehavior.Thrown)).collect(Collectors.toList());
                if (fireballs.isEmpty())
                    abilityData.setRegenBurnout(true);


            }
            world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 4F, 0.8F);
        }

        return true;
    }

}
