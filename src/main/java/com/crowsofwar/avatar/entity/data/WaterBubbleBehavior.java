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

package com.crowsofwar.avatar.entity.data;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.water.AbilityFlowControl;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AvatarWorldData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public abstract class WaterBubbleBehavior extends OffensiveBehaviour {

    protected WaterBubbleBehavior() {
    }

    public static void register() {
        registerBehavior(Drop.class);
        registerBehavior(PlayerControlled.class);
        registerBehavior(Lobbed.class);
        registerBehavior(Grow.class);
        registerBehavior(ShieldShrink.class);
        registerBehavior(StreamShrink.class);
        //When you use the water bubble like a bucket
    }

    //For some reason this works extremely well at high velocities for a beam. Replace water blast with this.
    private static void bubbleSwirl(EntityOffensive entity, World world) {
        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 50).gravity(true)
                .time(16).scale(0.5F).spawnEntity(entity).element(BendingStyles.get(Waterbending.ID))
                .spin(entity.getAvgSize() / 10, world.rand.nextGaussian() / 20)
                .swirl((int) (entity.getAvgSize() * 12), (int) (entity.getAvgSize() * 2 * Math.PI),
                        entity.getAvgSize() * 0.85F, entity.getAvgSize() * 5, ((EntityWaterBubble) entity).getDegreesPerSecond()
                                * entity.getAvgSize(),
                        (float) (world.rand.nextGaussian() / 8F), entity, world, true, AvatarEntityUtils.getBottomMiddleOfEntity(entity),
                        ParticleBuilder.SwirlMotionType.OUT, false, true);
    }

    public static class Drop extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            entity.addVelocity(Vector.DOWN.times(0.981));
            if (entity.collided) {
                entity.setDead();
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

    public static class PlayerControlled extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            EntityLivingBase owner = entity.getOwner();
            World world = entity.world;

            if (owner == null) return this;
            if (!(entity instanceof EntityWaterBubble)) return this;

            BendingData data = BendingData.getFromEntity(owner);
            entity.rotationPitch = owner.rotationPitch;
            entity.rotationYaw = owner.rotationYaw;


            Vec3d pos = Vector.getEntityPos(owner).toMinecraft();
            Vec3d look = owner.getLookVec().scale(2.5).add(0, owner.getEyeHeight() / 2, 0);

            if (((EntityWaterBubble) entity).getState() == EntityWaterBubble.State.STREAM) {
                AvatarEntityUtils.dragEntityTowardsPoint(entity,
                        AvatarEntityUtils.circularMotion(pos.add(0, look.y, 0), 10 * (int) entity.world.getWorldTime(),
                                0, 1), 0.125);
                //Visuals
                if (world.isRemote) {
                    //Because the shield uses a shrunk water bubble for a clean animation
                    float size = entity.getMaxEntitySize() * 0.75F;
                    //Overall sphere shape/swirl
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 50).gravity(true)
                            .time(16).scale(0.5F).spawnEntity(entity).element(BendingStyles.get(Waterbending.ID))
                            .spin(size / 10, world.rand.nextGaussian() / 20)
                            .swirl((int) (size * 12), (int) (size * 4 * Math.PI),
                                    size, size * 5, ((EntityWaterBubble) entity).getDegreesPerSecond()
                                            * size * 6,
                                    (float) (world.rand.nextGaussian() / 8F), entity, world, false, AvatarEntityUtils.getBottomMiddleOfEntity(entity),
                                    ParticleBuilder.SwirlMotionType.OUT, true, true);
                    ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 90).gravity(true)
                            .time(16).scale(0.5F).spawnEntity(entity).element(BendingStyles.get(Waterbending.ID))
                            .spin(size / 10, world.rand.nextGaussian() / 20)
                            .swirl((int) (size * 12), (int) (size * 4 * Math.PI),
                                    size, size * 3, ((EntityWaterBubble) entity).getDegreesPerSecond()
                                            * size * 6,
                                    (float) (world.rand.nextGaussian() / 8F), entity, world, true, AvatarEntityUtils.getBottomMiddleOfEntity(entity),
                                    ParticleBuilder.SwirlMotionType.OUT, true, true);
                }
//
                AbilityFlowControl control = (AbilityFlowControl) Abilities.get("flow_control");
                AbilityData abilityData = AbilityData.get(owner, "flow_control");
                if (control != null && abilityData != null) {
                    int frequency = control.getProperty(Ability.CHARGE_FREQUENCY, abilityData).intValue();
                    int amount = control.getProperty(Ability.CHARGE_AMOUNT, abilityData).intValue();
                    if (entity.ticksExisted % frequency == 0 && ((EntityWaterBubble) entity).getDegreesPerSecond() < entity.getMaxEntitySize() * 15)
                        ((EntityWaterBubble) entity).setDegreesPerSecond(((EntityWaterBubble) entity).getDegreesPerSecond() + amount);

                } else {
                    //Increases the spin speed
                    //Default
                    if (entity.ticksExisted % 6 == 0 && ((EntityWaterBubble) entity).getDegreesPerSecond() < entity.getMaxEntitySize() * 15)
                        ((EntityWaterBubble) entity).setDegreesPerSecond(((EntityWaterBubble) entity).getDegreesPerSecond() + 1);
                }
            } else {
                AvatarEntityUtils.dragEntityTowardsPoint(entity, pos.add(look), 0.125);

                //particles!
                if (world.isRemote && entity.getOwner() != null) {
                    //3 main types: BUBBLE, SHIELD, RING


                    if (((EntityWaterBubble) entity).getState().equals(EntityWaterBubble.State.BUBBLE)) {
                        //Particles are * 2 * PI because that's the circumference of a circle and idk.
                        //Use the bottom of the entity cause my method is bad and shifts the centre point up. Dw about it.
                        bubbleSwirl(entity, world);
                    }

                    //Because the shield uses a shrunk water bubble for a clean animation
                    float size = entity.getMaxEntitySize();
                    if (((EntityWaterBubble) entity).getState().equals(EntityWaterBubble.State.SHIELD)) {
                        //Overall sphere shape/swirl
                        ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 50).gravity(true)
                                .time(16).scale(0.5F).spawnEntity(entity).element(BendingStyles.get(Waterbending.ID))
                                .spin(size / 10, world.rand.nextGaussian() / 20)
                                .swirl((int) (size * 12), (int) (size * 4 * Math.PI),
                                        size, size * 5, ((EntityWaterBubble) entity).getDegreesPerSecond()
                                                * size * 6,
                                        (float) (world.rand.nextGaussian() / 8F), entity, world, true, AvatarEntityUtils.getBottomMiddleOfEntity(entity),
                                        ParticleBuilder.SwirlMotionType.OUT, true, true);
                        //Disc/spin.
                        //Loop for particles spinning around, and for how many particles within.
                        int max = (int) (size * 12);
                        for (int h = 0; h < max; h++) {
                            for (int i = 0; i < max; i++) {
                                pos = Vector.getOrthogonalVector(entity.getLookVec(), (i + 1) * (360F / max) + (entity.ticksExisted % 360) *
                                        ((EntityWaterBubble) entity).getDegreesPerSecond() * 5, size * (float) ((h + 1) / max) * 1.25F).toMinecraft();
                                pos = pos.add(0, -entity.getEyeHeight() / 2, 0);
                                Vec3d velocity;
                                Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(entity);

                                pos = pos.add(entityPos).add(entity.getLookVec().scale(0.5F * size));
                                velocity = pos.subtract(entityPos).normalize();
                                velocity = velocity.scale(entity.velocity().sqrMagnitude() / 400000);
                                double spawnX = pos.x;
                                double spawnY = pos.y;
                                double spawnZ = pos.z;
                                //We want at least 1 particle to collide for vfx, so if fire hits it, it looks cool
                                ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 20 + velocity.x,
                                                world.rand.nextGaussian() / 20 + velocity.y, world.rand.nextGaussian() / 20 + velocity.z)
                                        .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(1F, 1F, 1F, 0.3F).spawnEntity(entity).collide(world.rand.nextBoolean())
                                        .scale(0.75F).element(BendingStyles.get(Waterbending.ID)).spawn(world);
                                ParticleBuilder.create(ParticleBuilder.Type.CUBE).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 20 + velocity.x,
                                                world.rand.nextGaussian() / 20 + velocity.y, world.rand.nextGaussian() / 20 + velocity.z)
                                        .time(16 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(1F, 1F, 1F, 0.3F).spawnEntity(entity)
                                        .scale(0.75F).element(BendingStyles.get(Waterbending.ID)).spawn(world);

                            }
                        }
                    }
                }
            }

            if (data != null) {
                if (entity.ticksExisted % 4 == 0) {
                    //Shield, swirl, throw
                    if (!data.hasStatusControl(StatusControlController.RESET_SHIELD_BUBBLE))
                        data.addStatusControl(StatusControlController.SHIELD_BUBBLE);
                    if (!data.hasStatusControl(StatusControlController.RESET_SWIRL_BUBBLE))
                        data.addStatusControl(StatusControlController.SWIRL_BUBBLE);
                    if (((EntityWaterBubble) entity).getState().equals(EntityWaterBubble.State.BUBBLE))
                        data.addStatusControl(StatusControlController.LOB_BUBBLE);
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

    //Used for a grow animation.
    public static class Grow extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {

            if (entity instanceof EntityWaterBubble && entity.getOwner() != null) {
                World world = entity.world;
                if (world.isRemote) {
                    bubbleSwirl(entity, world);
                }

                ((EntityWaterBubble) entity).setState(EntityWaterBubble.State.BUBBLE);

                //Grows the entity at an exponential rate
                float sizeMult = entity.getAvgSize() <= 1 ? 1.125F * entity.getAvgSize() : (float) Math.pow(entity.getAvgSize(), 1.125);
                entity.setEntitySize(sizeMult * 1.125F);
                //Grows the entity until it's the correct size
                if (entity.getHeight() >= entity.getMaxHeight() && entity.getWidth() >=
                        entity.getMaxWidth()) {
                    return new WaterBubbleBehavior.PlayerControlled();
                }
            } else return null;
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

    //used for a shrink animation
    public static class ShieldShrink extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityWaterBubble && entity.getOwner() != null) {
                World world = entity.world;
                if (world.isRemote) {
                    bubbleSwirl(entity, world);
                }

                //Grows the entity at an exponential rate
                float sizeMult = entity.getAvgSize() >= 1 ? entity.getAvgSize() / 1.125F : (float) Math.pow(entity.getAvgSize(), 1.125);
                entity.setEntitySize(sizeMult / 1.125F);
                //Grows the entity until it's the correct size
                if (entity.getHeight() <= 0.1F && entity.getWidth() <= 0.1F) {
                    ((EntityWaterBubble) entity).setState(EntityWaterBubble.State.SHIELD);
                    return new WaterBubbleBehavior.PlayerControlled();
                }
            } else return null;
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

    //used for a shrink animation
    public static class StreamShrink extends WaterBubbleBehavior {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityWaterBubble && entity.getOwner() != null) {
                World world = entity.world;
                if (world.isRemote) {
                    bubbleSwirl(entity, world);
                }

                //Grows the entity at an exponential rate
                float sizeMult = entity.getAvgSize() >= 1 ? entity.getAvgSize() / 1.25F : (float) Math.pow(entity.getAvgSize(), 1.25);
                entity.setEntitySize(sizeMult / 1.25F);
                //Grows the entity until it's the correct size
                if (entity.getHeight() <= entity.getMaxEntitySize() / 2 && entity.getWidth() <= entity.getMaxEntitySize() / 2) {
                    ((EntityWaterBubble) entity).setState(EntityWaterBubble.State.STREAM);
                    return new WaterBubbleBehavior.PlayerControlled();
                }
            } else return null;
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

    public static class Lobbed extends WaterBubbleBehavior {
        //For when you use the water bubble like a bucket
        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            entity.addVelocity(Vector.DOWN.times(0.8));
            if (entity.getOwner() == null) return this;
            if (entity instanceof EntityWaterBubble) {

                IBlockState state = Blocks.FLOWING_WATER.getDefaultState();

                ((EntityWaterBubble) entity).cleanup();

                if (entity.onCollideWithSolid()) {
                    if (!entity.world.isRemote) {

                        if (((EntityWaterBubble) entity).getDegreesPerSecond() <= 8) {
                            entity.world.setBlockState(entity.getPosition(), state, 3);
                            entity.Explode();

                            if (!((EntityWaterBubble) entity).isSourceBlock()) {
                                AvatarWorldData wd = AvatarWorldData.getDataFromWorld(entity.world);
                                wd.addTemporaryWaterLocation(entity.getPosition());
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
