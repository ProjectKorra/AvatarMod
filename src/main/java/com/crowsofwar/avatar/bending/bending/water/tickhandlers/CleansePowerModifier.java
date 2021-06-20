package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.bending.bending.water.AbilityCleanse;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
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
        //if (CLIENT_CONFIG.shaderSettings.useCleanseShaders) {
        return new Vision[]{Vision.CLEANSE_WEAK, Vision.CLEANSE_MEDIUM, Vision.CLEANSE_POWERFUL};
        //}
        //else return null;
    }

    @Override
    public boolean onUpdate(BendingContext ctx) {
        //Particle time
        //Swirl with cubes and flash
        AbilityCleanse cleanse = (AbilityCleanse) Abilities.get("cleanse");
        World world = ctx.getWorld();
        if (cleanse != null) {
            float radius = cleanse.getProperty(Ability.RADIUS, AbilityData.get(ctx.getBenderEntity(),
                    getAbilityName())).floatValue();
            if (world.isRemote) {
                ParticleBuilder.create(ParticleBuilder.Type.CUBE);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH);
            }
        }
        return super.onUpdate(ctx);
    }

    @Override
    protected String getAbilityName() {
        return "cleanse";
    }

}

