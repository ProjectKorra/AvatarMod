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

package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityWave;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Are you proud of me, dad? I'm outlining how this should work in the documentation.
 * I'm actually following conventions. Praise be!
 * <p>
 * Wave -- Waterbending
 * This ability is designed for mobility and offense. It's a staple of waterbenders, and allows them
 * to dominate in water, snow, or rain. Charge up to create a bigger wave! As with most waterbending abilities,
 * it can be frozen and electrocuted.
 * Rideable.
 * <p>
 * Level 1 - Simple Wave.
 * Level 2 - Faster, Stronger, Bigger, Cooler. Ya know. Pierces. Works on land (with applicable water source nearby).
 * Level 3 - It's now rideable! Woo! Bigger source radius! Drawn from plants!
 * Level 4 Path 1 : Sundering Tsunami - Waves are now closer to geysers of water in a line. Think ravine but water and stronger.
 * Level 4 Path 2 : Voluminous Falls - Waves are now thicker, and can be controlled/charged into shapes (walls)!
 *
 * @author CrowsofWar, FavouriteDragon (mainly me)
 * <p>
 * TODO: Finish property file
 */
public class AbilityCreateWave extends Ability {

    public static final String
            GROW = "grow",
            PULLS = "pulls",
            RIDEABLE = "rideable",
            LAND = "land";

    public AbilityCreateWave() {
        super(Waterbending.ID, "wave");
    }


    @Override
    public void init() {
        super.init();
        addBooleanProperties(PULLS, GROW, RIDEABLE, LAND);
    }

    //Of course it doesn't work anymore. TODO: Fix.
    @Override
    public void execute(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityCreateWave abilityWave = (AbilityCreateWave) Abilities.get("wave");
        AbilityData abilityData = ctx.getAbilityData();

        Vector look = Vector.getLookRectangular(entity).times(0.25);
        if (bender.consumeChi(getChiCost(ctx)) && abilityWave != null) {
            //Need to fix my logic tbh
            //Entity damage values and such go here
            float damage = getProperty(DAMAGE, ctx).floatValue();
            float speed = getProperty(SPEED, ctx).floatValue() * 4;
            int lifetime = getProperty(LIFETIME, ctx).intValue();
            float push = getProperty(KNOCKBACK, ctx).floatValue() / 2;
            float size = getProperty(SIZE, ctx).floatValue();

            damage = powerModify(damage, abilityData);
            speed = powerModify(speed, abilityData);
            lifetime = (int) powerModify(lifetime, abilityData);
            push = powerModify(push, abilityData);
            size = powerModify(size, abilityData);

            EntityWave wave = new EntityWave(world);
            wave.setOwner(entity);
            wave.setRunOnLand(getBooleanProperty(LAND, ctx));
            wave.setRideable(getBooleanProperty(RIDEABLE, ctx));
            wave.setDamage(damage);
            wave.setDistance(lifetime * speed / 10);
            wave.setAbility(this);
            wave.setPush(push);
            wave.setLifeTime(lifetime);
            wave.setTier(getCurrentTier(ctx));
            wave.setEntitySize(size, size * 1.75F);
            wave.setXp(getProperty(XP_HIT).floatValue());
            wave.rotationPitch = entity.rotationPitch;
            wave.rotationYaw = entity.rotationYaw;
            wave.setVelocity(look.times(speed).withY(0));
            wave.setGrows(getBooleanProperty(GROW, ctx));
            wave.setPulls(getBooleanProperty(PULLS, ctx));
            wave.setBehaviour(new WaveBehaviour());
            wave.setEntitySize(0.125F);
            wave.setExpandedHeight(size * 0.5F);
            wave.setExpandedWidth(size * 2F);


            //TODO: Fix positioning so that particles are consistent
            if (getBooleanProperty(RIDEABLE, ctx)) {
                //Add a status control here
            }

            //Consumes water at the end
            if (ctx.consumeWater(getProperty(WATER_AMOUNT, ctx).intValue())) {
                //It's working?? Why isn't it spawning?
                //At 1.5 it adjusts the height of the wave
                float adjustment = 0.5F * size;
                //0.1F is for normal ground level (water is slightly shorter)
                //The game hates me :(( so I had to use some hacks to make it spawn correctly
                IBlockState state = abilityData.getSourceBlock();
                if (!Waterbending.isBendable(this, state, entity))
                    wave.setPosition(abilityData.getSourceInfo().getBlockPos().add(0, adjustment, 0));
                else
                    wave.setPosition(abilityData.getSourceInfo().getBlockPos().add(0, adjustment + 1, 0));
                if (!world.isRemote) {
                    world.spawnEntity(wave);
                }
            } else bender.sendMessage("avatar.waterSourceFail");
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
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiWave(this, entity, bender);
    }

    public static class WaveBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity != null && entity.getOwner() != null) {
                World world = entity.world;
                if (entity instanceof EntityWave) {

                    if (world.isRemote) {
                        Vec3d look = Vector.getLookRectangular(entity).withY(0).toMinecraft();
                        Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).subtract(0, entity.getExpandedHitboxHeight() / 2, 0);
                        //It's maths time boys and girls
                        //We want kinda a curved triangle shape, so we need two curves (one with less height)
                        //Also need to optimise this; high levels kill it
                        double widthInc = entity.getExpandedHitboxWidth() / 10 + 0.2;
                        float particleSize = (float) (0.5F + entity.getExpandedHitboxWidth() / 4F);
                        for (double w = 0; w < entity.getExpandedHitboxWidth(); w += widthInc) {
                            //We want to start in the middle then go right and left/scale right and left
                            //Going right
                            Vec3d posRight = Vector.getOrthogonalVector(look, 90, w / 2).toMinecraft();
                            Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getExpandedHitboxHeight() * 0.075F + world.rand.nextDouble() / 10,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                            Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getExpandedHitboxHeight() * 0.1F + world.rand.nextDouble() / 4,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(lowerWaterVel).spawnEntity(entity).element(new Waterbending())
                                    .pos(pos.add(posRight)).scale(particleSize).collide(true).glow(true).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(upperWaterVel).spawnEntity(entity).element(new Waterbending())
                                    .pos(pos.add(posRight)).scale(particleSize).collide(true).glow(true).spawn(world);

                        }
                        for (double w = 0; w < entity.getExpandedHitboxWidth(); w += widthInc) {
                            //We want to start in the middle then go right and left/scale right and left
                            Vec3d posLeft = Vector.getOrthogonalVector(look, -90, w / 2).toMinecraft();
                            Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextDouble() / 10,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                            Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextDouble() / 4,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);

                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(lowerWaterVel)
                                    .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos.add(posLeft)).scale(particleSize)
                                    .collide(true).glow(true).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(upperWaterVel)
                                    .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos.add(posLeft)).scale(particleSize)
                                    .collide(true).glow(true).spawn(world);

                            //All foam behaviour is handled within the particle class (call .collide(true) and make the
                            //element waterbending.

                        }

                        Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getExpandedHitboxHeight() * 0.075F + world.rand.nextDouble() / 10,
                                world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                        Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getExpandedHitboxHeight() * 0.1F + world.rand.nextDouble() / 4,
                                world.rand.nextGaussian() / 60 + entity.motionZ * 2);

                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                .vel(lowerWaterVel)
                                .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos).scale(particleSize)
                                .collide(true).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                .vel(upperWaterVel)
                                .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos).scale(particleSize)
                                .collide(true).glow(true).spawn(world);

                    }

                    EntityWave wave = (EntityWave) entity;
                    if (wave.doesGrow()) {
                        wave.setExpandedWidth(wave.getExpandedHitboxWidth() * 1.025F);
                        wave.setExpandedHeight(wave.getExpandedHitboxHeight() * 1.025F);
                        wave.motionX *= 1.05;
                        wave.motionY *= 1.05;
                        wave.motionZ *= 1.05;
                    }

                    if (wave.doesPull()) {
                        //Maybe particle effects later?
                        //First, create an AABB, then pull enemies in.
                        AxisAlignedBB pullBox = entity.getExpandedHitbox().grow(entity.getExpandedHitboxWidth() / 4);
                        List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, pullBox);
                        if (!targets.isEmpty()) {
                            for (Entity target : targets) {
                                if (target != entity && target != entity.getOwner()) {
                                    if (DamageUtils.isValidTarget(entity, target)) {
                                        if (!world.isRemote) {
                                            Vec3d entityVel = entity.velocity().toMinecraft();
                                            Vec3d futurePos = entity.getPositionVector().add(entityVel.scale(1 / 20F));
                                            Vec3d targetPos = target.getPositionVector();
                                            Vec3d vel = futurePos.subtract(targetPos).scale(-1 / 10F);
                                            target.motionX = vel.x;
                                            target.motionY = 0.1;
                                            target.motionZ = vel.z;
                                            AvatarUtils.afterVelocityAdded(target);
                                        }
                                    }
                                }
                            }
                        }

                        //Now, particles or else it looks a bit weird
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
