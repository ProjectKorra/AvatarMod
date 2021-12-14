package com.crowsofwar.avatar.bending.bending.avatar.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.avatar.AbilityElementSpout;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarParticleUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.EFFECT_RADIUS;
import static com.crowsofwar.avatar.bending.bending.Ability.SPEED;
import static com.crowsofwar.avatar.bending.bending.avatar.AbilityElementSpout.MAX_HEIGHT;
import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.StrictMath.toRadians;

public class ElementSpoutHandler extends TickHandler {


    public ElementSpoutHandler(int id) {
        super(id);
    }

    //Add a movement modifier to slow the player, and spawn vfx
    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Chi chi = data.chi();
        World world = ctx.getWorld();
        Bender bender = Bender.get(entity);
        AbilityData abilityData = data.getAbilityData("element_spout");
        AbilityElementSpout spout = (AbilityElementSpout) Abilities.get("element_spout");

        if (bender != null && spout != null) {
            if (bender.consumeChi(spout.getChiCost(abilityData) / 20)) {
                float effectSize = spout.getProperty(EFFECT_RADIUS, abilityData).floatValue();

                effectSize = spout.powerModify(effectSize, abilityData);

                //Particles:
                int height = 0;

                Block block = Blocks.AIR;
                while (block == Blocks.AIR) {
                    IBlockState state = world.getBlockState(entity.getPosition().down(height));
                    block = state.getBlock();
                    //Ensures you hit a solid block
                    if ((!state.isFullBlock() || !state.isFullCube()) && state != Blocks.WATER.getDefaultState()
                            && state != Blocks.FLOWING_WATER.getDefaultState())
                        block = Blocks.AIR;
                    height++;
                }

                //Motion:
                double targetSpeed = spout.getProperty(SPEED, abilityData).floatValue() / 2;
                targetSpeed *= abilityData.getDamageMult() * abilityData.getXpModifier();

                if (entity.moveForward != 0) {
                    if (entity.moveForward < 0) {
                        targetSpeed /= 2;
                    } else {
                        targetSpeed *= 1.3;
                    }
                }

                double posY = entity.onGround ? entity.getEntityBoundingBox().minY + 0.25 : entity.getEntityBoundingBox().minY;
                entity.setPosition(entity.posX, posY, entity.posZ);
                Vector currentVelocity = new Vector(entity.motionX, entity.motionY, entity.motionZ);
                Vector targetVelocity = toRectangular(toRadians(entity.rotationYaw), 0).times(targetSpeed);

                double targetWeight = 0.1;
                currentVelocity = currentVelocity.times(1 - targetWeight);
                targetVelocity = targetVelocity.times(targetWeight);

                double targetSpeedWeight = 0.2;
                double speed = currentVelocity.magnitude() * (1 - targetSpeedWeight)
                        + targetSpeed * targetSpeedWeight;

                Vector newVelocity = currentVelocity.plus(targetVelocity).normalize().times(speed);

                Vector playerMovement = toRectangular(toRadians(entity.rotationYaw - 90),
                        toRadians(entity.rotationPitch)).times(entity.moveStrafing * 0.02);

                newVelocity = newVelocity.plus(playerMovement);

                entity.motionX = newVelocity.x();
                if (entity.onGround)
                    entity.motionY += 0.1;
                else
                    entity.motionY += entity.getLookVec().scale(speed / 2.5).y * 2;
                entity.motionY *= 0.5;

                if (height > spout.getProperty(MAX_HEIGHT, abilityData).intValue())
                    entity.motionY -= 0.05;

                entity.motionZ = newVelocity.z();


                if (!entity.onGround)
                    entity.isAirBorne = true;

                AvatarUtils.afterVelocityAdded(entity);
                if (entity instanceof EntityBender || entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative())
                    abilityData.addBurnout(spout.getBurnOut(abilityData) / 20);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(spout.getExhaustion(abilityData) / 20);

                if (world.isRemote) {
                    //Vortex time??
                    if (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
                        //Use the cube particles
                        AvatarParticleUtils.spawnSpinningVortex(world, entity, entity.getPositionVector().add(0, -height, 0), 5, 360 * height, height,
                                0.05F, effectSize, ParticleBuilder.Type.CUBE, new Vec3d(0.05, 0.05, 0.05), new Vec3d(entity.motionX,
                                        entity.motionY, entity.motionZ), true, 255, 255, 255, 80, 14, BendingStyles.get(Waterbending.ID),
                                false, 0.5F);
                    } else
                        //Use block crack particles
                        //Todo: iteration size to reduce lag
                        if (entity.ticksExisted % 2 == 0)
                            AvatarParticleUtils.spawnSpinningVortex(world, 4, height * 540, height, 0.01,
                                    effectSize, EnumParticleTypes.BLOCK_CRACK, entity.getPositionVector().add(0, -height, 0),
                                    new Vec3d(world.rand.nextGaussian() / 10, world.rand.nextGaussian() / 10, world.rand.nextGaussian() / 10), new Vec3d(entity.motionX, entity.motionY, entity.motionZ),
                                    Block.getStateId(block.getBlockState().getBaseState()));
                }
            }
        }
        //If you're touching a block, it removes the tickhandler
        return world.collidesWithAnyBlock(entity.getEntityBoundingBox().grow(0.025)) && data.getTickHandlerDuration(this) > 30;
    }


    //Just in case the player isn't flying
    @Override
    public void onAdded(BendingContext ctx) {
        super.onAdded(ctx);
    }

    //Stops them from flying
    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);

        if (ctx.getBenderEntity() instanceof EntityPlayer)
            ((EntityPlayer) ctx.getBenderEntity()).capabilities.isFlying = false;
    }
}
