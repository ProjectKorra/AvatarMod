package com.crowsofwar.avatar.common.bending.combustion;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.common.entity.mob.EntityExplosionSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityExplosivePillar extends Ability {
    public AbilityExplosivePillar() {super (Combustionbending.ID, "explosive_pillar"); }

    @Override
    public void execute(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getData().getAbilityData(this);
        float xp = abilityData.getTotalXp();
        float ticks = 100;
        float chi =STATS_CONFIG.chiLightning+1;
        if (bender.consumeChi(chi)){
            EntityExplosionSpawner spawner = new EntityExplosionSpawner(world);
            Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
            double mult = ctx.getLevel() >= 1 ? 14 : 8;

            if (abilityData.getLevel() == 1) {
                spawner.setVelocity(look.times(mult * 5));

            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                spawner.setVelocity(look.times(mult * 10));

            }
            spawner.setOwner(entity);
            spawner.setPosition(entity.posX, entity.posY, entity.posZ);
            spawner.setVelocity(look.times(mult));
            spawner.maxTicks(ticks);
            spawner.isUnstoppable(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
            world.spawnEntity(spawner);
        }
    }
}
