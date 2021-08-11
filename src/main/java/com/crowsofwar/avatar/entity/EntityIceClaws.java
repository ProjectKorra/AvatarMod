package com.crowsofwar.avatar.entity;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityIceClaws extends EntityOffensive {

    //Probably want a destination variable
    private Vec3d destination;
    /**
     * @param world
     */
    public EntityIceClaws(World world) {
        super(world);
    }
}
