package com.crowsofwar.avatar.bending.bending.sand;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.entity.EntitySandstorm;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.util.data.StatusControlController.SANDSTORM_REDIRECT;

public class AbilitySandstorm extends Ability {

    public static final String
            CONTACT_DAMAGE = "contactDamage",
            FLUNG_DAMAGE = "flungDamage",
            VULNERABLE = "vulnerableAirbending";

    public AbilitySandstorm() {
        super(Sandbending.ID, "sandstorm");
    }

    @Override
    public void init() {
        super.init();
        addBooleanProperties(FLUNG_DAMAGE, CONTACT_DAMAGE, VULNERABLE);
    }

    @Override
    public void execute(AbilityContext ctx) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();

        if (bender.consumeChi(getChiCost(ctx))) {

            // Determine stats based on experience

            AbilityData abilityData = ctx.getAbilityData();
            float speed = powerModify(getProperty(SPEED, ctx).floatValue(), abilityData) / 10F;
            float size = powerModify(getProperty(SIZE, ctx).floatValue(), abilityData);
            float push = powerModify(getProperty(KNOCKBACK, ctx).floatValue(), abilityData) * 6F;
            float damage = powerModify(getProperty(DAMAGE, ctx).floatValue(), abilityData);


            // Spawn the sandstorm

            Vector velocity = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0).times(8).times(speed);

            EntitySandstorm sandstorm = new EntitySandstorm(world);
            sandstorm.setPosition(Vector.getEntityPos(entity));
            sandstorm.setOwner(entity);
            sandstorm.setVelocity(velocity);
            sandstorm.setAbility(this);
            sandstorm.setVelocityMultiplier(speed);
            sandstorm.setDamageFlungTargets(getBooleanProperty(FLUNG_DAMAGE, ctx));
            sandstorm.setDamageContactingTargets(getBooleanProperty(CONTACT_DAMAGE, ctx));
            sandstorm.setVulnerableToAirbending(getBooleanProperty(VULNERABLE, ctx));
            sandstorm.setEntitySize(size * 2, size);
         //   sandstorm.setEntitySize(4F, 0.5F);
            sandstorm.setTier(getBaseTier());
            sandstorm.setPush(push);
            sandstorm.setElement(new Sandbending());
            sandstorm.setDamage(damage);
            sandstorm.setChiHit(powerModify(getProperty(CHI_HIT, ctx).floatValue(), abilityData));
            sandstorm.setPerformanceAmount(getProperty(PERFORMANCE, ctx).intValue());
            sandstorm.setLifeTime(/*140);**/(int) powerModify(getProperty(LIFETIME, ctx).intValue(), abilityData));
            sandstorm.setDamageSource("avatar_Sand_sandstorm");

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
