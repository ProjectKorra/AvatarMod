package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.bending.Ability;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author Aang23
 */
@Cancelable
public class BendingEvent extends EntityEvent {
    private Ability ability;

    public BendingEvent(Entity entity, Ability ability) {
        super(entity);
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }
}
