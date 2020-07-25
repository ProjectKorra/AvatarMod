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

package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.blocks.BlockUtils;
import com.crowsofwar.avatar.client.particle.AvatarParticles;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityFlames;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class AbilityFireShot extends Ability {

    public static final String SHOCKWAVE = "shockwave", REFLECT = "reflect", TRAILING_FIRE = "trailingFire";

    public AbilityFireShot() {
        super(Firebending.ID, "fire_shot");
        requireRaytrace(-1, false);
    }


    @Override
    public void init() {
        super.init();
        addProperties(FIRE_R, FIRE_G, FIRE_B, FADE_R, FADE_G, FADE_B, EFFECT_RADIUS);
        addBooleanProperties(SETS_FIRES, SMELTS, SHOCKWAVE, REFLECT, TRAILING_FIRE);
    }


    @Override
    public void execute(AbilityContext ctx) {

        World world = ctx.getWorld();
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getAbilityData();

        Vector pos = Vector.getEyePos(entity);

        float speed = getProperty(SPEED, ctx).floatValue() * 2;
        float knockback = getProperty(KNOCKBACK, ctx).floatValue() / 10;
        float size = getProperty(SIZE, ctx).floatValue();
        float damage = getProperty(DAMAGE, ctx).floatValue();
        float chi = getChiCost(ctx);
        float xp = getProperty(XP_HIT, ctx).floatValue();

        int fireTime = getProperty(FIRE_TIME, ctx).intValue();
        int lifeTime = getProperty(LIFETIME, ctx).intValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();

        double damageMult = bender.getDamageMult(Firebending.ID);
        double powerrating = bender.calcPowerRating(Firebending.ID);

        damage *= abilityData.getXpModifier() * damageMult;
        size *= (abilityData.getXpModifier() * (powerrating / 100) + 1);
        speed *= abilityData.getXpModifier() * damageMult;
        knockback *= abilityData.getXpModifier() * damageMult;
        fireTime *= Math.min(abilityData.getXpModifier() * damageMult, 0.25F);
        lifeTime *= Math.min(abilityData.getXpModifier() * damageMult, 0.25F);

        if (bender.consumeChi(chi)) {
            if (!getBooleanProperty(SHOCKWAVE, ctx)) {
                //TODO: Fix trailing fire and piercing

                EntityFlames flames = new EntityFlames(world);
                flames.setVelocity(Vector.getLookRectangular(entity).times(speed));
                flames.setOwner(entity);
                flames.setPosition(pos.minusY(size / 2));
                flames.rotationYaw = entity.rotationYaw;
                flames.rotationPitch = entity.rotationPitch;
                flames.setEntitySize(size);
                flames.setDynamicSpreadingCollision(true);
                flames.setReflect(getBooleanProperty(REFLECT, ctx));
                flames.setAbility(this);
                flames.setPerformanceAmount(performance);
                flames.setTier(getCurrentTier(ctx));
                flames.setXp(xp);
                flames.setLifeTime(lifeTime);
                flames.setTrailingFires(getBooleanProperty(TRAILING_FIRE, ctx));
                flames.setFireTime(fireTime);
                flames.setFires(getBooleanProperty(SETS_FIRES, ctx));
                flames.setDamage(damage);
                flames.setRGB(getProperty(FIRE_R, ctx).intValue(), getProperty(FIRE_G, ctx).intValue(), getProperty(FIRE_B, ctx).intValue());
                flames.setFade(getProperty(FADE_R, ctx).intValue(), getProperty(FADE_G, ctx).intValue(), getProperty(FADE_B, ctx).intValue());
                flames.setElement(new Firebending());
                flames.setPush(knockback);
                flames.setDamageSource("avatar_Fire_fireShot");
                world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.75F +
                        world.rand.nextFloat(), 0.5F + world.rand.nextFloat(), false);
                if (!world.isRemote)
                    world.spawnEntity(flames);

                if (world.isRemote) {
                    for (double angle = 0; angle < 360; angle += 8) {
                        Vector position = Vector.getOrthogonalVector(entity.getLookVec(), angle, size / 20F);
                        Vector velocity;
                        position = position.plus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity)));
                        velocity = position.minus(pos.minusY(0.05).plus(Vector.getLookRectangular(entity))).normalize();
                        velocity = velocity.times(speed / 300);
                        double spawnX = position.x();
                        double spawnY = position.y();
                        double spawnZ = position.z();
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
                                world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
                                .time(8 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(1F, 10 / 255F, 5 / 255F, 0.75F).spawnEntity(entity)
                                .scale(size / 2F).element(new Firebending()).collide(true).ability(this).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x(),
                                world.rand.nextGaussian() / 80 + velocity.y(), world.rand.nextGaussian() / 80 + velocity.z())
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(1F, (40 + AvatarUtils.getRandomNumberInRange(0, 60)) / 255F,
                                10 / 255F, 0.75F).spawnEntity(entity)
                                .scale(size / 2F).element(new Firebending()).collide(true).ability(this).spawn(world);
                    }
                }
            } else {
                EntityShockwave wave = new EntityShockwave(world);
                wave.setOwner(entity);
                wave.rotationPitch = entity.rotationPitch;
                wave.rotationYaw = entity.rotationYaw;
                wave.setPosition(entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0));
                wave.setFireTime(fireTime);
                wave.setElement(new Firebending());
                wave.setAbility(this);
                wave.setParticle(AvatarParticles.getParticleFlames());
                wave.setDamage(damage);
                wave.setPerformanceAmount(performance);
                wave.setBehaviour(new FireShockwaveBehaviour());
                wave.setSpeed(speed);
                wave.setPush(knockback);
                wave.setKnockbackHeight(0.15);
                wave.setParticleSpeed(0.18F);
                wave.setParticleWaves(2);
                wave.setParticleAmount(10);
                world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.75F +
                        world.rand.nextFloat(), 0.5F + world.rand.nextFloat(), false);
                if (!world.isRemote)
                    world.spawnEntity(wave);
            }
        }

    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiFireShot(this, entity, bender);
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    public static class FireShockwaveBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity.getOwner() != null) {
                if (entity instanceof EntityShockwave) {
                    for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (entity.ticksExisted * 3)) {
                        int x = entity.posX < 0 ? (int) (entity.posX + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed())) * Math.sin(angle) - 1)
                                : (int) (entity.posX + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle)));
                        int z = entity.posZ < 0 ? (int) (entity.posZ + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle) - 1))
                                : (int) (entity.posZ + ((entity.ticksExisted * ((EntityShockwave) entity).getSpeed())) * Math.cos(angle));

                        BlockPos spawnPos = new BlockPos(x, (int) (entity.posY), z);
                        if (BlockUtils.canPlaceFireAt(entity.world, spawnPos)) {
                            if (spawnPos != entity.getPosition()) {
                                int time = entity.ticksExisted * ((EntityShockwave) entity).getSpeed() >= ((EntityShockwave) entity).getRange() - 0.2 ? 120 : 10;
                                BlockTemp.createTempBlock(entity.world, spawnPos, time, Blocks.FIRE.getDefaultState());
                            }
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
