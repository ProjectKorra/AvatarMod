package com.crowsofwar.avatar.bending.bending.custom.dark;

import com.crowsofwar.avatar.bending.bending.Ability;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class AbilityWorldEnd extends Ability {

    public AbilityWorldEnd() {
        super(Darkbending.ID, "world_end");
    }

    @Override
    public void init() {
        super.init();
        addProperties(EFFECT_RADIUS, EFFECT_DURATION);
    }

    @Override
    public boolean isOffensive() {
        return true;
    }


    @Override
    public void execute(AbilityContext ctx) {
        super.execute(ctx);
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
        int beamTime = getProperty(LIFETIME, ctx).intValue();


        world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERDRAGON_DEATH,
                SoundCategory.PLAYERS, 2.0F, 0.5F, true);

        //Spawn particles
        if (world.isRemote) {
            AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity, Vec3d.ZERO,
                    (int) (200 * Math.PI * 4), 200, 0.1F, radius * 5, ParticleBuilder.Type.FLASH, targetPos,
                    new Vec3d(0.075, 0.075, 0.075), Vec3d.ZERO, true, 30, 0, 50, 120, false, beamTime, radius / 2, true, 0);
            ParticleBuilder.create(ParticleBuilder.Type.SCORCH).pos(targetPos.add(0, 0.1, 0))
                    .time(lifetime).scale(radius * 2).face(EnumFacing.UP).spawn(world);

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
                                hit, AvatarDamageSource.DARK,
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
        buff.setElement(Darkbending.ID);
        buff.setAbility(this);
        buff.setPosition(targetPos);
        buff.setVelocity(Vector.ZERO);
        if (!world.isRemote)
            world.spawnEntity(buff);
    }
}
