package com.crowsofwar.avatar.bending.bending.custom.ki;

import com.crowsofwar.avatar.bending.bending.Ability;

import java.util.UUID;

public class AbilityKaioKen extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityKaioKen() {
        super(Kibending.ID, "kaio_ken");
    }
}