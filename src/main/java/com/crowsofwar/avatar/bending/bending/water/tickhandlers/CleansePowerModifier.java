package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.bending.bending.water.AbilityCleanse;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class CleansePowerModifier extends BuffPowerModifier {

    @Override
    public double get(BendingContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData("cleanse");
        AbilityCleanse cleanse = (AbilityCleanse) Abilities.get("cleanse");

        double modifier = 50;
        if (cleanse != null) {
            modifier = cleanse.getProperty(Ability.POWERRATING, abilityData).floatValue();
        }
        return modifier;

    }

    @Override
    protected Vision[] getVisions() {
        return new Vision[]{Vision.CLEANSE_WEAK, Vision.CLEANSE_MEDIUM, Vision.CLEANSE_POWERFUL};
    }

    @Override
    public boolean onUpdate(BendingContext ctx) {
        //Particle time
        //Swirl with cubes and flash
        AbilityCleanse cleanse = (AbilityCleanse) Abilities.get("cleanse");
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        if (cleanse != null) {
            float radius = cleanse.getProperty(Ability.RADIUS, AbilityData.get(ctx.getBenderEntity(),
                    getAbilityName())).floatValue();
            int rings = (int) (radius * 3);
            int particles = 2;
            if (world.isRemote) {
            //    if (entity.ticksExisted % 3 == 0)
//                ParticleBuilder.create(ParticleBuilder.Type.CUBE).spawnEntity(entity)
//                .time(28 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0, 102, 255, 95).scale(radius / 2F * world.rand.nextFloat())
//                        .swirl(rings, particles, radius * 0.5F, particles / 5F, rings * 2,
//                                (1 / (radius / 10F)), entity, world, false, AvatarEntityUtils.getMiddleOfEntity(entity),
//                                ParticleBuilder.SwirlMotionType.OUT, false, true);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).spawnEntity(entity)
                            .time(34 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(AvatarUtils.getRandomNumberInRange(0, 50),
                            180 + AvatarUtils.getRandomNumberInRange(0, 70), 235 + AvatarUtils.getRandomNumberInRange(0, 20),
                            (int) (5)).scale(radius * world.rand.nextFloat() / 1.5F)
                            //The .max and .min functions ensure it doesn't infinite loop if the radius is too small
                            .glow(AvatarUtils.getRandomNumberInRange(1, 100) > 15 - radius).swirl(rings, particles, (float) Math.sqrt(radius),
                            particles / 14F * (radius), rings / 2F, (radius / 3F), entity, world, true, AvatarEntityUtils.getMiddleOfEntity(entity),
                            ParticleBuilder.SwirlMotionType.OUT, false, true);
            }
        }
        return super.onUpdate(ctx);
    }

    @Override
    protected String getAbilityName() {
        return "cleanse";
    }

}

