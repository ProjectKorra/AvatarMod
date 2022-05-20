/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.bending.bending.custom.abyss;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityAirGust;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class AbilityAbyssalBlast extends Ability {

    public static final String
            PIERCES_ENEMIES = "piercesEnemies",
            DESTROY_PROJECTILES = "destroyProjectiles",
            SLOW_PROJECTILES = "slowProjectiles";


    public AbilityAbyssalBlast() {
        super(Abyssbending.ID, "abyss_blast");
    }

    private static float getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 25) / 255F;
    }

    @Override
    public void init() {
        super.init();
        addBooleanProperties(PUSH_IRON_TRAPDOOR, PUSH_IRONDOOR, PUSH_STONE, PUSH_REDSTONE, PIERCES_ENEMIES, DESTROY_PROJECTILES, SLOW_PROJECTILES);
    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();

        Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
                Math.toRadians(entity.rotationPitch));
        Vector pos = Vector.getEyePos(entity);

        float speed = getProperty(SPEED, ctx).floatValue();
        float damage = getProperty(DAMAGE, ctx).floatValue();
        float size = getProperty(SIZE, ctx).floatValue();
        int lifetime = getProperty(LIFETIME, ctx).intValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();
        float push = getProperty(KNOCKBACK, ctx).floatValue() / 2;

        //Xp and powerrating integration
        size *= ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier();
        speed += 5 * ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier();
        lifetime += 5 * ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier();
        push *= (1 + 0.5 * ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier());
        damage = powerModify(damage, ctx.getAbilityData());

        if (bender.consumeChi(getChiCost(ctx))) {
            EntityAirGust gust = new EntityAirGust(world);
            gust.setVelocity(look.times(speed));
            gust.setPosition(pos.minusY(0.5));
            gust.setOwner(entity);
            gust.setEntitySize(size);
            gust.setDamage(0);
            gust.setLifeTime(lifetime);
            gust.setPush(push / 1.25F);
            gust.setDamage(damage);
            gust.rotationPitch = entity.rotationPitch;
            gust.rotationYaw = entity.rotationYaw;
            gust.setPerformanceAmount(performance);
            gust.setPushRedstone(getBooleanProperty(PUSH_REDSTONE, ctx));
            gust.setPushStone(getBooleanProperty(PUSH_STONE, ctx));
            gust.setPushIronDoor(getBooleanProperty(PUSH_IRONDOOR, ctx));
            gust.setPushIronTrapDoor(getBooleanProperty(PUSH_IRON_TRAPDOOR, ctx));
            gust.setDestroyProjectiles(getBooleanProperty(DESTROY_PROJECTILES, ctx));
            gust.setSlowProjectiles(getBooleanProperty(SLOW_PROJECTILES, ctx));
            gust.setPiercing(getBooleanProperty(PIERCES_ENEMIES, ctx));
            gust.setAbility(this);
            gust.setTier(getCurrentTier(ctx));
            gust.setChiHit(getProperty(CHI_HIT, ctx).floatValue());
            gust.setXp(getProperty(XP_HIT, ctx).floatValue());
            gust.setBehaviour(new AbyssalBlastBehaviour());
            gust.setDamageSource("avatar_Ki");
            if (!world.isRemote)
                world.spawnEntity(gust);


            if (world.isRemote) {
                for (double angle = 0; angle < 360; angle += Math.max((int) (size * 15), 15)) {
                    Vector position = Vector.getOrthogonalVector(entity.getLookVec(), angle, size / 20F);
                    Vector velocity;
                    position = position.plus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity)));
                    velocity = position.minus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity))).normalize();
                    velocity = velocity.times(speed / 400);
                    double spawnX = position.x();
                    double spawnY = position.y();
                    double spawnZ = position.z();
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
                                    world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z()).glow(world.rand.nextBoolean())
                            .time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0.05F, 0.05F, 0.05F, 0.05F).spawnEntity(entity)
                            .scale(size * (1 / size)).element(BendingStyles.get(Abyssbending.ID)).collide(world.rand.nextBoolean()).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
                                    world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z()).glow(false)
                            .time(10 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.05F, 0.05F, 0.05F, 0.05F).spawnEntity(entity)
                            .scale(size * (1 / size)).element(BendingStyles.get(Abyssbending.ID)).collide(world.rand.nextBoolean()).spawn(world);
                    entity.swingArm(world.rand.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                }
            }


            ctx.getAbilityData().setRegenBurnout(true);
            entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_LIGHTNING_IMPACT, entity.getSoundCategory(), 1.0F + Math.max(ctx.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
            super.execute(ctx);
        }
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    public static class AbyssalBlastBehaviour extends OffensiveBehaviour {

        @Override
        public Behavior<EntityOffensive> onUpdate(EntityOffensive entity) {
            if (entity != null) {
                World world = entity.world;
                if (world.isRemote && entity.getOwner() != null) {
                    int rings = (int) (entity.getAvgSize() * 2) + 4;
                    float size = 0.75F * entity.getAvgSize() * (1 / entity.getAvgSize() + 0.5F);
                    int particles = (int) (Math.min((int) (entity.getAvgSize() * Math.PI), 2) + (entity.velocity().magnitude() / 20));
                    Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(BendingStyles.get(Airbending.ID)).collide(AvatarUtils.getRandomNumberInRange(1, 100) > 90)
                            .clr(getClrRand(), getClrRand(), getClrRand(), 0.35F).time(12).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 96)
                            .scale(size * 0.75F).spawnEntity(entity).swirl(rings, particles, entity.getAvgSize() * 0.75F,
                                    size / 3F, (float) (entity.velocity().sqrMagnitude() / 10 * entity.getAvgSize()), (-0.75F / size), entity,
                                    world, true, centre, ParticleBuilder.SwirlMotionType.IN,
                                    false, true);
                    int max = (int) (entity.getAvgSize() * 5);
                    for (int i = 0; i < max; i++) {
                        Vec3d pos = Vector.getOrthogonalVector(entity.getLookVec(), i * (360F / max) + (entity.ticksExisted % 360) * 25 *
                                (1 / entity.getAvgSize()), entity.getAvgSize() / 2F).toMinecraft();
                        Vec3d velocity;
                        Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(entity);

                        pos = pos.add(entityPos);
                        velocity = pos.subtract(entityPos).normalize();
                        velocity = velocity.scale(entity.velocity().sqrMagnitude() / 400000);
                        double spawnX = pos.x;
                        double spawnY = pos.y;
                        double spawnZ = pos.z;
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
                                        world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 90)
                                .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(getClrRand(), getClrRand(), getClrRand(), 0.5F).spawnEntity(entity)
                                .scale(1.25F * entity.getAvgSize() * (1 / entity.getAvgSize())).element(BendingStyles.get(Airbending.ID)).collide(AvatarUtils.getRandomNumberInRange(1, 100) > 99)
                                .collideParticles(AvatarUtils.getRandomNumberInRange(1, 100) > 70).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
                                        world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 90)
                                .time(16 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(getClrRand(), getClrRand(), getClrRand(), 0.2F).spawnEntity(entity)
                                .scale(1.25F * entity.getAvgSize() * (1 / entity.getAvgSize())).element(BendingStyles.get(Airbending.ID)).spawn(world);

                    }
                }
                entity.motionX *= 0.975;
                entity.motionY *= 0.975;
                entity.motionZ *= 0.975;
                if (entity.velocity().sqrMagnitude() < 0.75 * 0.75)
                    entity.Dissipate();
                float expansionRate = 1f / 60;
                entity.setEntitySize(entity.getAvgSize() + expansionRate);
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
