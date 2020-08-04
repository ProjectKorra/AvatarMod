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
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_LEFT_CLICK_DOWN;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowFireball extends StatusControl {

    public StatCtrlThrowFireball() {
        super(10, CONTROL_LEFT_CLICK_DOWN, LEFT_OF_CROSSHAIR);
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
            double speedMult = ability.getProperty(Ability.SPEED, abilityData).floatValue() * 3.5;
            float chi = ability.getChiCost(abilityData);
            cooldown = ability.getProperty(Ability.COOLDOWN, abilityData).intValue();
            burnOut = ability.getProperty(Ability.BURNOUT, abilityData).floatValue();
            exhaustion = ability.getProperty(Ability.EXHAUSTION, abilityData).floatValue();
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

            cooldown -= cooldown * abilityData.getDamageMult() * abilityData.getXpModifier();
            burnOut -= burnOut * abilityData.getDamageMult() * abilityData.getXpModifier();
            exhaustion -= exhaustion * abilityData.getDamageMult() * abilityData.getXpModifier();
            maxBurnout -= maxBurnout * abilityData.getDamageMult() * abilityData.getXpModifier();
            maxExhaustion -= maxExhaustion * abilityData.getDamageMult() * abilityData.getXpModifier();


            cooldown *= (1 + abilityData.getBurnOut() / 200);

            exhaustion *= mult;
            exhaustion = Math.min(exhaustion, maxExhaustion);
            exhaustion *= (1 + abilityData.getBurnOut() / 100);

            burnOut *= mult;
            burnOut = Math.min(burnOut, maxBurnout);
            burnOut *= (1 + abilityData.getBurnOut() / 200);

            damage *= mult;
            damage = Math.min(damage, maxDamage);


            Vector lookPos = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(15 + fireball.getAvgSize()));

            //Sets it to 0 if the entity is in creative mode.
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                chi = burnOut = exhaustion = cooldown = 0;
            }

            if (Objects.requireNonNull(Bender.get(entity)).consumeChi(chi)) {

                fireball.setBehaviour(new FireballBehavior.Thrown());
                fireball.rotationPitch = entity.rotationPitch;
                fireball.rotationYaw = entity.rotationYaw;
                fireball.setDamage(damage);
                abilityData.setAbilityCooldown(cooldown);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                abilityData.setBurnOut(burnOut);

                Vector vel = lookPos.minus(Vector.getEntityPos(fireball));

                //Drillgon200: Why deal with orbit ids when there's already two other ids you can organize them by?
                //FD: No clue
                if (!world.isRemote) {
                    if (!fireballs.isEmpty()) {
                        fireballs = fireballs.stream().filter(fireball1 -> !(fireball1.getBehaviour() instanceof FireballBehavior.Thrown
                                || fireball1.getBehaviour() instanceof AbilityFireball.FireballOrbitController)).collect(Collectors.toList());
                        if (!fireballs.isEmpty()) {
                            fireballs.get(0).setBehaviour(new AbilityFireball.FireballOrbitController());
                            for (EntityFireball ball : fireballs)
                                ball.setOrbitID(ball.getOrbitID() - 1);
                        }
                        if (fireballs.size() > 1)
                            fireball.setVelocity(vel.normalize().times(speedMult));
                        else fireball.setVelocity(Vector.getLookRectangular(entity).times(speedMult));
                    } else fireball.setVelocity(Vector.getLookRectangular(entity).times(speedMult));
                }
            }
            world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 4F, 0.8F);
        }

        return true;
    }

}
