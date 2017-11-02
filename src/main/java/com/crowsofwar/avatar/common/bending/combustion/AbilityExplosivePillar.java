package com.crowsofwar.avatar.common.bending.combustion;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.common.entity.EntityExplosionSpawner;
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
            if (abilityData.getLevel() == 1 || abilityData.getLevel() == 2) {
                spawner.setVelocity(look.times(mult * 5));
                spawner.setExplosionStrength(1.25F);
                spawner.setExplosionFrequency(8F);
                spawner.spawnFlames(false);

            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)){
               spawner.setVelocity(look.times(mult*2));
               spawner.setExplosionStrength(1F);
               spawner.setExplosionFrequency(1F);
               spawner.spawnFlames(true);
            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                spawner.setVelocity(look.times(mult * 10));
                spawner.setExplosionStrength(3F);
                spawner.setExplosionFrequency(12F);
                spawner.spawnFlames(false);

            }
            spawner.setOwner(entity);
            spawner.spawnFlames(false);
            spawner.setExplosionFrequency(10F);
            spawner.setExplosionStrength(1F);
            spawner.setPosition(entity.posX, entity.posY, entity.posZ);
            spawner.setVelocity(look.times(mult));
            spawner.maxTicks(ticks);
            spawner.isUnstoppable(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
            world.spawnEntity(spawner);
        }
    }
}
