package com.crowsofwar.avatar.bending.bending.custom.ki.powermods;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityImmolate;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import java.util.Objects;

public class KaioKenPowerModifier extends PowerRatingModifier {

    @Override
    public double get(BendingContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(new AbilityImmolate().getName());

        //Powerrating should be an integer but I'll leave it as a double toa count for user error
        return Objects.requireNonNull(Abilities.get("kaio_ken")).getProperty(Ability.POWERRATING, abilityData).doubleValue();

    }

    @Override
    public boolean onUpdate(BendingContext ctx) {
        return false;
    }

}

