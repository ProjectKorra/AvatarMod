package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;
import static java.lang.Math.abs;

public class AbilityLightningSpear extends Ability {
    public AbilityLightningSpear() {
        super(Lightningbending.ID, "lightning_spear");
        requireRaytrace(2.5, false);
    }
    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();

        if (data.hasStatusControl(StatusControl.THROW_LIGHTNINSPEAR)) return;

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

            EntityLightningSpear spear = new EntityLightningSpear(world);
            spear.setPosition(target);
            spear.setOwner(entity);
            spear.setBehavior(new LightningSpearBehavior.PlayerControlled());
            spear.setDamage(damage);
            if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) spear.setSize(20);
            world.spawnEntity(spear);


            data.addStatusControl(StatusControl.THROW_LIGHTNINSPEAR);

        }

    }


    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiLightningSpear(this, entity, bender);
    }

}


