package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.entity.EntityCloudBall;
import com.crowsofwar.avatar.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_CLOUDBURST;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

public class AbilityCloudBurst extends Ability {

    private static final String
            CHI_SMASH = "chiSmash",
            ABSORB = "absorb";

    public AbilityCloudBurst() {
        super(Airbending.ID, "cloudburst");
        requireRaytrace(2.5, false);
    }

    @Override
    public void init() {
        super.init();
        addBooleanProperties(PUSH_IRON_TRAPDOOR, PUSH_IRONDOOR, PUSH_STONE, PUSH_REDSTONE, CHI_SMASH, ABSORB);

    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        Bender bender = ctx.getBender();
        BendingData data = ctx.getData();
        AbilityData abilityData = ctx.getAbilityData();

        if (data.hasStatusControl(THROW_CLOUDBURST)) return;


        if (bender.consumeChi(getChiCost(ctx) / 4)) {

            Vector target;
            if (ctx.isLookingAtBlock()) {
                target = ctx.getLookPos();
            } else {
                Vector playerPos = getEyePos(entity);
                target = playerPos.plus(getLookRectangular(entity).times(2.5));
            }

            float damage = getProperty(DAMAGE, ctx).floatValue();
            int size = (int) (getProperty(SIZE, ctx).floatValue() * 32);
            float chiHit = getProperty(CHI_HIT, ctx).floatValue();
            int lifetime = getProperty(LIFETIME, ctx).intValue();
            float knockback = getProperty(KNOCKBACK, ctx).floatValue() / 2;

            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            chiHit *= abilityData.getDamageMult() * abilityData.getXpModifier();
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            knockback *= abilityData.getDamageMult() * abilityData.getXpModifier();

            EntityCloudBall cloudball = new EntityCloudBall(world);

            if (target != null)
                cloudball.setPosition(target);

            cloudball.setOwner(entity);
            cloudball.setSize(size);
            cloudball.setPushStoneButton(getBooleanProperty(PUSH_STONE, ctx));
            cloudball.setPushIronTrapDoor(getBooleanProperty(PUSH_IRON_TRAPDOOR, ctx));
            cloudball.setPushIronDoor(getBooleanProperty(PUSH_IRONDOOR, ctx));
            cloudball.setBehavior(new CloudburstBehavior.PlayerControlled());
            cloudball.setDamage(damage);
            cloudball.setXp(getProperty(XP_HIT, ctx).floatValue());
            cloudball.setAbsorb(getBooleanProperty(ABSORB, ctx));
            cloudball.setChiSmash(getBooleanProperty(CHI_SMASH, ctx));
            cloudball.setAbility(this);
            cloudball.setElement(new Airbending());
            cloudball.setChiHit(chiHit);
            cloudball.setDamageSource("avatar_Air");
            cloudball.setTier(getCurrentTier(ctx));
            cloudball.setLifeTime(lifetime);
            cloudball.setPush(knockback);
            if (!world.isRemote)
                world.spawnEntity(cloudball);

            data.addStatusControl(THROW_CLOUDBURST);
        }
        super.execute(ctx);

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
        return 3;
    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiCloudBall(this, entity, bender);
    }

    @Override
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getBurnOut(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getExhaustion(AbilityContext ctx) {
        return 0;
    }
}
