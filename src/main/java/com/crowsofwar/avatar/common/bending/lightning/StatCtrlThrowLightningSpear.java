package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

public class StatCtrlThrowLightningSpear extends StatusControl {
    public StatCtrlThrowLightningSpear() {
        super(15, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();

        double size = 6;

        EntityLightningSpear spear = AvatarEntity.lookupControlledEntity(world, EntityLightningSpear.class, entity);

        if (spear != null) {
            AbilityData abilityData = ctx.getData().getAbilityData("lightning_spear");
            double speedMult = abilityData.getLevel() >= 1 ? 25 : 15;
            spear.addVelocity(Vector.getLookRectangular(entity).times(speedMult));
            spear.setBehavior(new LightningSpearBehavior.Thrown());
        }

        return true;
    }

}

