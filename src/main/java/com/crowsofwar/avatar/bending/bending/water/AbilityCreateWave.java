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
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityWave;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
            GEYSER = "geysers",
            CHARGEABLE = "chargeable",
            RIDEABLE = "rideable",
            LAND = "land";

    public AbilityCreateWave() {
        super(Waterbending.ID, "wave");
    }

    private static void spawnEntity(EntityOffensive spawner) {
        if (!spawner.world.isRemote) {
            BlockPos pos = new BlockPos(spawner.posX, spawner.posY, spawner.posZ);

            //TODO: BUG - Spawns weirdly/not at all with snow layers and such. Fix.
            if (spawner.world.getBlockState(pos.down()).getBlock() == Blocks.WATER) {

                // Falling blocks do the setting block to air themselves.
                //Fixed issues with floating blocks not appearing??? Now time to fix going up areas with
                //non-solid blocks.
                EntityFallingBlock fallingblock = new EntityFallingBlock(spawner.world, spawner.posX, (int) (spawner.posY - 0.5) + 0.5,
                        spawner.posZ, spawner.world.getBlockState(new BlockPos(spawner.posX, spawner.posY - 1, spawner.posZ)));
                fallingblock.motionY = 0.15 + spawner.getAvgSize() / 10;
                spawner.world.spawnEntity(fallingblock);
            }
        }
    }

    @Override
    public void init() {
        super.init();
        addProperties(SOURCE_ANGLES, SOURCE_RANGE, WATER_AMOUNT);
        addBooleanProperties(GEYSER, CHARGEABLE, RIDEABLE, PLANT_BEND, LAND);
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
            if (ctx.consumeWater(getProperty(WATER_AMOUNT, ctx).intValue())) {
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
                firstBendable |= getBooleanProperty(LAND, ctx) && world.getBlockState(pos1).isFullBlock();
                secondBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos2),
                        entity);
                secondBendable |= getBooleanProperty(LAND, ctx) && world.getBlockState(pos2).isFullBlock();

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
                wave.setVelocity(look.x() * speed / 5, 0, look.z() * speed / 5);
                if (getBooleanProperty(GEYSER, ctx))
                    wave.setBehaviour(new WaveGeyserBehaviour());
                else wave.setBehaviour(new WaveBehaviour());


                //TODO: Fix positioning so that particles are consistent
                if (getBooleanProperty(CHARGEABLE, ctx)) {
                    //Add a tick handler/stat ctrl for charging
                }
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

                        firstBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos1),
                                entity);
                        if (firstBendable) {
                            wave.setPosition(firstPos);
                        }
                    }
                }

                //Corrects the position of the wave
                BlockPos wavePos = new BlockPos(wave.posX, wave.getEntityBoundingBox().minY,
                        wave.posZ);
                Block waveBlock = world.getBlockState(wavePos).getBlock();
                Block belowBlock = world.getBlockState(wavePos.down()).getBlock();
                //Ensures the wave spawns right on top of the water; also limits the number of tries.
                int i = 0;
                while (Waterbending.isBendable(abilityWave, world.getBlockState(wavePos),
                        entity) && waveBlock != Blocks.AIR && i < getProperty(SOURCE_RANGE, ctx).intValue()) {
                    wavePos = wavePos.up();
                    waveBlock = world.getBlockState(wavePos).getBlock();
                    i++;
                }
                i = 0;
                while (belowBlock == Blocks.AIR && i < getProperty(SOURCE_RANGE, ctx).intValue()) {
                    wavePos = wavePos.down();
                    belowBlock = world.getBlockState(wavePos.down()).getBlock();
                    i++;
                }

                wave.setPosition(wave.posX, Math.round(wave.posY), wave.posZ);
                if (!world.isRemote && (firstBendable || secondBendable ||
                        Waterbending.isBendable(abilityWave, world.getBlockState(wavePos.down()),
                                entity))) {
                    world.spawnEntity(wave);
                }

            } else bender.sendMessage("avatar.waterSourceFail");
        }

        super.execute(ctx);
    }

    @Override
    public int getBaseTier() {
        return 2;
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
                if (entity.world.isRemote) {
                    World world = entity.world;
                    Vec3d look = Vector.getLookRectangular(entity).withY(0).toMinecraft();
                    Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).subtract(0, entity.height , 0);
                    Vec3d foamPos = AvatarEntityUtils.getMiddleOfEntity(entity).add(0, entity.height / 4, 0);
                    //It's maths time boys and girls
                    //We want kinda a curved triangle shape, so we need two curves (one with less height)
                    for (double w = 0; w < entity.width; w += 0.2) {
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

//                        //Foam
//                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).clr(255, 255, 255, 85)
//                                .time(8 + AvatarUtils.getRandomNumberInRange(0, 1))
//                                .vel(world.rand.nextGaussian() / 30, entity.getHeight() * 0.075F + world.rand.nextGaussian() / 30, world.rand.nextGaussian() / 30)
//                                .spawnEntity(entity).element(new Waterbending()).pos(foamPos.add(posRight)).scale(entity.getAvgSize() * 0.75F)
//                                .collide(true).glow(true).spawn(world);
//                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).clr(255, 255, 255, 85)
//                                .time(8 + AvatarUtils.getRandomNumberInRange(0, 1))
//                                .vel(world.rand.nextGaussian() / 30, entity.getHeight() * 0.1F + world.rand.nextGaussian() / 15, world.rand.nextGaussian() / 30)
//                                .spawnEntity(entity).element(new Waterbending()).pos(foamPos.add(posRight)).scale(entity.getAvgSize() * 0.75F)
//                                .collide(true).glow(true).spawn(world);
                    }
                    for (double w = 0; w < entity.width; w += 0.2) {
                        //We want to start in the middle then go right and left/scale right and left
                        Vec3d posLeft = Vector.getOrthogonalVector(look, -90, w / (entity.width)).toMinecraft();
                        Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextDouble() / 10,
                                world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                        Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextDouble() / 4,
                                world.rand.nextGaussian() / 60 + entity.motionZ * 2);

                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                .vel(lowerWaterVel)
                                .spawnEntity(entity).element(new Waterbending()).pos(pos.add(posLeft)).scale(entity.getAvgSize())
                                .collide(true).glow(true).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                                .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                                .vel(upperWaterVel)
                                .spawnEntity(entity).element(new Waterbending()).pos(pos.add(posLeft)).scale(entity.getAvgSize())
                                .collide(true).glow(true).spawn(world);

//                        //Foam
//                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).clr(255, 255, 255, 85)
//                                .time(8 + AvatarUtils.getRandomNumberInRange(0, 1))
//                                .vel(world.rand.nextGaussian() / 30 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextGaussian() / 30,
//                                        world.rand.nextGaussian() / 30 + entity.motionZ * 2)
//                                .spawnEntity(entity).element(new Waterbending()).pos(foamPos.add(posLeft)).scale(entity.getAvgSize() * 0.75F)
//                                .collide(true).glow(true).spawn(world);
//                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).clr(255, 255, 255, 85)
//                                .time(8 + AvatarUtils.getRandomNumberInRange(0, 1))
//                                .vel(world.rand.nextGaussian() / 30 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextGaussian() / 15,
//                                        world.rand.nextGaussian() / 30 * entity.motionZ * 2)
//                                .spawnEntity(entity).element(new Waterbending()).pos(foamPos.add(posLeft)).scale(entity.getAvgSize() * 0.75F)
//                                .collide(true).glow(true).spawn(world);
                    }

                    Vec3d lowerWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextDouble() / 10,
                            world.rand.nextGaussian() / 60 + entity.motionZ * 2);
                    Vec3d upperWaterVel = new Vec3d(world.rand.nextGaussian() / 60 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextDouble() / 4,
                            world.rand.nextGaussian() / 60 + entity.motionZ * 2);

                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                            .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                            .vel(lowerWaterVel)
                            .spawnEntity(entity).element(new Waterbending()).pos(pos).scale(entity.getAvgSize())
                            .collide(true).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(0, 200, 255, 75)
                            .time(12 + AvatarUtils.getRandomNumberInRange(0, 1)).gravity(true)
                            .vel(upperWaterVel)
                            .spawnEntity(entity).element(new Waterbending()).pos(pos).scale(entity.getAvgSize())
                            .collide(true).glow(true).spawn(world);

//                    //Foam
//                    ParticleBuilder.create(ParticleBuilder.Type.SNOW).clr(255, 255, 255, 85)
//                            .time(8 + AvatarUtils.getRandomNumberInRange(0, 1))
//                            .vel(world.rand.nextGaussian() / 30 + entity.motionX * 2, entity.getHeight() * 0.075F + world.rand.nextGaussian() / 30,
//                                    world.rand.nextGaussian() / 30 * entity.motionZ * 2)
//                            .spawnEntity(entity).element(new Waterbending()).pos(foamPos).scale(entity.getAvgSize())
//                            .collide(true).glow(true).spawn(world);
//                    ParticleBuilder.create(ParticleBuilder.Type.SNOW).clr(255, 255, 255, 85)
//                            .time(8 + AvatarUtils.getRandomNumberInRange(0, 1))
//                            .vel(world.rand.nextGaussian() / 30 + entity.motionX * 2, entity.getHeight() * 0.1F + world.rand.nextGaussian() / 15,
//                                    world.rand.nextGaussian() / 30 * entity.motionZ * 2)
//                            .spawnEntity(entity).element(new Waterbending()).pos(foamPos).scale(entity.getAvgSize())
//                            .collide(true).glow(true).spawn(world);
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

    //Makes it spawn a line of geysers!
    public static class WaveGeyserBehaviour extends OffensiveBehaviour {

        @Override
        public Behavior onUpdate(EntityOffensive entity) {
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
