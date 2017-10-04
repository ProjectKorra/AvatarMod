package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.data.ctx.PlayerBender;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;

import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.util.Raytrace.entityRaytrace;
import static com.crowsofwar.avatar.common.util.Raytrace.getReachDistance;
import static com.crowsofwar.avatar.common.util.Raytrace.getTargetBlock;

public abstract class WaterChargeHandler extends TickHandler {
    private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString
            ("dfb6235c-82b6-407e-beaf-a4d8045735a82");


    /**
     * Gets AbilityData to be used for determining lightning strength. This is normally the
     * bender's AbilityData, but in the case of redirection, it is the original bender's
     * AbilityData.
     */
    @Nullable
    protected abstract AbilityData getLightningData(BendingContext ctx);

    @Override
    public boolean tick(BendingContext ctx) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();


        Vector eyePos = Vector.getEyePos(entity);
        Vector look = new Vector(entity.getLookVec());



        if (world.isRemote) {
            return false;
        }

        int duration = data.getTickHandlerDuration(this);

        float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
        applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));

        if (duration >= 40) {

            AbilityData abilityData = getLightningData(ctx);
            if (abilityData == null) {
                return true;
            }

            double speed = abilityData.getLevel() >= 1 ? 20 : 30;
            float damage = abilityData.getLevel() >= 2 ? 8 : 6;
            float size = 1;
            float[] turbulenceValues = { 0.6f, 1.2f };

            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                damage = 15;
                size = 1f;
            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                size = 6.0f;
            }

            fireCannon(world, entity, damage, speed, size, turbulenceValues);

            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);

            world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WATER_AMBIENT,
                    SoundCategory.PLAYERS, 1, 2);


            return true;

        }

        return false;

    }



    private void fireCannon(World world, EntityLivingBase entity, float damage, double speed,
                            float size, float[] turbulenceValues) {

        for (float turbulence : turbulenceValues) {
            Vector playerpos = Vector.getEntityPos(entity);
            Vector look = Vector.getEyePos(entity);
            double maxRange = -1;
            EntityWaterCannon cannon = new EntityWaterCannon(world);
            List<Entity> hitEntities = Raytrace.entityRaytrace(world, playerpos, Vector.getLookRectangular(entity), maxRange);
                if (!hitEntities.isEmpty()) {
                    cannon.setEndPos(Vector.getEntityPos(hitEntities.get(1)));

            }   else {
                    Raytrace.Result hitBlocks = Raytrace.getTargetBlock(entity, maxRange);
                    if (hitBlocks.hitSomething()) {
                        cannon.setEndPos(hitBlocks.getPosPrecise());

                    } else {
                        if (hitEntities.isEmpty() && !hitBlocks.hitSomething()) {


                            cannon.setEndPos(Vector.getLookRectangular(entity));
                        }
                    }
                }







            




            cannon.setOwner(entity);
            cannon.setDamage(10);
            cannon.setSizeMultiplier(size);
            cannon.setMainArc(turbulence == turbulenceValues[0]);

            cannon.setPosition(Vector.getEyePos(entity));

            Vector velocity = Vector.getLookRectangular(entity);
            velocity = velocity.normalize().times(speed);
            cannon.setVelocity(velocity);

            world.spawnEntity(cannon);

        }

    }


    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
                .MOVEMENT_SPEED);

        moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

        moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID,
                "Water charge modifier", multiplier - 1, 1));

    }



}


