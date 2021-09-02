package com.crowsofwar.avatar.bending.bending.custom.light;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityBuff;
import com.crowsofwar.avatar.util.AvatarParticleUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class AbilityDivineJudgement extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityDivineJudgement() {
        super(Lightbending.ID, "divine_judgement");
    }

    @Override
    public void init() {
        super.init();
        addProperties(EFFECT_RADIUS, EFFECT_LEVEl, EFFECT_DURATION);
    }

    @Override
    public void execute(AbilityContext ctx) {
        super.execute(ctx);
        //Just summons a beam of light down from the heavens
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        Vec3d targetPos = Vector.getEntityPos(entity).plus(Vector.getLookRectangular(entity)
                .withY(0).times(3)).toMinecraft();

        float radius = getProperty(SIZE, ctx).floatValue();
        float damage = getProperty(DAMAGE, ctx).floatValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();
        float xp = getProperty(XP_HIT, ctx).floatValue();
        float push = getProperty(KNOCKBACK, ctx).floatValue();
        int lifetime = getProperty(EFFECT_DURATION, ctx).intValue();


        //Spawn particles
        if (world.isRemote) {
            AvatarUtils.spawnHelix(world, entity, new Vec3d(0, -1, 0),
                    (int) (200 * Math.PI * 4), 200, radius / 1.5F, ParticleBuilder.Type.FLASH, targetPos,
                    Vec3d.ZERO, true, 20, true, 1.0F, 1.0F, 0.3F, 0.5F, radius / 2);
            ParticleBuilder.create(ParticleBuilder.Type.BEAM).pos(targetPos.add(0, 200, 0)).target(targetPos).scale(radius * 6).clr(1.0F, 1.0F, 0.3F)
                    .fade(1F, 1F, 1F).time(10).spawn(world);

            ParticleBuilder.create(ParticleBuilder.Type.DIVINE_SCORCH).pos(targetPos.add(0, 0.1, 0))
                    .time(120).scale(radius * 2).face(EnumFacing.UP).spawn(world);

        }
        //Damage
        else {
            AxisAlignedBB target = new AxisAlignedBB(targetPos.x - radius,
                    targetPos.y, targetPos.z - radius, targetPos.x + radius,
                    targetPos.y + 50 * radius, targetPos.z + radius);
            List<Entity> targets = world.getEntitiesWithinAABB(Entity.class,
                    target);
            if (!targets.isEmpty()) {
                for (Entity hit : targets) {
                    if (DamageUtils.canDamage(entity, hit)) {
                        DamageUtils.attackEntity(entity,
                                hit, AvatarDamageSource.LIGHT,
                                damage, performance, this, xp);
                        double dx = hit.posX - entity.posX;
                        double dy = hit.posY - entity.posY;
                        double dz = hit.posZ - entity.posZ;
                        // Normalises the velocity.
                        double vectorLength = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                        dx /= vectorLength;
                        dy /= vectorLength;
                        dz /= vectorLength;

                        hit.motionX = push * dx;
                        hit.motionY = push * dy + 0.1;
                        hit.motionZ = push * dz;
                        AvatarUtils.afterVelocityAdded(hit);
                    }
                }
            }
        }
        //Spawn entity
        EntityBuff buff = new EntityBuff(world);
        buff.setLifetime(lifetime);
        buff.setRadius(radius);
        buff.setOwner(entity);
        buff.setElement(Lightbending.ID);
        buff.setAbility(this);
        buff.setPosition(targetPos);
        buff.setVelocity(Vector.ZERO);
        if (!world.isRemote)
            world.spawnEntity(buff);
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}
