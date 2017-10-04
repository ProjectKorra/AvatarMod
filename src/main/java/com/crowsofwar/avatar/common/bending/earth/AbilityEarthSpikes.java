package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthSpike;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityEarthSpikes extends Ability {

    public AbilityEarthSpikes() {
         super(Earthbending.ID,"earthspike");
    }
    public boolean unstoppable = false;

    @Override
    public void execute(AbilityContext ctx) {

        Bender bender = ctx.getBender();

        float chi = STATS_CONFIG.chiRavine;
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
            chi *= 1.5f;
        }

        if (bender.consumeChi(chi)) {

            AbilityData abilityData = ctx.getData().getAbilityData(this);
            float xp = abilityData.getTotalXp();

            EntityLivingBase entity = ctx.getBenderEntity();
            World world = ctx.getWorld();

            Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);

            double mult = ctx.getLevel() >= 1 ? 14 : 8;
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)){
                unstoppable = true;
            }

            EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
            earthspike.setOwner(entity);
            earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
            earthspike.setVelocity(look.times(mult));
            earthspike.setDamageMult(.90f + xp / 100);
            earthspike.setDistance(ctx.getLevel() >= 2 ? 16 : 10);
            earthspike.setBreakBlocks(ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST));
            earthspike.setDropEquipment(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
            world.spawnEntity(earthspike);

        }

    }

}
