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
package com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.custom.dark.AbilityOblivionBeam;
import com.crowsofwar.avatar.bending.bending.custom.light.AbilityDivineBeginning;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.CHARGE_TIME;
import static com.crowsofwar.avatar.bending.bending.Ability.CHI_COST;

/**
 * @author CrowsOfWar
 */
public class ChargeDivineBeginning extends TickHandler {

    public static final UUID DIVINE_BEGINNING_MOD_ID = UUID.randomUUID();

    public ChargeDivineBeginning(int id) {
        super(id);
    }


    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("divine_beginning");
        AbilityDivineBeginning divineBeginning = (AbilityDivineBeginning) Abilities.get("divine_beginning");
        int chargeDuration = data.getTickHandlerDuration(this);
        if (divineBeginning == null)
            return false;

        float requiredChi = divineBeginning.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;


        //Charging visuals (6 orbs around the player's head)
        float radius = 1.5F;
        float size = radius / 4;

        if (world.isRemote && entity != null) {
            Vector look = Vector.getLookRectangular(entity);
            for (int i = 0; i < 360; i += 60) {
                //We only want 6 orbs
                //Create the points
                double angle = Math.toRadians(i);
                double x = radius * Math.cos(angle);
                double y = 0;
                double z = radius * Math.sin(angle);
                Vector pos = new Vector(x, y, z);
                pos = Vector.rotateAroundAxisX(pos, entity.rotationPitch + 90);
                pos = Vector.rotateAroundAxisY(pos, entity.rotationYaw);
                pos = pos.plus(Vector.getEyePos(entity));


                //With pos as our new vector, we make spheres.
                if (entity.ticksExisted % 6 == 0 || data.getTickHandlerDuration(this) == 1) {
                    double x1, y1, z1;
                    for (double theta = 0; theta <= 180; theta += 1) {
                        double dphi = (76) / Math.sin(Math.toRadians(theta));
                        for (double phi = 0; phi < 360; phi += dphi) {
                            double rphi = Math.toRadians(phi);
                            double rtheta = Math.toRadians(theta);

                            //Making it spin increases the sphere size
                            x1 = size * 1.5F * Math.cos(rphi) * Math.sin(rtheta);
                            y1 = size * 1.5F * Math.sin(rphi) * Math.sin(rtheta);
                            z1 = size * 1.5F * Math.cos(rtheta);

                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(look.x() * 0.0125 + world.rand.nextGaussian() / 80,
                                            look.z() * 0.0125 + world.rand.nextGaussian() / 80, look.z() * 0.0125 + world.rand.nextGaussian() / 80).
                                    scale(size * 0.85F)
                                    .time(12).pos(pos.toMinecraft().add(look.times(0.75).toMinecraft().add(x1, y1 - 0.025, z1))).spin(0.1, world.rand.nextGaussian() / 20)
                                    .clr(1F, 1F, 0.3F, 0.85F).glow(true).spawnEntity(entity).spawn(world);
                        }
                    }
                }

                Vector targetPos = look.times(3).plus(Vector.getEyePos(entity));
                //Beam trail of particles
                Vector vel = targetPos.minus(pos);
                for (int h = 0; h < 6; h++) {
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(vel.x() * 0.125,
                                    vel.z() * 0.125, vel.z() * 0.125).
                            scale(size * 1.5F)
                            .time(12).pos(pos.toMinecraft())
                            .clr(1F, 1F, 0.3F, 0.85F).glow(true).spawnEntity(entity).spawn(world);

                }
            }
        }


        float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(DIVINE_BEGINNING_MOD_ID) == null)
            applyMovementModifier(entity, movementModifier);

        entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                0.6F, 0.8F + world.rand.nextFloat() / 10);
        return chargeDuration >= divineBeginning.getProperty(CHARGE_TIME, abilityData).intValue();
    }


    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("divine_beginning");
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(DIVINE_BEGINNING_MOD_ID) != null)
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(DIVINE_BEGINNING_MOD_ID);
        abilityData.setRegenBurnout(true);
        ctx.getData().addTickHandler(TickHandlerController.DIVINE_BEGINNING_HANDLER, ctx);

    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(DIVINE_BEGINNING_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(DIVINE_BEGINNING_MOD_ID, "Divine Beginning Movement Modifier", multiplier - 1, 1));

    }

}
