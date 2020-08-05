package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.data.StatusControlController.SANDSTORM_REDIRECT;

public class AbilitySandstorm extends Ability {

    public AbilitySandstorm() {
        super(Sandbending.ID, "sandstorm");
    }

    @Override
    public void execute(AbilityContext ctx) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();

        if (bender.consumeChi(ConfigStats.STATS_CONFIG.chiSandstorm)) {

            // Determine stats based on experience

            AbilityData abilityData = ctx.getAbilityData();
            float speedMult = abilityData.getLevel() >= 1 ? 1 : 0.8f;
            boolean damageFlung = abilityData.getLevel() >= 2;
            boolean damageContacting = false;
            boolean vulnerableToAirbending = true;
            if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
                speedMult = 1.4f;
                vulnerableToAirbending = false;
            }
            if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
                damageContacting = true;
            }

            // Spawn the sandstorm

            Vector velocity = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0).times(8).times(speedMult);

            EntitySandstorm sandstorm = new EntitySandstorm(world);
            sandstorm.setPosition(Vector.getEntityPos(entity));
            sandstorm.setOwner(entity);
            sandstorm.setVelocity(velocity);
            sandstorm.setAbility(this);

            sandstorm.setVelocityMultiplier(speedMult);
            sandstorm.setDamageFlungTargets(damageFlung);
            sandstorm.setDamageContactingTargets(damageContacting);
            sandstorm.setVulnerableToAirbending(vulnerableToAirbending);

            if (!world.isRemote)
                world.spawnEntity(sandstorm);

            ctx.getData().addStatusControl(SANDSTORM_REDIRECT);

        }

    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public int getBaseParentTier() {
        return 4;
    }
}
