package com.crowsofwar.avatar.bending.bending.custom.demonic;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityFlames;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

//Instantaneous Whip.
//Just use EntityFlames and make it demonic + custom particle behaviour
public class AbilityChaosLash extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public AbilityChaosLash() {
        super(Demonbending.ID, "chaos_lash");
    }

    @Override
    public void init() {
        super.init();
        addProperties(FIRE_TIME);
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }


    @Override
    public void execute(AbilityContext ctx) {


        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();

        float damage = getProperty(DAMAGE, ctx).floatValue();
        float size = getProperty(SIZE, ctx).floatValue();
        int lifetime = (int) (getProperty(LIFETIME, ctx).intValue() * 2.25);
        float speed = getProperty(SPEED, ctx).floatValue() / 14;
        float push = getProperty(KNOCKBACK, ctx).floatValue();
        float xp = getProperty(XP_HIT, ctx).floatValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();
        int fireTime = getProperty(FIRE_TIME, ctx).intValue();

        Vec3d look = entity.getLookVec().scale(speed);
        Vec3d startPos = entity.getPositionVector().add(entity.getLookVec().scale(0.05).add(0, entity.getEyeHeight() - 0.5, 0));
        EntityFlames whip = new EntityFlames(world);
        whip.setOwner(entity);
        whip.setDamageSource("avatar_Demonic_chaosLash");
        whip.setDamage(damage);
        whip.setFireTime(fireTime);
        whip.setXp(xp);
        whip.setAbility(this);
        whip.setTier(getCurrentTier(ctx));
        whip.setPush(push);
        whip.setPiercing(true);
        whip.setBehaviour(new WhipBehaviour());
        whip.setPerformanceAmount(performance);
        whip.setLifeTime(lifetime);
        whip.setEntitySize(size);
        whip.setElement(Demonbending.ID);
        whip.setVelocity(look);
        whip.setPosition(startPos);
        if (!world.isRemote)
            world.spawnEntity(whip);

        super.execute(ctx);
    }

    public static class WhipBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            //Loightning
            World world = entity.world;
            EntityLivingBase owner = entity.getOwner();
            if (owner != null) {
                List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getExpandedHitbox());

                if (!targets.isEmpty()) {
                    for (EntityLivingBase target : targets) {
                        if (DamageUtils.canDamage(entity, target)) {
                            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 10));
                        }
                    }
                }
                entity.motionX *= 1.275;
                entity.motionY *= 1.275;
                entity.motionZ *= 1.275;
                if (world.isRemote) {
                    int rings = (int) (entity.getAvgSize() * 2) + 4;
                    float size = 0.5F * entity.getAvgSize() * (1 / entity.getAvgSize() + 0.5F);
                    int particles = (int) (Math.min((int) (entity.getAvgSize() * Math.PI), 2) + (entity.velocity().magnitude() / 20));
                    Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(BendingStyles.get(Demonbending.ID)).collide(AvatarUtils.getRandomNumberInRange(1, 100) > 80)
                            .clr(255, 13, 13, 190).time(12).glow(true)
                            .scale(size * 0.75F).spawnEntity(entity).swirl(rings, particles, entity.getAvgSize() * 0.75F,
                            size / 3F, (float) (entity.velocity().sqrMagnitude() / 10 * entity.getAvgSize()), (-0.75F / size), entity,
                            world, false, centre, ParticleBuilder.SwirlMotionType.IN,
                            false, true);
                    int max = (int) (entity.getAvgSize() * 4);
                    for (int h = 0; h < max; h++) {
                        Vec3d pos = Vector.getOrthogonalVector(entity.getLookVec(), h * (360F / max) + (entity.ticksExisted % 360) * 20 *
                                (1 / entity.getAvgSize()), entity.getAvgSize() / 1.5F).toMinecraft();
                        Vec3d velocity;
                        Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(entity);

                        pos = pos.add(entityPos);
                        velocity = pos.subtract(entityPos).normalize();
                        velocity = velocity.scale(entity.velocity().sqrMagnitude() / 400000);
                        double spawnX = pos.x;
                        double spawnY = pos.y;
                        double spawnZ = pos.z;
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
                                world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
                                .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(255, 13, 13, 120).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 15)
                                .scale(entity.getAvgSize() / 2).element(BendingStyles.get(Demonbending.ID)).collide(AvatarUtils.getRandomNumberInRange(1, 100) > 80).collideParticles(AvatarUtils.getRandomNumberInRange(1, 100) > 80).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
                                world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
                                .time(16 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(50, 0, 0, 120).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 15)
                                .scale(entity.getAvgSize() / 2).element(BendingStyles.get(Demonbending.ID)).collide(AvatarUtils.getRandomNumberInRange(1, 100) > 80).collideParticles(AvatarUtils.getRandomNumberInRange(1, 100) > 80).spawn(world);

                    }
                    //Rope of lightning that does a big crack when the entity dies
                    if (entity.ticksExisted >= entity.getLifeTime() - 1) {
                        ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING)
                                .pos(Vector.getEntityPos(owner).toMinecraft().add(owner.getLookVec().scale(0.05).add(0, entity.getEyeHeight(), 0)))
                                .target(AvatarEntityUtils.getMiddleOfEntity(entity)).time(10).scale(entity.getAvgSize() * 2)
                                .spawnEntity(owner).clr(77, 13, 13).spawn(world);
                        for (double i = 0; i < entity.width; i += 0.025) {
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH)
                                    .scale(entity.getAvgSize()).clr(77, 13, 13).glow(true)
                                    .collide(true).spawnEntity(entity).element(BendingStyles.get(Demonbending.ID))
                                    .time(8).vel(world.rand.nextGaussian() / 10, world.rand.nextGaussian() / 10,
                                    world.rand.nextGaussian() / 10).pos(AvatarEntityUtils.getMiddleOfEntity(entity))
                                    .spawn(world);
                        }
                    } else ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING)
                            .pos(Vector.getEntityPos(owner).toMinecraft().add(owner.getLookVec().scale(0.05)).add(0, entity.getEyeHeight(), 0))
                            .target(AvatarEntityUtils.getMiddleOfEntity(entity)).time(1).scale(entity.getAvgSize() * 2 *
                                    entity.ticksExisted / entity.getLifeTime()).clr(77, 13, 13).spawnEntity(owner).spawn(world);
                    if (entity.onCollideWithSolid()) {
                        for (double i = 0; i < entity.width * 2; i += 0.025) {
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH)
                                    .scale(entity.getAvgSize() / 2).clr(255, 13, 13).glow(true)
                                    .collide(AvatarUtils.getRandomNumberInRange(1, 100) > 80).spawnEntity(entity).element(BendingStyles.get(Demonbending.ID))
                                    .time(16).vel(world.rand.nextGaussian() / 10, world.rand.nextGaussian() / 10,
                                    world.rand.nextGaussian() / 10).pos(AvatarEntityUtils.getMiddleOfEntity(entity))
                                    .spawn(world);
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {

        }

        @Override
        public void toBytes(PacketBuffer buf) {

        }

        @Override
        public void load(NBTTagCompound nbt) {

        }

        @Override
        public void save(NBTTagCompound nbt) {

        }
    }
}
