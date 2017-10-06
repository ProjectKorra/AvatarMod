package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;


import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

public class StatCtrlThrowCloudBall extends StatusControl {
    public StatCtrlThrowCloudBall() {
        super(10, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();

        double size = 6;

        EntityCloudBall cloudBall = AvatarEntity.lookupControlledEntity(world, EntityCloudBall.class, entity);

        if (cloudBall != null) {
            AbilityData abilityData = ctx.getData().getAbilityData("cloudburst");
            double speedMult = abilityData.getLevel() >= 1 ? 25 : 15;
            cloudBall.addVelocity(Vector.getLookRectangular(entity).times(speedMult));
            cloudBall.setBehavior(new CloudburstBehavior.Thrown());
        }

        return true;
    }

}
//REGISTER THIS TO SEE IF IT FIXES ITSELF

