package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.AiCloudBall;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

public class AbilityCloudBurst extends Ability {


    public AbilityCloudBurst() {
        super(Airbending.ID, "cloudburst");
        requireRaytrace(2.5, false);
    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        Bender bender = ctx.getBender();
        BendingData data = ctx.getData();

        if (data.hasStatusControl(StatusControl.THROW_CLOUDBURST)) return;

        if (bender.consumeChi(STATS_CONFIG.chiCloudburst)) {

            Vector target;
            if (ctx.isLookingAtBlock()) {
                target = ctx.getLookPos();
            } else {
                Vector playerPos = getEyePos(entity);
                target = playerPos.plus(getLookRectangular(entity).times(2.5));
            }

            float damage = STATS_CONFIG.fireballSettings.damage;
            damage *= ctx.getLevel() >= 2 ? 2.5f : 1f;

            EntityCloudBall cloudball = new EntityCloudBall(world);
            cloudball.setPosition(target);
            cloudball.setOwner(entity);
            cloudball.setBehavior(new CloudburstBehavior.PlayerControlled());
            cloudball.setDamage(damage);
            if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) cloudball.setSize(20);
            world.spawnEntity(cloudball);


            data.addStatusControl(StatusControl.THROW_CLOUDBURST);

        }

    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiCloudBall(this, entity, bender);
    }

}
