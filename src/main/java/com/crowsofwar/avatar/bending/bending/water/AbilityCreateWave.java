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
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
        addProperties(SOURCE_ANGLES, SOURCE_RANGE, WATER_AMOUNT);
        addBooleanProperties(PULLS, GROW, RIDEABLE, PLANT_BEND, LAND);
    }

    @Override
    public void execute(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityCreateWave abilityWave = (AbilityCreateWave) Abilities.get("wave");
        AbilityData abilityData = ctx.getAbilityData();

        Vector look = Vector.getLookRectangular(entity).times(0.25);
        Vector pos = Vector.getEntityPos(entity);
        if (bender.consumeChi(getChiCost(ctx)) && abilityWave != null) {
            //Entity damage values and such go here
            float damage = getProperty(DAMAGE, ctx).floatValue();
            float speed = getProperty(SPEED, ctx).floatValue() * 2;
            int lifetime = getProperty(LIFETIME, ctx).intValue();
            float push = getProperty(KNOCKBACK, ctx).floatValue() / 2;
            float size = getProperty(SIZE, ctx).floatValue();

            damage = powerModify(damage, abilityData);
            speed = powerModify(speed, abilityData);
            lifetime = (int) powerModify(lifetime, abilityData);
            push = powerModify(push, abilityData);
            size = powerModify(size, abilityData);

            //Logic for spawning the wave
            Vector firstPos = pos.plus(look).minusY(1);
            Vector secondPos = firstPos.minusY(1);
            BlockPos pos1 = firstPos.toBlockPos();
            BlockPos pos2 = secondPos.toBlockPos();
            boolean firstBendable;
            boolean secondBendable;

            //Either the wave can go on land or there's a compatible block to use
            firstBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos1),
                    entity);
            firstBendable |= getBooleanProperty(LAND, ctx) && world.getBlockState(pos1).isFullBlock() &&
                    world.getBlockState(pos1).getBlock() != Blocks.AIR;
            secondBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos2),
                    entity);
            secondBendable |= getBooleanProperty(LAND, ctx) && world.getBlockState(pos2).isFullBlock() &&
                    world.getBlockState(pos1).getBlock() != Blocks.AIR;

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
            wave.setVelocity(look.times(speed / 5).withY(0));
            wave.setGrows(getBooleanProperty(GROW, ctx));
            wave.setPulls(getBooleanProperty(PULLS, ctx));
            wave.setBehaviour(new WaveBehaviour());
            wave.setEntitySize(size * 0.5F, size * 2F);


            //TODO: Fix positioning so that particles are consistent
            if (getBooleanProperty(RIDEABLE, ctx)) {
                //Add a status control here
            }

            //Block at feet is bendable
            if (firstBendable) {
                //Pos is the source block, we want it to be above the source block
                wave.setPosition(firstPos.plusY(1));
            }
            //Block below feet is bendable
            else if (secondBendable) {
                //Same thing here. Above source block.
                wave.setPosition(secondPos.plusY(1));
            }
            //If the blocks beneath the player's feet aren't bendable
            else {
                firstPos = Waterbending.getClosestWaterbendableBlock(entity,
                        abilityWave, ctx);

                if (firstPos != null) {
                    pos1 = firstPos.toBlockPos();
                    BlockPos wavePos = pos1;
                    Block waveBlock = world.getBlockState(wavePos).getBlock();
                    Block belowBlock = world.getBlockState(wavePos.down()).getBlock();
                    //Ensures the wave spawns right on top of the water; also limits the number of tries.
//                        int i = 0;
//                        while (Waterbending.isBendable(abilityWave, world.getBlockState(wavePos),
//                                entity) && waveBlock != Blocks.AIR && i < getProperty(SOURCE_RANGE, ctx).intValue()) {
//                            wavePos = wavePos.up();
//                            waveBlock = world.getBlockState(wavePos).getBlock();
//                            i++;
//                        }
//                        i = 0;
//                        while (belowBlock == Blocks.AIR && i < getProperty(SOURCE_RANGE, ctx).intValue()) {
//                            wavePos = wavePos.down();
//                            belowBlock = world.getBlockState(wavePos.down()).getBlock();
//                            i++;
//                        }
                    firstBendable = Waterbending.isBendable(abilityWave, world.getBlockState(wavePos),
                            entity);
                    if (firstBendable) {
                        wave.setPosition(wavePos.up());
                        //Corrects the position of the wave
                    }
                } else
                    bender.sendMessage("avatar.waterSourceFail");

            }

            //Consumes water at the end
            if (ctx.consumeWater(getProperty(WATER_AMOUNT, ctx).intValue())) {
                //It's working?? Why isn't it spawning?
                //At 1.5 it adjusts the height of the wave
                float adjustment = 0;
                if (size * 10 % 10 == 5)
                    adjustment = 0.5F;
                //0.1F is for normal ground level (water is slightly shorter)
                wave.setPosition(wave.posX, Math.round(wave.posY + adjustment) + 0.1F, wave.posZ);
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
                        Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).subtract(0, entity.height / 2, 0);
                        //It's maths time boys and girls
                        //We want kinda a curved triangle shape, so we need two curves (one with less height)
                        //Also need to optimise this; high levels kill it
                        double widthInc = entity.width / 10 + 0.2;
                        for (double w = 0; w < entity.width; w += widthInc) {
                            //We want to start in the middle then go right and left/scale right and left
                            //Going right
                            Vec3d posRight = Vector.getOrthogonalVector(look, 90, w / (entity.width)).toMinecraft();
                            Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextDouble() / 10,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                            Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextDouble() / 4,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(lowerWaterVel).spawnEntity(entity).element(new Waterbending())
                                    .pos(pos.add(posRight)).scale(entity.getAvgSize()).collide(true).glow(true).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(upperWaterVel).spawnEntity(entity).element(new Waterbending())
                                    .pos(pos.add(posRight)).scale(entity.getAvgSize()).collide(true).glow(true).spawn(world);

                        }
                        for (double w = 0; w < entity.width; w += widthInc) {
                            //We want to start in the middle then go right and left/scale right and left
                            Vec3d posLeft = Vector.getOrthogonalVector(look, -90, w / (entity.width)).toMinecraft();
                            Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextDouble() / 10,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                            Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextDouble() / 4,
                                    world.rand.nextGaussian() / 60 + entity.motionZ * 2);

                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(lowerWaterVel)
                                    .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos.add(posLeft)).scale(entity.getAvgSize())
                                    .collide(true).glow(true).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                    .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                    .vel(upperWaterVel)
                                    .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos.add(posLeft)).scale(entity.getAvgSize())
                                    .collide(true).glow(true).spawn(world);

                            //All foam behaviour is handled within the particle class (call .collide(true) and make the
                            //element waterbending.

                        }

                        Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextDouble() / 10,
                                world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                        Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextDouble() / 4,
                                world.rand.nextGaussian() / 60 + entity.motionZ * 2);

                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                .vel(lowerWaterVel)
                                .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos).scale(entity.getAvgSize())
                                .collide(true).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                .vel(upperWaterVel)
                                .spawnEntity(entity).element(BendingStyles.get(Waterbending.ID)).pos(pos).scale(entity.getAvgSize())
                                .collide(true).glow(true).spawn(world);

                    }

                    EntityWave wave = (EntityWave) entity;
                    if (wave.doesGrow()) {
                        wave.setEntitySize(wave.getHeight() * 1.025F, wave.getWidth() * 1.025F);
                        wave.motionX *= 1.05;
                        wave.motionY *= 1.05;
                        wave.motionZ *= 1.05;
                    }

                    if (wave.doesPull()) {
                        //Maybe particle effects later?
                        //First, create an AABB, then pull enemies in.
                        AxisAlignedBB pullBox = entity.getExpandedHitbox().grow(entity.width / 4);
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
