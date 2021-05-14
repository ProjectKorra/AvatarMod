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

package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
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
import net.minecraft.entity.EntityLiving;
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
public class AbilityAirGust extends Ability {

    public static final String
            PIERCES_ENEMIES = "piercesEnemies",
            DESTROY_PROJECTILES = "destroyProjectiles",
            SLOW_PROJECTILES = "slowProjectiles";


    public AbilityAirGust() {
        super(Airbending.ID, "air_gust");
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
        float size = getProperty(SIZE, ctx).floatValue();
        int lifetime = getProperty(LIFETIME, ctx).intValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();
        float push = getProperty(KNOCKBACK, ctx).floatValue() / 2;

        //Xp and powerrating integration
        size *= ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier();
        speed += 5 * ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier();
        lifetime += 5 * ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier();
        push *= (1 + 0.5 * ctx.getPowerRatingDamageMod() * ctx.getAbilityData().getXpModifier());

        if (bender.consumeChi(getChiCost(ctx))) {
            EntityAirGust gust = new EntityAirGust(world);
            gust.setVelocity(look.times(speed));
            gust.setPosition(pos.minusY(0.5));
            gust.setOwner(entity);
            gust.setEntitySize(size);
            gust.setDamage(0);
            gust.setLifeTime(lifetime);
            gust.setPush(push / 1.25F);
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
            gust.setBehaviour(new AirGustBehaviour());
            gust.setDamageSource("avatar_Air");
            if (!world.isRemote)
                world.spawnEntity(gust);

            entity.swingArm(EnumHand.MAIN_HAND);

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
                            world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
                            .time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0.95F, 0.95F, 0.95F, 0.05F).spawnEntity(entity)
                            .scale(size * (1 / size)).element(new Airbending()).collide(true).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
                            world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
                            .time(10 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.05F).spawnEntity(entity)
                            .scale(size * (1 / size)).element(new Airbending()).collide(true).spawn(world);
                }
            }

            entity.swingArm(world.rand.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            ctx.getAbilityData().setRegenBurnout(true);
            entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, entity.getSoundCategory(), 1.0F + Math.max(ctx.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
            super.execute(ctx);
        }
    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiAirGust(this, entity, bender);
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    public static class AirGustBehaviour extends OffensiveBehaviour {

        @Override
        public Behavior<EntityOffensive> onUpdate(EntityOffensive entity) {
            if (entity != null) {
                World world = entity.world;
                if (world.isRemote && entity.getOwner() != null) {
                    int rings = (int) (Math.sqrt(entity.getAvgSize()) * 4);
                    float size = 0.75F * entity.getAvgSize() * (1 / entity.getAvgSize());
                    int particles = (int) (Math.min((int) (entity.getAvgSize() * Math.PI), 2) + (entity.velocity().magnitude() / 20));
                    Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(entity.velocity().toMinecraft().scale(0.002));
                    pos = pos.add(0, entity.getAvgSize() / 2, 0);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Airbending()).collide(true)
                            .clr(0.95F, 0.95F, 0.95F, 0.05F + (float) (0.0025F * entity.velocity().magnitude())).time(14 + AvatarUtils.getRandomNumberInRange(0, 10))
                            .scale(size).spawnEntity(entity).swirl(rings, particles, entity.getAvgSize() * 0.75F,
                            size / 2, (float) (entity.velocity().magnitude() * 10), (1 / size), entity,
                            world, true, pos, ParticleBuilder.SwirlMotionType.IN,
                            false);
                }
                entity.motionX *= 0.95;
                entity.motionY *= 0.95;
                entity.motionZ *= 0.95;
                if (entity.velocity().sqrMagnitude() < 0.5 * 0.5)
                    entity.Dissipate();
                float expansionRate = 1f / 80;
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
