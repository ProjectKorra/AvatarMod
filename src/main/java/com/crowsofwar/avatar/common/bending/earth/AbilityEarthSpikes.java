package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityEarthspikeSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityEarthSpikes extends Ability {


    public AbilityEarthSpikes() {
        super(Earthbending.ID, "earthspike");
    }

    @Override
    public void execute(AbilityContext ctx) {

        AbilityData abilityData = ctx.getData().getAbilityData(this);
        float damage = 0.90F;
        float xp = abilityData.getTotalXp();
        float ticks = 100;
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        Bender bender = ctx.getBender();

        float chi = STATS_CONFIG.chiEarthspike;
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
            chi *= 2.5f;
            damage = 1f;
            ticks = 150;
        }
        if (ctx.getLevel() == 1 || ctx.getLevel() == 2) {
            chi *= 1.5f;
            damage = 1F;
            ticks = 150;
        }
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
            chi *= 2f;
            damage = 1.5F;
            ticks = 200;

        }

        if (bender.consumeChi(chi)) {
            EntityEarthspikeSpawner earthspike = new EntityEarthspikeSpawner(world);
            Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
            double mult = ctx.getLevel() >= 1 ? 14 : 8;
            double speed = 3;

            if (abilityData.getLevel() == 1) {
                earthspike.setVelocity(look.times(mult * 5));

            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                earthspike.setVelocity(look.times(mult * 10));

            }
            if (abilityData.getLevel() <= 2 || abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {


                earthspike.setOwner(entity);
                earthspike.setPosition(entity.posX, entity.posY, entity.posZ);
                earthspike.setVelocity(look.times(mult));
                earthspike.setDamageMult(damage + xp / 100);
                earthspike.maxTicks(ticks);
                earthspike.isUnstoppable(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
                world.spawnEntity(earthspike);
            }

            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {

                for (int i = 0; i < 8; i++) {

                    Vector direction1 = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
                            i * 45), 0);
                    Vector velocity = direction1.times(speed);
                    EntityEarthspikeSpawner spawner = new EntityEarthspikeSpawner(world);

                    spawner.setVelocity(velocity);
                    spawner.maxTicks(ticks);
                    spawner.setOwner(entity);
                    spawner.setPosition(entity.posX, entity.posY, entity.posZ);
                    spawner.setDamageMult(damage + xp / 100);
                    world.spawnEntity(spawner);
                }
            }

        }
    }
}
