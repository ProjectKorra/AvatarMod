package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.util.AvatarEntityUtils;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EntityTornado extends EntityOffensive {

    private static final DataParameter<Boolean> SYNC_EXPLODE = EntityDataManager.createKey(EntityTornado.class,
            DataSerializers.BOOLEAN);

    public EntityTornado(World world) {
        super(world);
    }

    @Override
    public boolean shouldExplode() {
        return false;
    }

    @Override
    public boolean shouldDissipate() {
        return true;
    }

    @Override
    public boolean isPiercing() {
        return true;
    }

    @Override
    public boolean multiHit() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //Render code
        //ArrayLists cause I'm lazy
        ArrayList<Vec3d> tornadoPoints = new ArrayList<>();
        if (world.isRemote) {
            //Essentially, creates a rough outline of a vortex, which is then bezier-curved.
            //It optimises everything and looks cooler. I think.

            //Creates the points to put into the bezier curve.
            for (int angle = 0; angle < 120; angle++) {
                double radAngle = Math.toRadians(angle);
                double angle2 = world.rand.nextDouble() * Math.PI * 2;
                double radius = 0.01 + (angle / (120 / (getWidth() + getExpandedHitboxWidth())));
                double x = radius * cos(radAngle);
                double y = angle / (120 / (getExpandedHitboxHeight() + getHeight()));
                double z = radius * sin(radAngle);
                double speed = world.rand.nextDouble() * 2 + 1;
                double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
                angle2 += omega;
                Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(this);
                tornadoPoints.add(new Vec3d(x + centre.x, y + centre.y, z + centre.z));
            }
            //Draws the bezier curve
            for (int i = 0; i < tornadoPoints.size(); i++) {
                Vec3d pos = tornadoPoints.get(i);
                Vec3d velPos = tornadoPoints.get(i + 1);

                //Iterate for the amount of particles in between each line.
                for (int h = 0; h < 360; h += (360 / 120)) {
                }
                //Only curves through 3 points to optimise the shape of the curve.


            }

        }
    }
}
