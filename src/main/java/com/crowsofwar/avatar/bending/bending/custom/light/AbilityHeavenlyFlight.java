package com.crowsofwar.avatar.bending.bending.custom.light;

import com.crowsofwar.avatar.bending.bending.Ability;

public class AbilityHeavenlyFlight extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityHeavenlyFlight() {
        super(Lightbending.ID, "heavenly_flight");
    }
}
