package com.crowsofwar.avatar.bending.bending.sand;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.config.ConfigStats;
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
            float speed = powerModify(getProperty(SPEED, ctx).floatValue(), abilityData) / 5F;
            float size = powerModify(getProperty(SIZE, ctx).floatValue(), abilityData);


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
            sandstorm.setEntitySize(size * 2.2F, size);
            sandstorm.setTier(getBaseTier());
            sandstorm.setElement(new Sandbending());

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
