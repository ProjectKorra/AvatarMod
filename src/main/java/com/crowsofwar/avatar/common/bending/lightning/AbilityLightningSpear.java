package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.air.AiAirblade;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirblade;
import com.crowsofwar.avatar.common.entity.EntityLightningSpear;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;
import static java.lang.Math.abs;

public class AbilityLightningSpear extends Ability {
    public AbilityLightningSpear() {
        super(Lightningbending.ID, "lightning_spear");
    }
    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();

        if (!bender.consumeChi(STATS_CONFIG.chiAirblade)) return;

        double pitchDeg = entity.rotationPitch;
        if (abs(pitchDeg) > 30) {
            pitchDeg = pitchDeg / abs(pitchDeg) * 30;
        }
        float pitch = (float) Math.toRadians(pitchDeg);

        Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), pitch);
        Vector spawnAt = Vector.getEntityPos(entity).plus(look.times(2)).plus(0, 1, 0);

        AbilityData abilityData = ctx.getData().getAbilityData(this);
        float xp = abilityData.getTotalXp();

        EntityLightningSpear spear = new EntityLightningSpear(world);
        spear.setPosition(spawnAt.x(), spawnAt.y(), spawnAt.z());
        spear.setVelocity(look.times(ctx.getLevel() >= 1 ? 30 : 20));
        spear.setDamage(STATS_CONFIG.airbladeSettings.damage * (1 + xp * .015f));
        spear.setOwner(entity);
        spear.setPierceArmor(abilityData.isMasterPath(SECOND));
        spear.setChainAttack(abilityData.isMasterPath(FIRST));

        float chopBlocks = -1;
        if (abilityData.getLevel() >= 1) {
            chopBlocks = 0;
        }
        if (abilityData.isMasterPath(SECOND)) {
            chopBlocks = 2;
        }
        spear.setChopBlocksThreshold(chopBlocks);

        world.spawnEntity(spear);

    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiLightningSpear(this, entity, bender);
    }

}


