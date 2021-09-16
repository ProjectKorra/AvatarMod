package com.crowsofwar.avatar.bending.bending.custom.dark;

import com.crowsofwar.avatar.bending.bending.Ability;

import java.util.UUID;

//Essentially functions as a shadow ball from pokemon. Slight charge time,
//small projectile, expands then implodes.
public class AbilityShadeBurst extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityShadeBurst() {
        super(Darkbending.ID, "shade_burst");
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}
